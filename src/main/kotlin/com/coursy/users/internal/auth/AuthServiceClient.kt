package com.coursy.users.internal.auth

import arrow.core.Either
import com.coursy.users.failure.Failure
import com.coursy.users.failure.NetworkFailure
import com.coursy.users.types.Email
import com.coursy.users.types.Password
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.util.*

@Component
class AuthServiceClient(
    private val webClient: WebClient,
    @Value("\${services.auth.url}")
    private val authServiceUrl: String
) {
    fun createUser(email: Email, password: Password, id: UUID, platformId: UUID?): Either<Failure, Unit> {
        return try {
            webClient
                .post()
                .uri("$authServiceUrl/api/auth/register")
                .header("Content-Type", "application/json")
                .bodyValue(
                    AuthRegistrationRequest(
                        email,
                        password,
                        id,
                        platformId
                    )
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError) { response ->
                    Mono.error(RuntimeException("Auth service error: ${response.statusCode()}"))
                }
                .bodyToMono<Unit>()
                .block()

            Either.Right(Unit)
        } catch (ex: Exception) {
            Either.Left(NetworkFailure(ex.message ?: "Unknown error"))
        }
    }

    fun createOwner(currentUserId: UUID, ownerId: UUID, platformId: UUID): Either<Failure, Unit> {
        return try {
            webClient
                .post()
                .uri("$authServiceUrl/api/auth/owner")
                .header("Content-Type", "application/json")
                .bodyValue(
                    OwnerRegistrationRequest(
                        currentUserId,
                        ownerId,
                        platformId,
                    )
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError) { response ->
                    Mono.error(RuntimeException("Auth service error: ${response.statusCode()}"))
                }
                .bodyToMono<Unit>()
                .block()

            Either.Right(Unit)
        } catch (ex: Exception) {
            Either.Left(NetworkFailure(ex.message ?: "Unknown error"))
        }
    }
}