package com.example.slipfinal

import androidx.recyclerview.widget.RecyclerView
import com.example.slipfinal.databinding.ContactCardBinding

class CardViewHolder(
    private val cardBinding: ContactCardBinding,
    private val clickListener:ContactClick
) : RecyclerView.ViewHolder(cardBinding.root)
{
fun bindCard(contact: Contact){
    cardBinding.uxCardName.text= contact.Name;
    cardBinding.uxCardUniqueKey.text=contact.id.toString()
    cardBinding.uxCardCardView.setOnClickListener { clickListener.onClick(contact) }
}

}