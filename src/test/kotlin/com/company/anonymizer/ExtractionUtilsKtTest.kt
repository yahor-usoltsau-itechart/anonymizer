package com.company.anonymizer

import com.company.anonymizer.service.extractEmails
import com.company.anonymizer.service.extractIds
import com.company.anonymizer.service.extractUrls
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ExtractionUtilsKtTest {

    private val inputText = """
        Test data:
        - ID: 12345
        - E-mail: foo@msn.com
        - URL: https://att.net/users/foo
        
        Same, but URL has subdomain:
        - ID: 12345
        - E-mail: foo@msn.com
        - URL: https://www.att.net/users/foo
        
        Same domain in e-mail and URL:
        - ID: 23456
        - E-mail: bar@hotmail.com
        - URL: https://hotmail.com/users/bar
        
        ID in e-mail and URL:
        - ID: 34567
        - E-mail: order+34567@yahoo.com
        - URL: https://yahoo.com/orders/34567
        
        Complex URLs:
        - https://username:password@sub.domain.co.uk:8443/path?foo=bar#fragment
        - https://username:password@1.1.1.1:8443/path?foo=bar#fragment
    """.trimIndent()

    @Test
    fun testExtractUrls() {
        val expectedUrls = setOf(
            "https://att.net/users/foo",
            "https://www.att.net/users/foo",
            "https://hotmail.com/users/bar",
            "https://yahoo.com/orders/34567",
            "https://username:password@1.1.1.1:8443/path?foo=bar#fragment",
            "https://username:password@sub.domain.co.uk:8443/path?foo=bar#fragment",
        )

        val actualUrls = extractUrls(inputText)

        assertEquals(expectedUrls, actualUrls)
    }

    @Test
    fun testExtractEmails() {
        val expectedEmails = setOf(
            "foo@msn.com",
            "foo@msn.com",
            "bar@hotmail.com",
            "order+34567@yahoo.com",
            "password@sub.domain.co.uk", // this is part of the authority of the last URL
        )

        val actualEmails = extractEmails(inputText)

        assertEquals(expectedEmails, actualEmails)
    }

    @Test
    fun testExtractIds() {
        val expectedIds = setOf(
            "12345",
            "23456",
            "34567",
            "8443", // this is the port number of the last URL
        )

        val actualIds = extractIds(inputText)

        assertEquals(expectedIds, actualIds)
    }

}
