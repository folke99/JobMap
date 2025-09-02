// service/maps/GoogleMapsService.kt
package com.mapscanner.service.maps

import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.LatLng
import com.google.maps.model.PlaceType
import com.google.maps.model.PlacesSearchResult
import com.mapscanner.exception.BusinessSearchException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class GoogleMapsService(private val geoApiContext: GeoApiContext) {
    
    private val logger = LoggerFactory.getLogger(GoogleMapsService::class.java)
    
    suspend fun searchNearbyBusinesses(
        location: LatLng,
        radius: Int,
        businessType: PlaceType? = null
    ): List<PlacesSearchResult> = withContext(Dispatchers.IO) {
        try {
            logger.info("Searching for businesses near ${location.lat}, ${location.lng} within ${radius}m")
            
            val request = PlacesApi.nearbySearchQuery(geoApiContext, location, radius)
            businessType?.let { request.type(it) }
            
            val results = request.await()
            logger.info("Found ${results.results.size} businesses")
            
            results.results.toList()
        } catch (ex: Exception) {
            logger.error("Failed to search businesses: ${ex.message}", ex)
            throw BusinessSearchException("Failed to search businesses: ${ex.message}", ex)
        }
    }
    
    suspend fun getPlaceDetails(placeId: String): com.google.maps.model.PlaceDetails? = 
        withContext(Dispatchers.IO) {
            try {
                val request = PlacesApi.placeDetails(geoApiContext, placeId)
                request.await()
            } catch (ex: Exception) {
                logger.error("Failed to get place details for $placeId: ${ex.message}", ex)
                null
            }
        }
}
