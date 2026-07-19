package com.tech.cropify.model.hybrid

data class HybridAdvisoryResponse(
    val best_crop: String?= null,
    val note: String,
    val recommended_crops: List<Any>?= emptyList()
)