package com.example.fundamentosandroiddragonballapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.fundamentosandroiddragonballapp.databinding.Activity2Binding

interface LoadingPage{
    fun showLoading(b: Boolean)
}

class Activity2 : AppCompatActivity(), LoadingPage {
    private lateinit var binding: Activity2Binding
    private val viewModel: Activity2ViewModel by viewModels()
    companion object{
        val TOKEN = "TOKEN"
        fun lanzarActivity(context: Context, token:String){
            val intent = Intent(context, Activity2::class.java)
            intent.putExtra(TOKEN, token)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = intent.getStringExtra(TOKEN)
        token?.let { mostrarFragmentLista(token)}
        setListeners()

    }

    fun setListeners(){
        binding.button.setOnClickListener {
            viewModel.curarHeros()
        }
    }
    private fun mostrarFragmentLista(token: String){
        supportFragmentManager
            .beginTransaction()
            .add(binding.fFragment.id, FragmentListHeros(token))
            .commit()
    }

    override fun showLoading(b: Boolean) {
        binding.loading.visibility = if(b) View.VISIBLE else View.GONE
    }

    fun mostrarFragmentDetail(){
        println("Paso 3.5> Estoy en el activity y voy a pintar el fragment")
        supportFragmentManager
            .beginTransaction()
            .add(binding.fFragment.id, FragmentDetailHero())
            .addToBackStack(null)
            .commit()
    }

}