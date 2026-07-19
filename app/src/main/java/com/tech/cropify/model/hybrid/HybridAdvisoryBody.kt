package com.tech.cropify.model.hybrid

data class HybridAdvisoryBody(
    val K: Int,
    val N: Int,
    val P: Int,
    val area: Int,
    val fertilizer: Int,
    val humidity: Int,
    val pesticide: Int,
    val ph: Long,
    val rainfall: Int,
    val state: String,
    val temperature: Int,
    val top_k_return: Int,
    val top_n_candidates: Int
)