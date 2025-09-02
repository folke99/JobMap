// controller/ScanController.kt
package com.mapscanner.controller

import com.mapscanner.model.dto.*
import com.mapscanner.model.entity.Location
import com.mapscanner.model.entity.ScanJob
import com.mapscanner.model.entity.ScanStatus
import com.mapscanner.repository.jobs.ScanJobRepository
import com.mapscanner.service.scan.BackgroundScanService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory
import java.util.*

@RestController
@RequestMapping("/api/scans")
@CrossOrigin(origins = ["*"]) // Configure appropriately for production
class ScanController(
    private val backgroundScanService: BackgroundScanService,
    private val scanJobRepository: ScanJobRepository
) {
    
    private val logger = LoggerFactory.getLogger(ScanController::class.java)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    
    @PostMapping("/start")
    suspend fun startScan(@RequestBody request: ScanRequestDto): ResponseEntity<ScanResponseDto> {
        try {
            // Create scan job
            val scanJob = ScanJob(
                status = ScanStatus.PENDING,
                location = Location(coordinates = listOf(request.longitude, request.latitude)),
                radius = request.radius,
                businessTypes = request.businessTypes
            )
            
            val savedScan = scanJobRepository.save(scanJob)
            val scanId = savedScan.id!!
            
            // Start background processing
            coroutineScope.launch {
                backgroundScanService.processScan(scanId, request)
            }
            
            logger.info("Started scan with ID: $scanId")
            
            return ResponseEntity.ok(
                ScanResponseDto(
                    scanId = scanId,
                    status = "STARTED",
                    message = "Scan initiated successfully"
                )
            )
            
        } catch (ex: Exception) {
            logger.error("Failed to start scan: ${ex.message}", ex)
            return ResponseEntity.badRequest().body(
                ScanResponseDto(
                    scanId = "",
                    status = "ERROR",
                    message = "Failed to start scan: ${ex.message}"
                )
            )
        }
    }
    
    @GetMapping("/{scanId}/status")
    suspend fun getScanStatus(@PathVariable scanId: String): ResponseEntity<ScanStatusDto> {
        val scanJob = scanJobRepository.findById(scanId)
        
        if (scanJob.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        
        val job = scanJob.get()
        val estimatedCompletion = if (job.status == ScanStatus.IN_PROGRESS && job.progress > 0) {
            "Approximately ${(100 - job.progress) / 10} minutes remaining"
        } else null
        
        return ResponseEntity.ok(
            ScanStatusDto(
                scanId = scanId,
                status = job.status.name,
                progress = job.progress,
                businessesFound = job.totalBusinessesFound,
                jobsFound = job.totalJobsFound,
                estimatedCompletion = estimatedCompletion
            )
        )
    }
    
    @GetMapping
    suspend fun getAllScans(): ResponseEntity<List<ScanJob>> {
        val scans = scanJobRepository.findAll()
        return ResponseEntity.ok(scans.toList())
    }
    
    @DeleteMapping("/{scanId}")
    suspend fun cancelScan(@PathVariable scanId: String): ResponseEntity<String> {
        val scanJob = scanJobRepository.findById(scanId)
        
        if (scanJob.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        
        val job = scanJob.get()
        if (job.status == ScanStatus.IN_PROGRESS) {
            val cancelled = job.copy(status = ScanStatus.CANCELLED)
            scanJobRepository.save(cancelled)
            return ResponseEntity.ok("Scan cancelled successfully")
        }
        
        return ResponseEntity.badRequest().body("Cannot cancel scan in status: ${job.status}")
    }
}