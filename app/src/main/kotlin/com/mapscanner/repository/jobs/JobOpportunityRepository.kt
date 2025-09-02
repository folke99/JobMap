// repository/jobs/JobOpportunityRepository.kt
package com.mapscanner.repository.jobs

import com.mapscanner.model.entity.JobOpportunity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface JobOpportunityRepository : MongoRepository<JobOpportunity, String> {
    
    fun findByBusinessId(businessId: String): List<JobOpportunity>
    
    @Query("{ 'title': { '\$regex': ?0, '\$options': 'i' } }")
    fun findByTitleContainingIgnoreCase(title: String): List<JobOpportunity>
    
    fun findByJobType(jobType: String): List<JobOpportunity>
    
    fun findByPostedDateAfter(date: LocalDateTime): List<JobOpportunity>
    
    @Query("{ 'businessId': { '\$in': ?0 } }")
    fun findByBusinessIds(businessIds: List<String>): List<JobOpportunity>
}