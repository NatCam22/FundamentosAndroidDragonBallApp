package com.example.fundamentosandroiddragonballapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fundamentosandroiddragonballapp.databinding.FragmentListHerosBinding
import kotlinx.coroutines.launch

interface Clickable{
    fun onHeroSelected(hero: Hero)
}

class FragmentListHeros(val token: String, val activity2: LoadingPage) : Fragment(), Clickable {

    private lateinit var binding: FragmentListHerosBinding
    private val heroAdapter = HeroAdapter(this)
    private val viewModel:Activity2ViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListHerosBinding.inflate(inflater)
        setObservers()
        viewModel.launchGetHeroes(token)
        configurarRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setObservers()
    }
    fun configurarRecyclerView(){
        binding.rvHeros.layoutManager = LinearLayoutManager(binding.root.context)
        binding.rvHeros.adapter = heroAdapter
    }

    fun rellenarLista(heroList: List<Hero>){
        heroAdapter.actualizarListaHeros(heroList)
    }

    private fun setObservers(){
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.uiState.collect{ state ->
                when(state) {
                    is Activity2ViewModel.State.HeroSelected -> showHero()
                    is Activity2ViewModel.State.Idle -> idle()
                    is Activity2ViewModel.State.Error -> error(state.message)
                    is Activity2ViewModel.State.Loading -> loading()
                    is Activity2ViewModel.State.SuccessGetHeros -> successGetHeros(state.heroList)
                    is Activity2ViewModel.State.OnHerosUpdated -> successGetHeros(state.heroList)
                }
            }
        }
    }

    override fun onHeroSelected(hero: Hero){
        println("paso 1> Se selecciono al heroe, lo tenemos y se lo pasamos al viewmodel")
        viewModel.onHeroSelected(hero)
    }

    private fun showHero(){
        println("Paso3> Registrado el cambio del estado le digo al activity que pinte el fragmento")
        (activity as? Activity2)?.mostrarFragmentDetail()
    }
    private fun showLoading(b:Boolean){
        (activity as? LoadingPage)?.showLoading(b)
    }
    private fun idle(){
        showLoading(false)
    }
    private fun error(message: String){
        showLoading(false)
        Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
    }
    private fun loading(){
        showLoading(true)
    }

    private fun successGetHeros(heroList: List<Hero>){
        showLoading(false)
        rellenarLista(heroList)
    }
}