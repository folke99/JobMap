// model/entity/Business.kt
package com.mapscanner.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime

@Document(collection = "businesses")
data class Business(
    @Id
    val id: String? = null,
    
    @Indexed
    val name: String,
    
    val address: String,
    
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    val location: Location,
    
    @Indexed
    val businessType: String,
    
    val phone: String? = null,
    val website: String? = null,
    val rating: Double? = null,
    val reviewCount: Int? = null,
    
    @Indexed
    val googlePlaceId: String? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class Location(
    val type: String = "Point",
    val coordinates: List<Double> // [longitude, latitude]
)