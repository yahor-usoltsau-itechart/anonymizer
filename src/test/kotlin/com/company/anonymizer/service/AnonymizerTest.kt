package com.company.anonymizer.service

import com.company.anonymizer.repository.InMemoryReplacementRepository
import com.company.anonymizer.repository.ReplacementRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

internal class AnonymizerTest {

    private val originalId = "12345"
    private val anonymizedId = "98765"

    private val originalEmail = "foo.bar-baz+123@qwe.com"
    private val anonymizedEmail = "fake@anonymous.org"

    private val originalDomain = "example.co.uk"
    private val anonymizedDomain = "google.com"

    private val originalIp = "1.1.1.1"
    private val anonymizedIp = "yahoo.com"

    private val originalText = """
        Test:
        - ID: $originalId
        - E-mail: $originalEmail
        - URL: https://$originalDomain/orders/12345
        
        Same, but URL has subdomain:
        - ID: $originalId
        - E-mail: $originalEmail
        - URL: https://www.$originalDomain/orders/12345
        
        Complex URLs:
        - https://username:password@sub.$originalDomain:8443/path?foo=bar#fragment
        - https://username:password@$originalIp:8443/path?foo=bar#fragment
    """.trimIndent()

    private val expectedText = """
        Test:
        - ID: $anonymizedId
        - E-mail: $anonymizedEmail
        - URL: https://$anonymizedDomain/orders/12345
        
        Same, but URL has subdomain:
        - ID: $anonymizedId
        - E-mail: $anonymizedEmail
        - URL: https://www.$anonymizedDomain/orders/12345
        
        Complex URLs:
        - https://username:password@sub.$anonymizedDomain:8443/path?foo=bar#fragment
        - https://username:password@$anonymizedIp:8443/path?foo=bar#fragment
    """.trimIndent()

    private lateinit var repository: ReplacementRepository

    @BeforeEach
    fun beforeEach() {
        repository = InMemoryReplacementRepository()
    }

    @Test
    fun `Should anonymize text remembering replacements between executions`() {
        val generator = mock(Generator::class.java)
        `when`(generator.generateDomain()).thenReturn(anonymizedDomain, anonymizedIp)
        `when`(generator.generateEmail()).thenReturn(anonymizedEmail)
        `when`(generator.generateId()).thenReturn(anonymizedId)
        val anonymizer = Anonymizer(generator, repository)

        val firstResult = anonymizer.anonymize(originalText)
        val secondResult = anonymizer.anonymize(originalText)

        assertThat(firstResult).isEqualTo(secondResult).isEqualTo(expectedText)
    }

}
