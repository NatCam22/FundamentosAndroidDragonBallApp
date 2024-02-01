package com.example.fundamentosandroiddragonballapp

data class Hero (
    val name: String,
    val photo: String,
    val description: String,
    val id: String,
    val vidaMaxima: Int,
    var vidaActual: Int
)