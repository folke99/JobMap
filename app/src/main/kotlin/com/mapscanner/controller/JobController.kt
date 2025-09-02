// controller/JobController.kt
package com.mapscanner.controller

import com.mapscanner.model.entity.JobOpportunity
import com.mapscanner.repository.jobs.JobOpportunityRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = ["*"])
class JobController(private val jobRepository: JobOpportunityRepository) {

    @GetMapping
    fun getAllJobs(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) type: String?
    ): ResponseEntity<List<JobOpportunity>> {
        val jobs = if (type != null) {
            jobRepository.findByJobType(type)
        } else {
            jobRepository.findAll()
        }
        
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, jobs.size)
        val pagedJobs = jobs.subList(startIndex, endIndex)
        
        return ResponseEntity.ok(pagedJobs)
    }

    @GetMapping("/{id}")
    fun getJobById(@PathVariable id: String): ResponseEntity<JobOpportunity> {
        val job = jobRepository.findById(id)
        return if (job.isPresent) {
            ResponseEntity.ok(job.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    fun searchJobs(@RequestParam query: String): ResponseEntity<List<JobOpportunity>> {
        val jobs = jobRepository.findByTitleContainingIgnoreCase(query)
        return ResponseEntity.ok(jobs)
    }

    @GetMapping("/business/{businessId}")
    fun getJobsByBusiness(@PathVariable businessId: String): ResponseEntity<List<JobOpportunity>> {
        val jobs = jobRepository.findByBusinessId(businessId)
        return ResponseEntity.ok(jobs)
    }

    @GetMapping("/recent")
    fun getRecentJobs(@RequestParam(defaultValue = "7") days: Long): ResponseEntity<List<JobOpportunity>> {
        val cutoffDate = LocalDateTime.now().minusDays(days)
        val jobs = jobRepository.findByPostedDateAfter(cutoffDate)
        return ResponseEntity.ok(jobs)
    }
}