// service/scraping/WebScrapingService.kt
package com.mapscanner.service.scraping

import com.mapscanner.model.entity.JobOpportunity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Service
class WebScrapingService {
    
    private val logger = LoggerFactory.getLogger(WebScrapingService::class.java)
    
    private val userAgents = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36"
    )
    
    suspend fun scrapeJobsFromWebsite(websiteUrl: String, businessId: String): List<JobOpportunity> =
        withContext(Dispatchers.IO) {
            try {
                logger.info("Scraping jobs from: $websiteUrl")
                
                val document = Jsoup.connect(websiteUrl)
                    .userAgent(userAgents.random())
                    .timeout(30000)
                    .followRedirects(true)
                    .get()
                
                extractJobsFromDocument(document, websiteUrl, businessId)
            } catch (ex: Exception) {
                logger.error("Failed to scrape jobs from $websiteUrl: ${ex.message}", ex)
                emptyList()
            }
        }
    
    private fun extractJobsFromDocument(
        document: Document, 
        sourceUrl: String, 
        businessId: String
    ): List<JobOpportunity> {
        val jobs = mutableListOf<JobOpportunity>()
        
        // Common job posting selectors - customize based on target websites
        val jobSelectors = listOf(
            ".job-posting, .job-listing, .career-opportunity",
            "[class*='job'], [class*='career'], [class*='position']",
            "article[data-job], div[data-job-id]"
        )
        
        for (selector in jobSelectors) {
            val elements = document.select(selector)
            if (elements.isNotEmpty()) {
                elements.forEach { element ->
                    try {
                        val title = element.select("h1, h2, h3, .title, .job-title").text()
                        val description = element.select(".description, .job-description, p").text()
                        
                        if (title.isNotBlank() && description.isNotBlank()) {
                            jobs.add(
                                JobOpportunity(
                                    businessId = businessId,
                                    title = title,
                                    description = description,
                                    jobType = "Unknown",
                                    sourceUrl = sourceUrl,
                                    postedDate = LocalDateTime.now()
                                )
                            )
                        }
                    } catch (ex: Exception) {
                        logger.warn("Failed to parse job element: ${ex.message}")
                    }
                }
                break // Found jobs with this selector, no need to try others
            }
        }
        
        logger.info("Extracted ${jobs.size} jobs from $sourceUrl")
        return jobs
    }
}