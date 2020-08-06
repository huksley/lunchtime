package org.ruslan.lunchtime

import io.ktor.http.HttpStatusCode

/**
 * Exception with HTTP status code
 */
data class HttpException(val code: HttpStatusCode, override val message: String): IllegalArgumentException(message)