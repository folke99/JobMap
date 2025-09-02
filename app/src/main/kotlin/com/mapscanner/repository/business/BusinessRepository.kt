// repository/business/BusinessRepository.kt
package com.mapscanner.repository.business

import com.mapscanner.model.entity.Business
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BusinessRepository : MongoRepository<Business, String> {
    
    fun findByBusinessType(businessType: String): List<Business>
    
    @Query("{ 'location': { '\$near': { '\$geometry': { 'type': 'Point', 'coordinates': [?0, ?1] }, '\$maxDistance': ?2 } } }")
    fun findBusinessesNearLocation(longitude: Double, latitude: Double, maxDistance: Int): List<Business>
    
    fun findByGooglePlaceId(googlePlaceId: String): Business?
    
    @Query("{ 'name': { '\$regex': ?0, '\$options': 'i' } }")
    fun findByNameContainingIgnoreCase(name: String): List<Business>
}