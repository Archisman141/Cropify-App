package com.tech.cropify.model.prediction

data class Optimization(
    val optimized_total_production: Double,
    val optimized_yield_ton_per_hectare: Double,
    val recommended_fert_kg_ha: Double,
    val recommended_pest_kg_ha: Double
)