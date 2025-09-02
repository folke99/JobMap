
// service/validation/LocationValidator.kt
package com.mapscanner.service.validation

import org.springframework.stereotype.Service

@Service
class LocationValidator {

    fun isValidLatitude(latitude: Double): Boolean {
        return latitude >= -90.0 && latitude <= 90.0
    }

    fun isValidLongitude(longitude: Double): Boolean {
        return longitude >= -180.0 && longitude <= 180.0
    }

    fun isValidRadius(radius: Int): Boolean {
        return radius > 0 && radius <= 50000 // Max 50km
    }

    fun validateCoordinates(latitude: Double, longitude: Double): List<String> {
        val errors = mutableListOf<String>()
        
        if (!isValidLatitude(latitude)) {
            errors.add("Latitude must be between -90 and 90")
        }
        
        if (!isValidLongitude(longitude)) {
            errors.add("Longitude must be between -180 and 180")
        }
        
        return errors
    }
}