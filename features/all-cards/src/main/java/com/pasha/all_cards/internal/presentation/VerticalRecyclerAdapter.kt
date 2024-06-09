package com.pasha.all_cards.internal.presentation

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pasha.all_cards.databinding.ItemCardLayoutBinding
import com.pasha.all_cards.internal.domain.models.Card

class AllCardsRecyclerAdapter(
    private val onCardClick: (Card) -> Unit,
    private val onDeleteClick: (Card) -> Unit
) : RecyclerView.Adapter<AllCardsRecyclerAdapter.CardViewHolder>() {
    private val cards = mutableListOf<Card>()

    class CardViewHolder(
        private val binding: ItemCardLayoutBinding,
        private val onCardClick: (Card) -> Unit,
        private val onDeleteClick: (Card) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card) {
            binding.tvCardName.text = card.cardName
            binding.tvCreateDate.text = card.creationTime

            binding.root.setOnClickListener {
                onCardClick(card)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCardLayoutBinding.inflate(layoutInflater, parent, false)
        return CardViewHolder(binding, onCardClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount() = cards.size


    fun setCards(newCards: List<Card>) {
        val diffCallback = CardDiffCallback(cards, newCards)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        cards.clear()
        cards.addAll(newCards)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addCard(card: Card) {
        val newCards = ArrayList(cards)
        newCards.add(card)
        setCards(newCards)
    }

    fun removeCard(card: Card) {
        val newCards = ArrayList(cards)
        newCards.remove(card)
        setCards(newCards)
    }

    class CardDiffCallback(
        private val oldList: List<Card>,
        private val newList: List<Card>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].cardId == newList[newItemPosition].cardId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

class SpacingItemDecoration(private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = verticalSpaceHeight
    }
}
