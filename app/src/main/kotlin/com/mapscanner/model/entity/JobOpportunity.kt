// model/entity/JobOpportunity.kt
package com.mapscanner.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime

@Document(collection = "job_opportunities")
data class JobOpportunity(
    @Id
    val id: String? = null,
    
    @Indexed
    val businessId: String,
    
    val title: String,
    val description: String,
    val requirements: List<String> = emptyList(),
    val salary: String? = null,
    val jobType: String, // Full-time, Part-time, Contract, etc.
    
    @Indexed
    val sourceUrl: String,
    
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    
    @Indexed
    val postedDate: LocalDateTime? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now()
)
