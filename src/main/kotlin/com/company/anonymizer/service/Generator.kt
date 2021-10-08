package com.company.anonymizer.service

import com.github.javafaker.Faker
import org.springframework.stereotype.Service

@Service
class Generator {

    companion object {
        private val FAKER = Faker()
    }

    fun generateDomain(): String = FAKER.internet().domainName()

    fun generateEmail(): String = FAKER.internet().emailAddress()

    fun generateId(): String = FAKER.number().digits(5)

}
