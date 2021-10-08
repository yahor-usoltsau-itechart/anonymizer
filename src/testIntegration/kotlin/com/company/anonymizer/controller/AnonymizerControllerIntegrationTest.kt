package com.company.anonymizer.controller

import com.company.anonymizer.IntegrationTest
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class AnonymizerControllerIntegrationTest : IntegrationTest() {

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

    @Test
    fun `Should anonymize text via API`() {
        Mockito.`when`(generator.generateDomain()).thenReturn(anonymizedDomain, anonymizedIp)
        Mockito.`when`(generator.generateEmail()).thenReturn(anonymizedEmail)
        Mockito.`when`(generator.generateId()).thenReturn(anonymizedId)

        mockMvc.post("/anonymize") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(TextModel(originalText))
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.text", Matchers.equalTo(expectedText)) }
        }
    }

}
