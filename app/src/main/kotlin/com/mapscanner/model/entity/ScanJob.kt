// model/entity/ScanJob.kt
package com.mapscanner.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime

@Document(collection = "scan_jobs")
data class ScanJob(
    @Id
    val id: String? = null,
    
    @Indexed
    val status: ScanStatus,
    
    val location: Location,
    val radius: Int, // in meters
    val businessTypes: List<String> = emptyList(),
    
    val progress: Int = 0, // percentage
    val totalBusinessesFound: Int = 0,
    val totalJobsFound: Int = 0,
    
    val errorMessage: String? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
)

enum class ScanStatus {
    PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
}
