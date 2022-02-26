package com.nagarro.currency.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nagarro.currency.databinding.HistoricalRateItemBinding
import com.nagarro.currency.domain.model.HistoricalExchangeRates

class HistoricalPricesAdapter : RecyclerView.Adapter<HistoricalPricesAdapter.HistoricalPricesViewHolder>() {

    private var historicalPrices = HistoricalExchangeRates()

    @SuppressLint("NotifyDataSetChanged")
    fun setContentList(historicalPrices: HistoricalExchangeRates) {
        this.historicalPrices = historicalPrices
        notifyDataSetChanged()
    }


    class HistoricalPricesViewHolder(val viewHolder: HistoricalRateItemBinding) :
        RecyclerView.ViewHolder(viewHolder.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoricalPricesViewHolder{
        val binding =
            HistoricalRateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoricalPricesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoricalPricesViewHolder, position: Int) {
        holder.viewHolder.fromSymbol = this.historicalPrices.fromCurrency
        holder.viewHolder.toSymbol = this.historicalPrices.toCurrency

        val sortedList = this.historicalPrices.historicalRates.toSortedMap(compareByDescending { it }).toList()
        val date = sortedList[position].first
        val price = sortedList[position].second

        holder.viewHolder.date = date
        holder.viewHolder.price = String.format("%.2f", price.toDouble())

    }

    override fun getItemCount(): Int {
        return this.historicalPrices.historicalRates.size
    }
}