package com.example.fundamentosandroiddragonballapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivityViewModel: ViewModel() {
    private val BASE_URL = "https://dragonball.keepcoding.education/api/"

    private var token: String = ""
    private val _uiState = MutableStateFlow<State>(State.Idle())
    val uiState: StateFlow<State> = _uiState

    sealed class State{
        class Idle: State()
        class Error(val message: String): State()
        class Loading: State()
        class SuccessLogin(val token: String): State()
    }

    fun launchLogin(usuario: String, contraseña: String){
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = State.Loading()
            val client = OkHttpClient()
            val url = "${BASE_URL}auth/login"
            val credentials = Credentials.basic(usuario, contraseña)
            val formBody= FormBody.Builder()
                .build()
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", credentials)
                .post(formBody)
                .build()
            val call = client.newCall(request)
            val response = call.execute()
            _uiState.value = if (response.isSuccessful)
                response.body?.let{
                    token = it.string()
                    println(token)
                    State.SuccessLogin(token)
                } ?: State.Error("Empty token")
            else
                State.Error(response.message)
        }
    }
}