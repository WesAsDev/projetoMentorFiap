package com.example.myapplication.Model


data class EnderecoModel(
    val cep: String? = null,
    val logradouro: String? = null,
    val complemento: String? = null,
    val numero: String? = null,
    val estado: String? = null,
    val cidade: String? = null,
    val longi: String? = null,
    val lat: String? = null,
) {
}