// config/GoogleMapsConfig.kt
package com.mapscanner.config

import com.google.maps.GeoApiContext
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.context.properties.EnableConfigurationProperties

@Configuration
@EnableConfigurationProperties(GoogleMapsProperties::class)
class GoogleMapsConfig(private val properties: GoogleMapsProperties) {

    @Bean
    fun geoApiContext(): GeoApiContext {
        return GeoApiContext.Builder()
            .apiKey(properties.apiKey)
            .queryRateLimit(properties.queryRateLimit)
            .connectTimeout(properties.connectTimeout, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(properties.readTimeout, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }
}

@ConfigurationProperties(prefix = "google.maps")
data class GoogleMapsProperties(
    val apiKey: String = "",
    val queryRateLimit: Int = 10,
    val connectTimeout: Long = 10,
    val readTimeout: Long = 30
)
