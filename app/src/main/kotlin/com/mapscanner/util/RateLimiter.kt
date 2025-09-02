
// util/RateLimiter.kt
package com.mapscanner.util

import kotlinx.coroutines.delay
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimiter {
    
    private val requestCounts = ConcurrentHashMap<String, MutableList<LocalDateTime>>()
    private val maxRequestsPerMinute = 60
    
    suspend fun waitIfNeeded(key: String) {
        val now = LocalDateTime.now()
        val requests = requestCounts.getOrPut(key) { mutableListOf() }
        
        synchronized(requests) {
            // Remove requests older than 1 minute
            requests.removeAll { it.isBefore(now.minusMinutes(1)) }
            
            if (requests.size >= maxRequestsPerMinute) {
                val oldestRequest = requests.minOrNull()
                if (oldestRequest != null) {
                    val waitTime = 60 - java.time.Duration.between(oldestRequest, now).seconds
                    if (waitTime > 0) {
                        delay(waitTime * 1000)
                    }
                }
            }
            
            requests.add(now)
        }
    }
}