package com.company.anonymizer.service

import com.company.anonymizer.repository.ReplacementRepository
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI

/**
 * An anonymization service which takes a text as an input and replaces all occurrences of URLs, IDs and E-mails with
 * fake data.
 *
 * Please note that this class is not thread safe.
 *
 * @see extractUrls to understand how URLs are extracted
 * @see extractEmails to understand how E-mails are extracted
 * @see extractIds to understand how IDs are extracted
 * @see tryReplaceSecondLevelDomain to understand how URLs with domains in host are processed
 * @see replaceHost to understand how URLs with IP-addresses in host are processed
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class Anonymizer(
    private val generator: Generator,
    private val replacementRepository: ReplacementRepository,
) {

    private val replacements = mutableListOf<String>()

    @Transactional
    fun anonymize(originalText: String): String {
        val anonymizedTextFormat = anonymizeIds(anonymizeEmails(anonymizeUrls(originalText)))
        val anonymizedText = anonymizedTextFormat.format(*replacements.toTypedArray())
        replacements.clear()
        return anonymizedText
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
