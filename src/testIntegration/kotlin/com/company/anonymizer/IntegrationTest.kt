package com.company.anonymizer

import com.company.anonymizer.service.Generator
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class IntegrationTest {

    @Autowired
    protected lateinit var jdbc: NamedParameterJdbcOperations

    @SpyBean
    protected lateinit var generator: Generator

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun beforeEach() {
        jdbc.update("delete from replacements", emptyMap<String, Any>())
    }

}
