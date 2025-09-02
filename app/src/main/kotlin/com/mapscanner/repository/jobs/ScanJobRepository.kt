// repository/jobs/ScanJobRepository.kt
package com.mapscanner.repository.jobs

import com.mapscanner.model.entity.ScanJob
import com.mapscanner.model.entity.ScanStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ScanJobRepository : MongoRepository<ScanJob, String> {
    fun findByStatus(status: ScanStatus): List<ScanJob>
    fun findByStatusIn(statuses: List<ScanStatus>): List<ScanJob>
}
