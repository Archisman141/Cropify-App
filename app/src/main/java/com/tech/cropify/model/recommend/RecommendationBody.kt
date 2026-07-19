package com.tech.cropify.model.recommend

data class RecommendationBody(
    val K: String,
    val N: String,
    val P: String,
    val ph: String,
    val temperature: String,
    val humidity: String,
    val rainfall: String
)
