package com.company.anonymizer

import com.company.anonymizer.controller.TextModel
import com.company.anonymizer.service.Generator
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApplicationTests {

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

    @Autowired
    private lateinit var jdbc: NamedParameterJdbcOperations

    @MockBean
    private lateinit var generator: Generator

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun beforeEach() {
        jdbc.update("delete from replacements", emptyMap<String, Any>())
    }

    @Test
    fun `Should anonymize text via API`() {
        `when`(generator.generateDomain()).thenReturn(anonymizedDomain, anonymizedIp)
        `when`(generator.generateEmail()).thenReturn(anonymizedEmail)
        `when`(generator.generateId()).thenReturn(anonymizedId)

        mockMvc.post("/anonymize") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(TextModel(originalText))
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.text", equalTo(expectedText)) }
        }
    }

}
