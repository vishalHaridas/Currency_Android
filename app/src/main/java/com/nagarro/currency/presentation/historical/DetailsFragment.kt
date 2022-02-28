package com.nagarro.currency.presentation.historical

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.nagarro.currency.R
import com.nagarro.currency.common.base.BaseFragment
import com.nagarro.currency.databinding.FragmentRateHistoryBinding
import com.nagarro.currency.domain.model.ExchangeRateRequest
import com.nagarro.currency.presentation.adapter.HistoricalPricesAdapter
import com.nagarro.currency.presentation.adapter.PopularPricesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class DetailsFragment : BaseFragment<DetailsViewModel, FragmentRateHistoryBinding>() {

    private val historicalViewModel: DetailsViewModel by viewModels()
    private val navigationArgs: DetailsFragmentArgs by navArgs()
    override fun getViewModel(): DetailsViewModel = historicalViewModel
    override fun getLayoutRes(): Int = R.layout.fragment_rate_history

    private val historicalPricesAdapter = HistoricalPricesAdapter()
    private val popularPricesAdapter = PopularPricesAdapter()

    override fun init() {
        super.init()
        setupBindingViewModel()
        setupRecyclerView()
        setupUiStates()
    }

    private fun setupRecyclerView() {
        binding.pricesRv.adapter = historicalPricesAdapter
        binding.popularPricesRv.adapter = popularPricesAdapter

        lifecycleScope.launchWhenStarted {
            historicalViewModel.popularExchangeRates.collect { popularExchangeRates ->
                popularPricesAdapter.setContentList(popularExchangeRates)
            }
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
                navigationArgs.fromCurrency.symbol,
                navigationArgs.toCurrency.symbol,
            ))
        historicalViewModel.calculatePopularCurrencies(
            navigationArgs.fromCurrency,
            navigationArgs.topCurrencies.toList()
        )
    }

}