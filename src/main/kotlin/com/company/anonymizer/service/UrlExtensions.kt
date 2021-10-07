package com.company.anonymizer.service

import com.google.common.net.InternetDomainName
import java.net.URI

/**
 * Extracts the Second Level Domain from the given URL.
 * If the given URL has an IP address as a host, returns null.
 */
@Suppress("UnstableApiUsage")
fun URI.tryExtractSecondLevelDomain(): String? {
    return runCatching { InternetDomainName.from(this.host).topPrivateDomain().toString() }.getOrNull()
}

/**
 * Replaces the Second Level Domain in the given URL keeping userinfo, subdomains and port number.
 * If the given URL has an IP address as a host, returns null.
 */
fun URI.tryReplaceSecondLevelDomain(secondLevelDomainReplacement: String): URI? {
    val originalSecondLevelDomain = this.tryExtractSecondLevelDomain() ?: return null
    val originalAuthority = this.authority
    val authorityReplacement = originalAuthority.reversed().replaceFirst(
        originalSecondLevelDomain.reversed(), secondLevelDomainReplacement.reversed()
    ).reversed()
    return this.withAuthority(authorityReplacement)
}

/**
 * Replaces the host in the given URL keeping userinfo and port number.
 * Works with both domain names and IP addresses.
 */
fun URI.replaceHost(hostReplacement: String): URI {
    val originalHost = this.host
    val originalAuthority = this.authority
    val authorityReplacement = originalAuthority.reversed().replaceFirst(
        originalHost.reversed(), hostReplacement.reversed()
    ).reversed()
    return this.withAuthority(authorityReplacement)
}

fun URI.withAuthority(authority: String): URI {
    return URI(this.scheme, authority, this.path, this.query, this.fragment)
}
