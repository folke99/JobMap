// config/MongoConfig.kt
package com.mapscanner.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.mapscanner.repository"])
class MongoConfig : AbstractMongoClientConfiguration() {
    
    override fun getDatabaseName(): String = "mapscanner"
    
    override fun getMappingBasePackages(): Collection<String> {
        return setOf("com.mapscanner.model.entity")
    }
}