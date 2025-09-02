// controller/BusinessController.kt
package com.mapscanner.controller

import com.mapscanner.model.entity.Business
import com.mapscanner.repository.business.BusinessRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/businesses")
@CrossOrigin(origins = ["*"])
class BusinessController(private val businessRepository: BusinessRepository) {

    @GetMapping
    fun getAllBusinesses(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) type: String?
    ): ResponseEntity<List<Business>> {
        val businesses = if (type != null) {
            businessRepository.findByBusinessType(type)
        } else {
            businessRepository.findAll()
        }
        
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, businesses.size)
        val pagedBusinesses = businesses.subList(startIndex, endIndex)
        
        return ResponseEntity.ok(pagedBusinesses)
    }

    @GetMapping("/{id}")
    fun getBusinessById(@PathVariable id: String): ResponseEntity<Business> {
        val business = businessRepository.findById(id)
        return if (business.isPresent) {
            ResponseEntity.ok(business.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    fun searchBusinesses(@RequestParam query: String): ResponseEntity<List<Business>> {
        val businesses = businessRepository.findByNameContainingIgnoreCase(query)
        return ResponseEntity.ok(businesses)
    }

    @GetMapping("/nearby")
    fun getNearbyBusinesses(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
        @RequestParam(defaultValue = "5000") radius: Int
    ): ResponseEntity<List<Business>> {
        val businesses = businessRepository.findBusinessesNearLocation(lng, lat, radius)
        return ResponseEntity.ok(businesses)
    }
}
