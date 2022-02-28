package com.nagarro.currency.presentation.convert

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nagarro.currency.R
import com.nagarro.currency.common.base.BaseFragment
import com.nagarro.currency.databinding.FragmentConvertBinding
import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.presentation.adapter.AvailableCurrenciesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ConvertFragment : BaseFragment<ConvertViewModel, FragmentConvertBinding>() {
    private val conversionViewModel: ConvertViewModel by viewModels()

    override fun getViewModel() = conversionViewModel
    override fun getLayoutRes() = R.layout.fragment_convert

    override fun init() {
        super.init()
        setBindingViewModel()
        conversionViewModel.fetchData()
        setupUIState()
        setupNavigation()
    }

    private fun setupNavigation() {
        binding.gotToDetailBtn.setOnClickListener {
            navigateToDetails()
        }
    }

    private fun navigateToDetails() {
        findNavController().navigate(
            ConvertFragmentDirections.actionConvertFragmentToHistoricalFragment(
                fromCurrency = conversionViewModel.selectedFromCurrency.symbol,
                toCurrency = conversionViewModel.selectedToCurrency.symbol
            )
        )
    }

    private fun setBindingViewModel() {
        binding.viewmodel = conversionViewModel
    }

    override fun initListeners() {

        var fromHasFocus = false
        var toHasFocus = false

        binding.fromNumberEditTxt.setOnFocusChangeListener { _, hasFocus ->
            fromHasFocus = hasFocus

            if (!fromHasFocus)
                conversionViewModel.ensureFromHasValue()

        }
        binding.toNumberEditTxt.setOnFocusChangeListener { _, hasFocus ->
            toHasFocus = hasFocus
            if (!toHasFocus)
                conversionViewModel.ensureToHasValue()

            binding.fromNumberEditTxt.setSelection(binding.fromNumberEditTxt.length())
        }

        binding.fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                conversionViewModel.setFromCurrency(adapterView?.getItemAtPosition(position) as Currency)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                conversionViewModel.setToCurrency(adapterView?.getItemAtPosition(position) as Currency)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        lifecycleScope.launchWhenStarted {
            conversionViewModel.toPrice.collectLatest {
                if (toHasFocus)
                    conversionViewModel.updateFrom()
            }
        }
        lifecycleScope.launchWhenStarted {
            conversionViewModel.fromPrice.collectLatest {
                if (fromHasFocus)
                    conversionViewModel.updateTo()
            }
        }
    }

    private fun setupUIState() {

        lifecycleScope.launchWhenStarted {
            conversionViewModel.availableCurrenciesState.collect {
                if (it.isLoading) {
                    Log.d("ConvertViewModel", "loading...")
                    toggleInputs(false)
                }

                when (it.error) {
                    ConvertUIState.Error.NetworkError ->
                        Toast.makeText(
                            requireContext(),
                            "Please connect to the internet",
                            Toast.LENGTH_SHORT
                        ).show()
                    ConvertUIState.Error.ApiError -> {
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    null -> {}
                }

                it.data?.let { currencyList ->
                    toggleInputs(true)
                    val availableCurrenciesAdapter = AvailableCurrenciesAdapter(currencyList)

                    binding.fromSpinner.adapter = availableCurrenciesAdapter
                    binding.toSpinner.adapter = availableCurrenciesAdapter
                }
            }
        }
    }

    private fun toggleInputs(enabled: Boolean) {
        toggleEditText(binding.fromNumberEditTxt, enabled)
        toggleEditText(binding.toNumberEditTxt, enabled)

        binding.swapBtn.isEnabled = enabled
        binding.gotToDetailBtn.isEnabled = enabled
    }

    private fun toggleEditText(editText: EditText, enable: Boolean) {
        editText.isEnabled = enable
    }


}