package com.company.anonymizer.service

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import java.net.URI

internal class URIExtensionsKtTest {

    private val generator = RandomGenerator()

    @Test
    fun `Should extract second level domain from URI`() {
        val uriToSld = mapOf(
            URI("https://example.com") to "example.com",
            URI("https://sub.example.com") to "example.com",
            URI("https://sub.sub.example.com") to "example.com",
            URI("https://example.co.uk") to "example.co.uk",
            URI("https://sub.example.co.uk") to "example.co.uk",
            URI("https://co.uk.co.uk") to "uk.co.uk",
            URI("https://a.b-c.de") to "b-c.de",
            URI("https://1337.net") to "1337.net",
            URI("https://username:password@co.uk.co.uk:8443/path?foo=bar#fragment") to "uk.co.uk",
        )

        assertSoftly { softly ->
            uriToSld.forEach { (uri, sld) ->
                softly.assertThat(uri.tryExtractSecondLevelDomain()).isNotNull().isEqualTo(sld)
            }
        }
    }

    @Test
    fun `Should not extract second level domain from URI`() {
        val uris = listOf(
            URI("https://localhost"),
            URI("https://127.0.0.1"),
            URI("https://[::1]"),
            URI("https://[0:0:0:0:0:0:0:1]"),
        )

        assertSoftly { softly ->
            uris.forEach { uri ->
                softly.assertThat(uri.tryExtractSecondLevelDomain()).isNull()
            }
        }
    }

    @Test
    fun `Should replace second level domain in URI`() {
        val replacement = generator.generateDomain()
        val originalToExpectedUris = mapOf(
            URI("https://example.com") to URI("https://$replacement"),
            URI("https://sub.example.com") to URI("https://sub.$replacement"),
            URI("https://sub.sub.example.com") to URI("https://sub.sub.$replacement"),
            URI("https://example.co.uk") to URI("https://$replacement"),
            URI("https://sub.example.co.uk") to URI("https://sub.$replacement"),
            URI("https://co.uk.co.uk") to URI("https://co.$replacement"),
            URI("https://a.b-c.de") to URI("https://a.$replacement"),
            URI("https://1337.net") to URI("https://$replacement"),
            URI("https://username:password@co.uk.co.uk:8443/path?foo=bar#fragment") to URI("https://username:password@co.$replacement:8443/path?foo=bar#fragment"),
        )

        assertSoftly { softly ->
            originalToExpectedUris.forEach { (original, expected) ->
                softly.assertThat(original.tryReplaceSecondLevelDomain(replacement)).isNotNull().isEqualTo(expected)
            }
        }
    }

    @Test
    fun `Should not replace second level domain in URI`() {
        val replacement = generator.generateDomain()
        val uris = listOf(
            URI("https://localhost"),
            URI("https://127.0.0.1"),
            URI("https://[::1]"),
            URI("https://[0:0:0:0:0:0:0:1]"),
        )

        assertSoftly { softly ->
            uris.forEach { uri ->
                softly.assertThat(uri.tryReplaceSecondLevelDomain(replacement)).isNull()
            }
        }
    }

    @Test
    fun `Should replace host in URI`() {
        val replacement = generator.generateDomain()
        val originalToExpectedUris = mapOf(
            URI("https://example.com") to URI("https://$replacement"),
            URI("https://sub.example.com") to URI("https://$replacement"),
            URI("https://localhost") to URI("https://$replacement"),
            URI("https://127.0.0.1") to URI("https://$replacement"),
            URI("https://[::1]") to URI("https://$replacement"),
            URI("https://[0:0:0:0:0:0:0:1]") to URI("https://$replacement"),
            URI("https://username:password@co.uk.co.uk:8443/path?foo=bar#fragment") to URI("https://username:password@$replacement:8443/path?foo=bar#fragment"),
        )

        assertSoftly { softly ->
            originalToExpectedUris.forEach { (original, expected) ->
                softly.assertThat(original.replaceHost(replacement)).isNotNull().isEqualTo(expected)
            }
        }
    }

    @Test
    fun `Should replace authority in URI`() {
        val replacement = generator.generateDomain()
        val originalToExpectedUris = mapOf(
            URI("https://example.com") to URI("https://$replacement"),
            URI("https://sub.example.com") to URI("https://$replacement"),
            URI("https://localhost") to URI("https://$replacement"),
            URI("https://127.0.0.1") to URI("https://$replacement"),
            URI("https://[::1]") to URI("https://$replacement"),
            URI("https://[0:0:0:0:0:0:0:1]") to URI("https://$replacement"),
            URI("https://username:password@co.uk.co.uk:8443/path?foo=bar#fragment") to URI("https://$replacement/path?foo=bar#fragment"),
        )

        assertSoftly { softly ->
            originalToExpectedUris.forEach { (original, expected) ->
                softly.assertThat(original.replaceAuthority(replacement)).isNotNull().isEqualTo(expected)
            }
        }
    }

}
