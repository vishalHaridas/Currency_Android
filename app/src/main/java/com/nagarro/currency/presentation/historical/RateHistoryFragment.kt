package com.nagarro.currency.presentation.historical

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.nagarro.currency.R
import com.nagarro.currency.common.base.BaseFragment
import com.nagarro.currency.databinding.FragmentRateHistoryBinding
import com.nagarro.currency.domain.model.ExchangeRateRequest
import com.nagarro.currency.presentation.adapter.HistoricalPricesAdapter
import com.nagarro.currency.presentation.convert.ConvertUIState
import com.nagarro.currency.presentation.convert.ConvertViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class RateHistoryFragment : BaseFragment<HistoricalDataViewModel, FragmentRateHistoryBinding>() {

    private val historicalViewModel: HistoricalDataViewModel by viewModels()
    private val navigationArgs: RateHistoryFragmentArgs by navArgs()
    override fun getViewModel(): HistoricalDataViewModel = historicalViewModel
    override fun getLayoutRes(): Int = R.layout.fragment_rate_history

    private val historicalPricesAdapter = HistoricalPricesAdapter()

    override fun init() {
        super.init()
        setupBindingViewModel()
        setupRecyclerView()
        setupUiStates()
    }

    private fun setupRecyclerView() {
        binding.pricesRv.apply {
            adapter = historicalPricesAdapter
        }
    }

    private fun setupUiStates() {
        lifecycleScope.launchWhenStarted {
            historicalViewModel.historicalDataUIState.collect {
                if (it.isLoading){
                    Log.d("ConvertViewModel","loading...")
                }

                when (it.error) {
                    HistoricalRatesUIState.Error.NetworkError ->
                        Toast.makeText(
                            requireContext(),
                            "Please connect to the internet",
                            Toast.LENGTH_SHORT
                        ).show()
                    HistoricalRatesUIState.Error.ApiError ->
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong!",
                            Toast.LENGTH_SHORT
                        ).show()
                    null -> {}
                }

                it.data?.let { historicalRates ->
                    historicalPricesAdapter.setContentList(historicalRates)
                }
            }
        }
    }

    private fun setupBindingViewModel() {
        binding.viewmodel = historicalViewModel
    }


    override fun initListeners() {
        historicalViewModel
            .fetchData(ExchangeRateRequest(
                navigationArgs.fromCurrency,
                navigationArgs.toCurrency,
            ))
    }

}