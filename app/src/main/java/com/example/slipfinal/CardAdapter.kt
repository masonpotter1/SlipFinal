package com.example.slipfinal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slipfinal.databinding.ContactCardBinding


class CardAdapter(private val contact: List<Contact>,private val click: ContactClick)
    : RecyclerView.Adapter<CardViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ContactCardBinding.inflate(from,parent,false)
        return CardViewHolder(binding,click)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindCard(contact[position])
    }

    override fun getItemCount(): Int = contact.size


}