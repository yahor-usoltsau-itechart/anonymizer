package com.company.anonymizer.repository

interface ReplacementRepository {

    fun getOrCreateDomainReplacement(originalDomain: String, replacementSupplier: () -> String): String

    fun getOrCreateEmailReplacement(originalEmail: String, replacementSupplier: () -> String): String

    fun getOrCreateIdReplacement(originalId: String, replacementSupplier: () -> String): String

}

class InMemoryReplacementRepository : ReplacementRepository {

    private val domainReplacements = mutableMapOf<String, String>()

    private val emailReplacements = mutableMapOf<String, String>()

    private val idReplacements = mutableMapOf<String, String>()

    override fun getOrCreateDomainReplacement(originalDomain: String, replacementSupplier: () -> String): String {
        return domainReplacements.compute(originalDomain) { _, anonymizedDomain ->
            anonymizedDomain ?: replacementSupplier()
        }!!
    }

    override fun getOrCreateEmailReplacement(originalEmail: String, replacementSupplier: () -> String): String {
        return emailReplacements.compute(originalEmail) { _, anonymizedEmail ->
            anonymizedEmail ?: replacementSupplier()
        }!!
    }

    override fun getOrCreateIdReplacement(originalId: String, replacementSupplier: () -> String): String {
        return idReplacements.compute(originalId) { _, anonymizedId ->
            anonymizedId ?: replacementSupplier()
        }!!
    }

}
