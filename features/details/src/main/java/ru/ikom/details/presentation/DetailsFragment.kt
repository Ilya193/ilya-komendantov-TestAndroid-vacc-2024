package ru.ikom.details.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.launch
import ru.ikom.common.BaseFragment
import ru.ikom.details.R
import ru.ikom.details.databinding.FragmentDetailsBinding
import ru.ikom.details.di.DetailsComponentViewModel
import javax.inject.Inject

class DetailsFragment : BaseFragment<FragmentDetailsBinding, DetailsViewModel>() {

    @Inject
    lateinit var viewModelFactory: DetailsViewModel.Factory.Factory

    override val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory.create(data))[DetailsViewModel::class.java]
    }

    private var data = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ViewModelProvider(this).get<DetailsComponentViewModel>().detailsComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = requireArguments().getString(KEY_DATA, "")
    }

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentDetailsBinding =
        FragmentDetailsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).collect { state ->
                state.product?.let {
                    binding.thumbnail.load(it.thumbnail)
                    binding.tvTitle.text = it.title
                    binding.tvDescription.text = it.description
                    binding.tvPrice.text = it.price.toRubles() + getString(R.string.currency)
                }
            }
        }
    }

    companion object {
        private const val KEY_DATA = "KEY_DATA"

        fun newInstance(data: String) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_DATA, data)
                }
            }
    }
}