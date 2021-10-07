package com.company.anonymizer.repository

interface ReplacementRepository {

    fun getOrCreateDomainReplacement(originalDomain: String, replacementSupplier: () -> String): String

    fun getOrCreateEmailReplacement(originalEmail: String, replacementSupplier: () -> String): String

    fun getOrCreateIdReplacement(originalId: String, replacementSupplier: () -> String): String

}
