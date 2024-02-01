package com.example.fundamentosandroiddragonballapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.fundamentosandroiddragonballapp.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel:MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        setObservers()
    }

    fun setListeners(){
        binding.loginButton.setOnClickListener {
            viewModel.launchLogin(binding.textEmailAddress.text.toString(), binding.textPassword.text.toString())
        }
    }

    fun setObservers(){
        lifecycleScope.launch(Dispatchers.Main){
            viewModel.uiState.collect{ state ->
                when(state) {
                    is MainActivityViewModel.State.Idle -> idle()
                    is MainActivityViewModel.State.Error -> error(state.message)
                    is MainActivityViewModel.State.Loading -> loading()
                    is MainActivityViewModel.State.SuccessLogin -> successLogin(state.token)
                }
            }
        }
    }
    private fun showLoading(b: Boolean) {
        binding.loading.visibility = if(b) View.VISIBLE else View.INVISIBLE
    }
    private fun idle(){
        showLoading(false)
    }
    private fun error(message: String){
        showLoading(false)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun loading(){
        showLoading(true)
    }

    private fun successLogin(token: String){
        showLoading(false)
        Activity2.lanzarActivity(this, token)
    }
}