package com.tech.cropify.model.prediction

data class Prediction(
    val ai_advice: String,
    val optimization: Optimization,
    val prediction: PredictionX
)