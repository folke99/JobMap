// controller/AnalyticsController.kt
package com.mapscanner.controller

import com.mapscanner.service.analytics.AnalyticsService
import com.mapscanner.service.analytics.DashboardStats
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = ["*"])
class AnalyticsController(private val analyticsService: AnalyticsService) {

    @GetMapping("/dashboard")
    fun getDashboardStats(): ResponseEntity<DashboardStats> {
        val stats = analyticsService.getDashboardStats()
        return ResponseEntity.ok(stats)
    }

    @GetMapping("/businesses/by-location")
    fun getBusinessesByLocation(
        @RequestParam(defaultValue = "100") limit: Int
    ): ResponseEntity<Map<String, Int>> {
        val locationData = analyticsService.getBusinessesByLocation(limit)
        return ResponseEntity.ok(locationData)
    }
}