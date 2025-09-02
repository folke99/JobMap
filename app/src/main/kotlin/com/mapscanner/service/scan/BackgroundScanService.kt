// service/scan/BackgroundScanService.kt
package com.mapscanner.service.scan

import com.google.maps.model.LatLng
import com.google.maps.model.PlaceType
import com.mapscanner.model.entity.*
import com.mapscanner.model.dto.ScanRequestDto
import com.mapscanner.repository.business.BusinessRepository
import com.mapscanner.repository.jobs.JobOpportunityRepository
import com.mapscanner.repository.jobs.ScanJobRepository
import com.mapscanner.service.maps.GoogleMapsService
import com.mapscanner.service.scraping.WebScrapingService
import kotlinx.coroutines.*
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Service
class BackgroundScanService(
    private val googleMapsService: GoogleMapsService,
    private val webScrapingService: WebScrapingService,
    private val scanJobRepository: ScanJobRepository,
    private val businessRepository: BusinessRepository,
    private val jobOpportunityRepository: JobOpportunityRepository
) {
    
    private val logger = LoggerFactory.getLogger(BackgroundScanService::class.java)
    
    @Async
    @Retryable(maxAttempts = 3)
    suspend fun processScan(scanId: String, request: ScanRequestDto) {
        try {
            updateScanStatus(scanId, ScanStatus.IN_PROGRESS, 0)
            
            val location = LatLng(request.latitude, request.longitude)
            val businesses = mutableListOf<Business>()
            val jobs = mutableListOf<JobOpportunity>()
            
            // Process each business type
            val businessTypes = if (request.businessTypes.isEmpty()) {
                listOf("restaurant", "store", "service")
            } else {
                request.businessTypes
            }
            
            for ((index, businessType) in businessTypes.withIndex()) {
                try {
                    val placeType = mapToPlaceType(businessType)
                    val searchResults = googleMapsService.searchNearbyBusinesses(
                        location, request.radius, placeType
                    )
                    
                    // Process each business
                    searchResults.forEach { result ->
                        val business = convertToBusinessEntity(result)
                        businesses.add(business)
                        
                        // Save business first to get ID
                        val savedBusiness = businessRepository.save(business)
                        
                        // Scrape jobs if website available and requested
                        if (request.includeJobScraping && result.website != null) {
                            delay(1000) // Rate limiting for scraping
                            val businessJobs = webScrapingService.scrapeJobsFromWebsite(
                                result.website, savedBusiness.id!!
                            )
                            jobs.addAll(businessJobs)
                        }
                    }
                    
                    // Update progress
                    val progress = ((index + 1) * 100) / businessTypes.size
                    updateScanStatus(scanId, ScanStatus.IN_PROGRESS, progress)
                    
                } catch (ex: Exception) {
                    logger.error("Error processing business type $businessType: ${ex.message}", ex)
                }
            }
            
            // Save all jobs
            if (jobs.isNotEmpty()) {
                jobOpportunityRepository.saveAll(jobs)
            }
            
            updateScanCompletion(scanId, ScanStatus.COMPLETED, businesses.size, jobs.size)
            
        } catch (ex: Exception) {
            logger.error("Scan failed for scanId: $scanId", ex)
            updateScanStatus(scanId, ScanStatus.FAILED, 0, ex.message)
        }
    }
    
    private suspend fun updateScanStatus(
        scanId: String, 
        status: ScanStatus, 
        progress: Int, 
        errorMessage: String? = null
    ) {
        val scanJob = scanJobRepository.findById(scanId)
        if (scanJob.isPresent) {
            val updated = scanJob.get().copy(
                status = status,
                progress = progress,
                errorMessage = errorMessage,
                completedAt = if (status == ScanStatus.COMPLETED || status == ScanStatus.FAILED) 
                    LocalDateTime.now() else null
            )
            scanJobRepository.save(updated)
        }
    }
    
    private suspend fun updateScanCompletion(
        scanId: String, 
        status: ScanStatus, 
        businessCount: Int, 
        jobCount: Int
    ) {
        val scanJob = scanJobRepository.findById(scanId)
        if (scanJob.isPresent) {
            val updated = scanJob.get().copy(
                status = status,
                progress = 100,
                totalBusinessesFound = businessCount,
                totalJobsFound = jobCount,
                completedAt = LocalDateTime.now()
            )
            scanJobRepository.save(updated)
        }
    }
    
    private fun convertToBusinessEntity(result: com.google.maps.model.PlacesSearchResult): Business {
        return Business(
            name = result.name,
            address = result.vicinity ?: "",
            location = Location(
                coordinates = listOf(result.geometry.location.lng, result.geometry.location.lat)
            ),
            businessType = result.types?.firstOrNull()?.toString() ?: "unknown",
            rating = result.rating?.toDouble(),
            googlePlaceId = result.placeId
        )
    }
    
    private fun mapToPlaceType(businessType: String): PlaceType? {
        return when (businessType.lowercase()) {
            "restaurant", "food" -> PlaceType.RESTAURANT
            "store", "retail" -> PlaceType.STORE
            "service" -> PlaceType.ESTABLISHMENT
            "health" -> PlaceType.HOSPITAL
            "education" -> PlaceType.SCHOOL
            else -> null
        }
    }
}