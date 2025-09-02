
// service/analytics/AnalyticsService.kt
package com.mapscanner.service.analytics

import com.mapscanner.repository.business.BusinessRepository
import com.mapscanner.repository.jobs.JobOpportunityRepository
import com.mapscanner.repository.jobs.ScanJobRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

data class DashboardStats(
    val totalBusinesses: Long,
    val totalJobs: Long,
    val totalScans: Long,
    val recentScans: Long,
    val topBusinessTypes: Map<String, Long>,
    val topJobTypes: Map<String, Long>,
    val recentActivity: String
)

@Service
class AnalyticsService(
    private val businessRepository: BusinessRepository,
    private val jobRepository: JobOpportunityRepository,
    private val scanRepository: ScanJobRepository
) {

    fun getDashboardStats(): DashboardStats {
        val totalBusinesses = businessRepository.count()
        val totalJobs = jobRepository.count()
        val totalScans = scanRepository.count()
        
        val recentCutoff = LocalDateTime.now().minusDays(7)
        val recentScans = scanRepository.findAll().count { 
            it.createdAt.isAfter(recentCutoff) 
        }.toLong()

        // Get top business types
        val businesses = businessRepository.findAll()
        val topBusinessTypes = businesses.groupBy { it.businessType }
            .mapValues { it.value.size.toLong() }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .toMap()

        // Get top job types
        val jobs = jobRepository.findAll()
        val topJobTypes = jobs.groupBy { it.jobType }
            .mapValues { it.value.size.toLong() }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .toMap()

        val recentActivity = if (recentScans > 0) {
            "Active - $recentScans scans in past 7 days"
        } else {
            "Quiet - No recent scanning activity"
        }

        return DashboardStats(
            totalBusinesses = totalBusinesses,
            totalJobs = totalJobs,
            totalScans = totalScans,
            recentScans = recentScans,
            topBusinessTypes = topBusinessTypes,
            topJobTypes = topJobTypes,
            recentActivity = recentActivity
        )
    }

    fun getBusinessesByLocation(limit: Int = 100): Map<String, Int> {
        val businesses = businessRepository.findAll().take(limit)
        return businesses.groupBy { 
            // Simple location grouping by approximate area
            "${(it.location.coordinates[1] * 10).toInt() / 10.0},${(it.location.coordinates[0] * 10).toInt() / 10.0}"
        }.mapValues { it.value.size }
    }
}
