package com.example.fundamentosandroiddragonballapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fundamentosandroiddragonballapp.databinding.ItemHeroBinding

//Se encarga de pintar un monton de elementos
class HeroAdapter(val fragment: Clickable): RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {

    private var heroList: List<Hero> = emptyList()
    //Se encarga de pintar un elemento
    class HeroViewHolder(private val binding: ItemHeroBinding, val fragment: Clickable): RecyclerView.ViewHolder(binding.root){
        fun mostrarHero(hero: Hero){
            binding.tvNombre.text = hero.name
            binding.root.setOnClickListener{
                if(hero.vidaActual == 0){
                    Toast.makeText(binding.root.context, "Estoy muerto X.x", Toast.LENGTH_SHORT).show()
                }
                else{
                    fragment.onHeroSelected(hero)
                }
            }
            Glide
                .with(binding.root)
                .load(hero.photo)
                .placeholder(R.drawable.gokususto)
                .into(binding.ivFoto)
            binding.vidaHero.progress = hero.vidaActual
            if(hero.vidaActual == 0){
                binding.root.setBackgroundColor(Color.GRAY)
            }
            else {
                binding.root.setBackgroundColor(Color.parseColor("#FF9800"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        return HeroViewHolder(
            ItemHeroBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            fragment
        )
    }


    override fun getItemCount(): Int {
        return heroList.count()
        //Cuantos elementos son?
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        holder.mostrarHero(heroList[position])
    }

    fun actualizarListaHeros(heroList: List<Hero>){
        this.heroList = heroList
        notifyDataSetChanged()
    }

}