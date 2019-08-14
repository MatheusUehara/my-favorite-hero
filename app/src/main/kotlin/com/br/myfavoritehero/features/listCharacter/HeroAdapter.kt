package com.br.myfavoritehero.features.listCharacter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.br.myfavoritehero.R
import com.br.myfavoritehero.data.interfaces.HeroEventListener
import com.br.myfavoritehero.data.models.Hero
import com.br.myfavoritehero.util.SharedPreferencesHelper
import com.br.myfavoritehero.util.getLargeLandscapeThumbnail
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.hero_item.view.favoriteIcon
import kotlinx.android.synthetic.main.hero_item.view.heroCardImage
import kotlinx.android.synthetic.main.hero_item.view.heroName

class HeroAdapter(
    private val elements: ArrayList<Hero>,
    private val listener: HeroEventListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoading = false
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hero_item, parent, false)
        context = view.context
        return HeroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var favorited = false
        holder as HeroViewHolder
        val hero = elements[position]
        Picasso.get().load(hero.thumbnail.path.getLargeLandscapeThumbnail()).into(holder.mLinearLayout.heroCardImage)
        holder.mLinearLayout.heroName.text = hero.name
        holder.mLinearLayout.setOnClickListener { listener.onHeroClicked(hero) }
        context?.let {
            favorited = SharedPreferencesHelper.isFavorited(it, hero.id)
            switchVisibility(favorited, holder)
        }

        holder.mLinearLayout.favoriteIcon.setOnClickListener {
            favorited = if (favorited) {
                SharedPreferencesHelper.setFavorite(it.context, hero.id, false)
                Snackbar.make(holder.mLinearLayout, R.string.unFavorited, Snackbar.LENGTH_SHORT).show()
                !favorited
            } else {
                holder.mLinearLayout.favoriteIcon.setImageResource(R.drawable.un_favorite)
                SharedPreferencesHelper.setFavorite(it.context, hero.id)
                Snackbar.make(holder.mLinearLayout, R.string.favorited, Snackbar.LENGTH_SHORT).show()
                !favorited
            }
            switchVisibility(favorited, holder)
        }
    }

    override fun getItemCount(): Int {
        //return if (isLoading) elements.size + 1 else elements.size
        return elements.size
    }

    fun startLoading() {
        isLoading = true
        notifyDataSetChanged()
    }

    fun stopLoading() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun isLoading(): Boolean {
        return isLoading
    }

    fun updateUI(elements: ArrayList<Hero>) {
        this.elements.addAll(elements)
        this.notifyDataSetChanged()
    }

    private fun switchVisibility(favorited: Boolean, holder: HeroViewHolder) {
        if (favorited) {
            holder.mLinearLayout.favoriteIcon.setImageResource(R.drawable.un_favorite)
        } else {
            holder.mLinearLayout.favoriteIcon.setImageResource(R.drawable.favorite)
        }
    }
}