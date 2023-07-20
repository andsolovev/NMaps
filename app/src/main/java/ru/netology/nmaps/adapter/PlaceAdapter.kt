package ru.netology.nmaps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmaps.R
import ru.netology.nmaps.databinding.FragmentPlaceBinding
import ru.netology.nmaps.db.dto.Place

class PlaceAdapter(
    private val listener: Listener,
) : ListAdapter<Place, PlaceAdapter.PlaceViewHolder>(DiffCallback) {

    interface Listener {
        fun onClick(place: Place)
        fun onDelete(place: Place)
        fun onEdit(place: Place)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = FragmentPlaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val holder = PlaceViewHolder(binding)

        with(binding) {
            root.setOnClickListener {
                val place = getItem(holder.adapterPosition)
                listener.onClick(place)
            }
            menu.setOnClickListener {
                PopupMenu(root.context, it).apply {
                    inflate(R.menu.place_menu)

                    setOnMenuItemClickListener { item ->
                        val place = getItem(holder.adapterPosition)
                        when (item.itemId) {
                            R.id.delete -> {
                                listener.onDelete(place)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(place)
                                true
                            }
                            else -> false
                        }
                    }

                    show()
                }
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaceViewHolder(
        private val binding: FragmentPlaceBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: Place) {
            with(binding) {
                title.text = place.title
                description.text = place.description
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean =
            oldItem == newItem
    }
}