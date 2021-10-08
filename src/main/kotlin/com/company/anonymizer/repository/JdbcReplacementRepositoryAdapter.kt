package com.company.anonymizer.repository

import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.stereotype.Repository

@Primary
@Repository
class JdbcReplacementRepositoryAdapter(private val jdbc: NamedParameterJdbcOperations) : ReplacementRepository {

    private val getOrCreateQuery = /* language=PostgreSQL */ """
        insert into replacements as r (type, source, target)
        values (:type::replacement_type, :source, :target)
        on conflict (type, source) do update
        set target = r.target
        returning target
    """.trimIndent()

    override fun getOrCreateDomainReplacement(originalDomain: String, replacementSupplier: () -> String): String {
        val params = buildParams(ReplacementType.DOMAIN, originalDomain, replacementSupplier())
        return jdbc.queryForObject(getOrCreateQuery, params, String::class.java)!!
    }

    override fun getOrCreateEmailReplacement(originalEmail: String, replacementSupplier: () -> String): String {
        val params = buildParams(ReplacementType.EMAIL, originalEmail, replacementSupplier())
        return jdbc.queryForObject(getOrCreateQuery, params, String::class.java)!!
    }

    override fun getOrCreateIdReplacement(originalId: String, replacementSupplier: () -> String): String {
        val params = buildParams(ReplacementType.ID, originalId, replacementSupplier())
        return jdbc.queryForObject(getOrCreateQuery, params, String::class.java)!!
    }

    private fun buildParams(type: ReplacementType, source: String, target: String) = mapOf<String, Any>(
        "source" to source.lowercase(),
        "type" to type.toString(),
        "target" to target,
    )

}

enum class ReplacementType {
    DOMAIN,
    EMAIL,
    ID,
}
