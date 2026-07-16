package com.tech.cropify.model.prediction

data class PredictionBody (
    val state: String,
    val crop: String,
    val area: String,
    val fertilizer: String,
    val pesticide: String,
    val rainfall: String
)