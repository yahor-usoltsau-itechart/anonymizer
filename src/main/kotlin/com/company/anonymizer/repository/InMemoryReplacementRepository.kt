package com.company.anonymizer.repository

import org.springframework.stereotype.Repository

@Repository
class InMemoryReplacementRepository : ReplacementRepository {

    private val domainReplacements = mutableMapOf<String, String>()

    private val emailReplacements = mutableMapOf<String, String>()

    private val idReplacements = mutableMapOf<String, String>()

    override fun getOrCreateDomainReplacement(originalDomain: String, replacementSupplier: () -> String): String {
        return domainReplacements.compute(originalDomain.lowercase()) { _, anonymizedDomain ->
            anonymizedDomain ?: replacementSupplier()
        }!!
    }

    override fun getOrCreateEmailReplacement(originalEmail: String, replacementSupplier: () -> String): String {
        return emailReplacements.compute(originalEmail.lowercase()) { _, anonymizedEmail ->
            anonymizedEmail ?: replacementSupplier()
        }!!
    }

    override fun getOrCreateIdReplacement(originalId: String, replacementSupplier: () -> String): String {
        return idReplacements.compute(originalId.lowercase()) { _, anonymizedId ->
            anonymizedId ?: replacementSupplier()
        }!!
    }

}
