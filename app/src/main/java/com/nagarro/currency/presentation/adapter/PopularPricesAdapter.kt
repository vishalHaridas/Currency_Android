package com.nagarro.currency.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nagarro.currency.databinding.HistoricalRateItemBinding
import com.nagarro.currency.databinding.PopularRateItemBinding
import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.domain.model.ExchangeRate

class PopularPricesAdapter : RecyclerView.Adapter<PopularPricesAdapter.HistoricalPricesViewHolder>() {

    private var popularPrices = listOf<ExchangeRate>()

    @SuppressLint("NotifyDataSetChanged")
    fun setContentList(popularPrices: List<ExchangeRate>) {
        this.popularPrices = popularPrices
        notifyDataSetChanged()
    }


    class HistoricalPricesViewHolder(val viewHolder: PopularRateItemBinding) :
        RecyclerView.ViewHolder(viewHolder.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoricalPricesViewHolder{
        val binding =
            PopularRateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoricalPricesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoricalPricesViewHolder, position: Int) {
        holder.viewHolder.fromSymbol = this.popularPrices[position].from
        holder.viewHolder.toSymbol = this.popularPrices[position].to

        val price = this.popularPrices[position].rate

        holder.viewHolder.price = String.format("%.2f", price)

    }

    override fun getItemCount(): Int {
        return this.popularPrices.size
    }
}