
// model/dto/ScanRequestDto.kt
package com.mapscanner.model.dto

data class ScanRequestDto(
    val latitude: Double,
    val longitude: Double,
    val radius: Int = 5000, // default 5km
    val businessTypes: List<String> = emptyList(),
    val includeJobScraping: Boolean = true
)

data class ScanResponseDto(
    val scanId: String,
    val status: String,
    val message: String
)

data class ScanStatusDto(
    val scanId: String,
    val status: String,
    val progress: Int,
    val businessesFound: Int,
    val jobsFound: Int,
    val estimatedCompletion: String? = null
)