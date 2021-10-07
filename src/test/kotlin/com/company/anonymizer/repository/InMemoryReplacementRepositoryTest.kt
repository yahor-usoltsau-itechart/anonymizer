package com.company.anonymizer.repository

import com.company.anonymizer.service.RandomGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class InMemoryReplacementRepositoryTest {

    private val generator = RandomGenerator()

    private lateinit var repository: ReplacementRepository

    @BeforeEach
    fun beforeEach() {
        repository = InMemoryReplacementRepository()
    }

    @Test
    fun `Should remember Domain replacements`() {
        val originalDomain = generator.generateDomain()

        val firstResult = repository.getOrCreateDomainReplacement(originalDomain, generator::generateDomain)
        val secondResult = repository.getOrCreateDomainReplacement(originalDomain, generator::generateDomain)

        assertThat(firstResult).isEqualTo(secondResult).isNotEqualTo(originalDomain)
    }

    @Test
    fun `Should remember E-mail replacements`() {
        val originalEmail = generator.generateEmail()

        val firstResult = repository.getOrCreateEmailReplacement(originalEmail, generator::generateEmail)
        val secondResult = repository.getOrCreateEmailReplacement(originalEmail, generator::generateEmail)

        assertThat(firstResult).isEqualTo(secondResult).isNotEqualTo(originalEmail)
    }

    @Test
    fun `Should remember ID replacements`() {
        val originalId = generator.generateId()

        val firstResult = repository.getOrCreateIdReplacement(originalId, generator::generateId)
        val secondResult = repository.getOrCreateIdReplacement(originalId, generator::generateId)

        assertThat(firstResult).isEqualTo(secondResult).isNotEqualTo(originalId)
    }

}
