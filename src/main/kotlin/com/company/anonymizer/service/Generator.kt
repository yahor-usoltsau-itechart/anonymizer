package com.company.anonymizer.service

import com.github.javafaker.Faker

interface Generator {

    fun generateDomain(): String

    fun generateEmail(): String

    fun generateId(): String

}

class RandomGenerator : Generator {

    companion object {
        private val FAKER = Faker()
    }

    override fun generateDomain(): String = FAKER.internet().domainName()

    override fun generateEmail(): String = FAKER.internet().emailAddress()

    override fun generateId(): String = FAKER.number().digits(5)

}
