package com.example.fundamentosandroiddragonballapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import okhttp3.Credentials
import okhttp3.Dispatcher
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class Activity2ViewModel : ViewModel(){
    private val BASE_URL = "https://dragonball.keepcoding.education/api/"

    private var heroList = mutableListOf<Hero>()
    private var heroSelected: Hero? = null
    private val _uiState = MutableStateFlow<State>(State.Idle())
    val uiState: StateFlow<State> = _uiState

    private val _uiStateDetail = MutableStateFlow<StateDetail>(StateDetail.Error("Si esta leyendo el state pero no cuando lo fijo en la funcion"))
    val uiStateDetail: StateFlow<StateDetail> = _uiStateDetail
    ///States: Sealed class


    sealed class StateDetail{
        class Idle: StateDetail()
        class Error(val message: String): StateDetail()
        class OnHeroSelected(val hero: Hero): StateDetail()
        class OnHeroInjured(val hero: Hero): StateDetail()
        class OnHeroCured(val hero: Hero): StateDetail()
        class OnHeroDied: StateDetail()
    }

    sealed class State{
        class Idle: State()
        class Error(val message: String): State()
        class Loading: State()
        class SuccessGetHeros(val heroList: List<Hero>): State()
        class HeroSelected(): State()
        class OnHerosUpdated(val heroList: List<Hero>) : State()
    }

    fun launchGetHeroes(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = State.Loading()
            val client = OkHttpClient()
            val url = "${BASE_URL}heros/all"
            val formBody= FormBody.Builder()
                .add("name", "") //Body for the POST request
                .build()
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post(formBody)
                .build()
            val call = client.newCall(request)
            val response = call.execute()
            _uiState.value = if (response.isSuccessful)
                response.body?.let{
                    val herosDtoList: Array<HeroDto> = Gson().fromJson(it.string(), Array<HeroDto>::class.java)
                    val herosList: List<Hero> = herosDtoList.map{
                        Hero(it.name,  it.photo, it.description, it.id, 100, 10)
                    }
                    changeHeroes(herosList)
                    State.SuccessGetHeros(herosList)
                } ?: State.Error("Empty token")
            else
                State.Error(response.message)
        }
    }

    fun changeHeroes(heros: List<Hero>) {
        heroList.clear()
        heroList.addAll(heros)
        _uiState.value = State.OnHerosUpdated(heros)
    }

    fun onHeroSelected(hero: Hero){
        heroSelected = hero
        println("Paso 2> Tengo al heroe en el viewmodel ahora le voy a decir al fragmento que seleccionaron heroe")
        _uiState.value = State.HeroSelected()
        println("Paso 5> se creo bien el fragmento")
        _uiState.value = State.Idle()
        println("Paso 6> Ahora modificamos el stateDetail! Le decimos que se cambie a YA SELECCIONAMOS HEROE")
        _uiStateDetail.value = StateDetail.OnHeroSelected(hero)
    }

    fun curarHero(){
        if(heroList.isEmpty()){
            _uiStateDetail.value = StateDetail.Error("I dont know any heros")
        }else{
            println("El heroe que esta seleccionado es: $heroSelected")
            heroSelected?.let {
                it.vidaActual = (if (it.vidaActual + 20 <= it.vidaMaxima) it.vidaActual + 20 else it.vidaMaxima)
                _uiStateDetail.value = StateDetail.OnHeroCured(it)
            }
            val pos = heroList.indexOf(heroSelected)
            heroList.find{it == heroSelected}?.let {
                heroList[pos] = it
                _uiState.value = State.OnHerosUpdated(heroList)
            }

        }
    }

    fun pegarHero(){
        if(heroList.isEmpty()){
            _uiStateDetail.value = StateDetail.Error("I dont know any heros")
        }else{
            println("El heroe que esta seleccionado es: $heroSelected")
            heroSelected?.let {
                val damage = Random.nextInt(1,7) * 10
                println(damage)
                it.vidaActual = (if (it.vidaActual - damage >= 0) it.vidaActual - damage else 0)
                println(it.vidaActual)
                if (it.vidaActual == 0){
                    _uiStateDetail.value = StateDetail.OnHeroDied()
                }
                else{
                _uiStateDetail.value = StateDetail.OnHeroInjured(it)
                }
            }
            val pos = heroList.indexOf(heroSelected)
            heroList.find{it == heroSelected}?.let {
                heroList[pos] = it
                _uiState.value = State.OnHerosUpdated(heroList)
            }
        }
    }

    fun curarHeros(){
        if(heroList.isEmpty()){
            _uiStateDetail.value = StateDetail.Error("I dont know any heros")
        }else{
            heroSelected?.let {
                it.vidaActual = it.vidaMaxima
                _uiStateDetail.value = StateDetail.OnHeroCured(it)
            }
            val herosCured = heroList.map {
                it.vidaActual = it.vidaMaxima
                it
            }
            _uiState.value = State.OnHerosUpdated(herosCured)
        }
    }
}