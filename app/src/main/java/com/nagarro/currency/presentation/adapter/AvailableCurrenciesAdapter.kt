package com.nagarro.currency.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.nagarro.currency.R
import com.nagarro.currency.databinding.CurrencySymbolListItemBinding
import com.nagarro.currency.domain.model.Currency

class AvailableCurrenciesAdapter(private val currencies: List<Currency>)
    : BaseAdapter() {

    override fun getCount(): Int = currencies.size
    override fun getItem(p0: Int): Currency = currencies[p0]
    override fun getItemId(p0: Int): Long = 1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemBinding: CurrencySymbolListItemBinding
        val viewHolder: CurrencyItemHolder

        if(convertView == null) {
            itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent?.context),
                R.layout.currency_symbol_list_item,
                parent,
                false
            )

            viewHolder = CurrencyItemHolder(itemBinding, itemBinding.root)
            viewHolder.view.tag = viewHolder
        } else {
            viewHolder = convertView.tag as CurrencyItemHolder
        }

        viewHolder.binding.currencySymbolTv.text = currencies[position].symbol
        return viewHolder.view
    }

    private class CurrencyItemHolder(
        val binding: CurrencySymbolListItemBinding,
        var view: View)

}