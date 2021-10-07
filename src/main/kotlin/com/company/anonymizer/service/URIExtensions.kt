package com.company.anonymizer.service

import com.google.common.net.InternetDomainName
import java.net.URI

/**
 * Extracts the second level domain from the given URI.
 * If the given URI has an IP address as a host, returns null.
 */
@Suppress("UnstableApiUsage")
fun URI.tryExtractSecondLevelDomain(): String? {
    return runCatching { InternetDomainName.from(this.host).topPrivateDomain().toString() }.getOrNull()
}

/**
 * Replaces the second level domain in the given URI.
 * Keeps scheme, userinfo, subdomains, port, path, query and fragment untouched.
 * If the given URI has an IP address as a host, returns null.
 */
fun URI.tryReplaceSecondLevelDomain(secondLevelDomainReplacement: String): URI? {
    val originalSecondLevelDomain = this.tryExtractSecondLevelDomain() ?: return null
    val originalAuthority = this.authority
    val authorityReplacement = originalAuthority.reversed().replaceFirst(
        originalSecondLevelDomain.reversed(), secondLevelDomainReplacement.reversed()
    ).reversed()
    return this.replaceAuthority(authorityReplacement)
}

/**
 * Replaces the host in the given URI.
 * Keeps scheme, userinfo, port, path, query and fragment untouched.
 * Works with both domain names and IP addresses.
 */
fun URI.replaceHost(hostReplacement: String): URI {
    val originalHost = this.host
    val originalAuthority = this.authority
    val authorityReplacement = originalAuthority.reversed().replaceFirst(
        originalHost.reversed(), hostReplacement.reversed()
    ).reversed()
    return this.replaceAuthority(authorityReplacement)
}

/**
 * Replaces the authority (userinfo + host + port) in the given URI.
 * Keeps scheme, path, query and fragment untouched.
 * Works with both domain names and IP addresses.
 */
fun URI.replaceAuthority(authorityReplacement: String): URI {
    return URI(this.scheme, authorityReplacement, this.path, this.query, this.fragment)
}
