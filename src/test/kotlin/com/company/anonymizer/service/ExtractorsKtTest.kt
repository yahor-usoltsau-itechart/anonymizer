package com.company.anonymizer.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ExtractorsKtTest {

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

        Complex E-mails:
        - email@sub.example.com
        - foo.bar@example.com
        - foo+bar@example.com
        - foo-bar@example.com
        - 12345@example.com
        
        Complex URLs:
        - https://username:password@1.1.1.1:8443/path?foo=bar#fragment
        - https://username:password@sub.example.co.uk:8443/path?foo=bar#fragment
    """.trimIndent()

    @Test
    fun `Should extract URLs from text`() {
        val expectedUrls = setOf(
            "https://att.net/users/foo",
            "https://www.att.net/users/foo",
            "https://hotmail.com/users/bar",
            "https://yahoo.com/orders/34567",
            "https://username:password@1.1.1.1:8443/path?foo=bar#fragment",
            "https://username:password@sub.example.co.uk:8443/path?foo=bar#fragment",
        )

        val actualUrls = extractUrls(inputText)

        assertThat(actualUrls).containsAll(expectedUrls)
    }

    @Test
    fun `Should extract E-mails from text`() {
        val expectedEmails = setOf(
            "foo@msn.com",
            "foo@msn.com",
            "bar@hotmail.com",
            "order+34567@yahoo.com",
            "email@sub.example.com",
            "foo.bar@example.com",
            "foo+bar@example.com",
            "foo-bar@example.com",
            "12345@example.com",
            "password@sub.example.co.uk", // this is part of the authority of the last URL
        )

        val actualEmails = extractEmails(inputText)

        assertThat(actualEmails).containsAll(expectedEmails)
    }

    @Test
    fun `Should extract IDs from text`() {
        val expectedIds = setOf(
            "12345",
            "23456",
            "34567",
            "8443", // this is the port number of the last URL
        )

        val actualIds = extractIds(inputText)

        assertThat(actualIds).containsAll(expectedIds)
    }

}
