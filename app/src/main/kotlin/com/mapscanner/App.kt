// Application.kt - Main Spring Boot Application
package com.mapscanner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableScheduling
class MapScannerApplication

fun main(args: Array<String>) {
    runApplication<MapScannerApplication>(*args)
}
