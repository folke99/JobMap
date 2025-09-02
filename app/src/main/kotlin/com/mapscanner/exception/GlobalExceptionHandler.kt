
// exception/GlobalExceptionHandler.kt
package com.mapscanner.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.slf4j.LoggerFactory

data class ErrorResponse(
    val error: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    @ExceptionHandler(BusinessSearchException::class)
    fun handleBusinessSearchException(ex: BusinessSearchException): ResponseEntity<ErrorResponse> {
        logger.error("Business search error: ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse("BUSINESS_SEARCH_ERROR", ex.message ?: "Unknown error")
        )
    }
    
    @ExceptionHandler(ScanJobException::class)
    fun handleScanJobException(ex: ScanJobException): ResponseEntity<ErrorResponse> {
        logger.error("Scan job error: ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse("SCAN_JOB_ERROR", ex.message ?: "Unknown error")
        )
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error: ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred")
        )
    }
}