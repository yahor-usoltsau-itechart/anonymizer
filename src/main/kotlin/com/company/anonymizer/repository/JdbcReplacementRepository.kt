package com.company.anonymizer.repository

import org.springframework.stereotype.Repository

@Repository
class JdbcReplacementRepository : ReplacementRepository {

    override fun getOrCreateDomainReplacement(originalDomain: String, replacementSupplier: () -> String): String {
        TODO("Not yet implemented")
    }

    override fun getOrCreateEmailReplacement(originalEmail: String, replacementSupplier: () -> String): String {
        TODO("Not yet implemented")
    }

    override fun getOrCreateIdReplacement(originalId: String, replacementSupplier: () -> String): String {
        TODO("Not yet implemented")
    }

}
