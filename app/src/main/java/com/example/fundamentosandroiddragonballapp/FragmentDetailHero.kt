package com.example.fundamentosandroiddragonballapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.fundamentosandroiddragonballapp.databinding.FragmentDetailHeroBinding
import kotlinx.coroutines.launch

class FragmentDetailHero() : Fragment() {

    private lateinit var binding: FragmentDetailHeroBinding
    private val viewModel: Activity2ViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailHeroBinding.inflate(inflater)
        println("Paso 3.9> Se creo el fragment")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        println("Paso 4> Se fijaron los observers")
    }

    private fun setObservers(){
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.uiStateDetail.collect{ stateDetails ->
                when(stateDetails) {
                    is Activity2ViewModel.StateDetail.Idle -> idle()
                    is Activity2ViewModel.StateDetail.Error -> lanzarError(stateDetails.message)
                    is Activity2ViewModel.StateDetail.OnHeroSelected -> {
                        heroSelected(stateDetails.hero)
                    }
                    is Activity2ViewModel.StateDetail.OnHeroCured -> heroChanged(stateDetails.hero)
                    is Activity2ViewModel.StateDetail.OnHeroInjured -> heroChanged(stateDetails.hero)
                    is Activity2ViewModel.StateDetail.OnHeroDied -> heroDied()
                }
            }
        }
    }

    private fun lanzarError(mensaje: String){
        println("Se lanzo un error $mensaje")
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }

    private fun heroDied() {
        parentFragmentManager.popBackStack()
    }

    private fun heroChanged(hero: Hero) {
        println("Si cambio mi heroe!")
        binding.pbVidaHero.progress = hero.vidaActual
    }

    private fun heroSelected(hero: Hero) {
        println("Por fin entro a modificarse el fragment!")
        binding.nombreHero.text = hero.name
        binding.pbVidaHero.progress = hero.vidaActual
        binding.descripcionHero.text = hero.description
        Glide
            .with(binding.root)
            .load(hero.photo)
            .placeholder(R.drawable.gokususto)
            .into(binding.ivFoto)
    }

    private fun idle() {
        println("Funcionara este otro estate?")
    }

    private fun setListeners(){
        binding.ibCura.setOnClickListener {
            viewModel.curarHero()
            Toast.makeText(requireContext(), "Gracias! me curaste :)", Toast.LENGTH_SHORT).show()}

        binding.ibHerida.setOnClickListener { viewModel.pegarHero()
            Toast.makeText(requireContext(), "Auch! ¿Por qué me pegas?", Toast.LENGTH_SHORT).show()}
    }

}