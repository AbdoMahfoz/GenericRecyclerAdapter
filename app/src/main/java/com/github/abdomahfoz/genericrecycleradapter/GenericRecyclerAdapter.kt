package com.github.abdomahfoz.genericrecycleradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.io.InvalidObjectException

class ClickListener<T>(private val clickListener: (type: T) -> Unit) {
    fun onClick(type: T) {
        clickListener(type)
    }
}
interface ViewHolder<E> {
    fun bind(item: E)
}

interface GenericRecyclerEntity {
    val id: String
    override fun equals(other: Any?): Boolean
}

class GenericViewHolder<E : GenericRecyclerEntity>(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root), ViewHolder<E> {

    override fun bind(item: E) {
        try {
            binding::class.java.declaredMethods.first { it.name == "setData" }.invoke(binding, item)
        } catch (e: NoSuchElementException) {
            throw InvalidObjectException(
                "Data binding object ${binding::class.simpleName} used in GenericRecyclerView " + "doesn't have a variable of name \"data\".\n" + "This variable is what the view holder binds ${item::class.simpleName} into."
            )
        }
    }
}

class GenericDiff<T : GenericRecyclerEntity> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        if (oldItem::class.java.isAssignableFrom(newItem::class.java)) {
            return oldItem.id == newItem.id
        }
        return false
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        if (oldItem::class.java.isAssignableFrom(newItem::class.java)) {
            return oldItem == newItem
        }
        return false
    }
}


class GenericRecyclerAdapter<E : GenericRecyclerEntity, VH>(
    private val viewHolderFactory: (ViewGroup, Int) -> VH, private val itemViewType: (E) -> Int
) : ListAdapter<E, VH>(
    GenericDiff<E>()
) where VH : ViewHolder<E>, VH : RecyclerView.ViewHolder {

    override fun getItemViewType(position: Int): Int {
        return itemViewType(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        viewHolderFactory(parent, viewType)

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        inline fun <reified E : GenericRecyclerEntity, B : ViewDataBinding> create(
            @LayoutRes layout: Int, noinline onClick: ((type: E) -> Unit)? = null
        ) = GenericRecyclerAdapter({ parent: ViewGroup, _: Int ->
            val binding: B = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), layout, parent, false
            )
            if (onClick != null) {
                val listener = ClickListener(onClick)
                binding::class.java.declaredMethods.first { it.name == "setListener" }
                    .invoke(binding, listener)
            }

            GenericViewHolder<E>(binding)
        }, { 0 })
    }
}
