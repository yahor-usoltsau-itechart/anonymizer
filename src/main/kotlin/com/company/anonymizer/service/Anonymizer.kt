package com.company.anonymizer.service

import com.company.anonymizer.repository.ReplacementRepository
import java.net.URI

/**
 * An anonymization service which takes a text as an input and replaces all occurrences of URLs, IDs and E-mails with
 * fake data.
 *
 * @see extractUrls to understand how URLs are extracted
 * @see extractEmails to understand how E-mails are extracted
 * @see extractIds to understand how IDs are extracted
 * @see tryReplaceSecondLevelDomain to understand how URLs with domains in host are processed
 * @see replaceHost to understand how URLs with IP-addresses in host are processed
 */
class Anonymizer(
    private val generator: Generator,
    private val replacementRepository: ReplacementRepository,
) {

    private val replacements = mutableListOf<String>()

    fun anonymize(originalText: String): String {
        val anonymizedTextFormat = anonymizeIds(anonymizeEmails(anonymizeUrls(originalText)))
        return anonymizedTextFormat.format(*replacements.toTypedArray())
    }

    private fun anonymizeUrls(originalText: String): String {
        var anonymizedTextFormat = originalText
        extractUrls(originalText)
            .map { originalUrl -> URI(originalUrl) }
            .map { originalUrl ->
                val originalDomain = originalUrl.tryExtractSecondLevelDomain() ?: originalUrl.host
                val anonymizedDomain = replacementRepository.getOrCreateDomainReplacement(
                    originalDomain,
                    generator::generateDomain
                )
                val anonymizedUrl = originalUrl.tryReplaceSecondLevelDomain(anonymizedDomain)
                    ?: originalUrl.replaceHost(anonymizedDomain)
                originalUrl to anonymizedUrl
            }
            .forEach { (originalUrl, anonymizedUrl) ->
                val replacementFormat = saveAndGetIndexFormat(anonymizedUrl.toString())
                anonymizedTextFormat = anonymizedTextFormat.replace(originalUrl.toString(), replacementFormat)
            }
        return anonymizedTextFormat
    }

    private fun anonymizeEmails(originalText: String): String {
        var anonymizedTextFormat = originalText
        extractEmails(originalText).forEach { originalEmail ->
            val anonymizedEmail = replacementRepository.getOrCreateEmailReplacement(
                originalEmail,
                generator::generateEmail
            )
            val replacementFormat = saveAndGetIndexFormat(anonymizedEmail)
            anonymizedTextFormat = anonymizedTextFormat.replace(originalEmail, replacementFormat)
        }
        return anonymizedTextFormat
    }

    private fun anonymizeIds(originalText: String): String {
        var anonymizedTextFormat = originalText
        extractIds(originalText).forEach { originalId ->
            val anonymizedId = replacementRepository.getOrCreateIdReplacement(
                originalId,
                generator::generateId
            )
            val replacementFormat = saveAndGetIndexFormat(anonymizedId)
            anonymizedTextFormat = anonymizedTextFormat.replace(originalId, replacementFormat)
        }
        return anonymizedTextFormat
    }

    private fun saveAndGetIndexFormat(replacement: String): String {
        replacements += replacement
        return "%${replacements.size}\$s"
    }

}
