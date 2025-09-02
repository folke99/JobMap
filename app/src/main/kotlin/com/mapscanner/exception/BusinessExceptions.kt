// exception/BusinessExceptions.kt
package com.mapscanner.exception

class BusinessSearchException(message: String, cause: Throwable? = null) : 
    RuntimeException(message, cause)

class ScanJobException(message: String, cause: Throwable? = null) : 
    RuntimeException(message, cause)

class WebScrapingException(message: String, cause: Throwable? = null) : 
    RuntimeException(message, cause)
