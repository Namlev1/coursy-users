package com.coursy.users.internal.auth

import com.coursy.users.types.Email
import com.coursy.users.types.Password
import java.util.*

data class AuthRegistrationRequest(
    val email: Email,
    val password: Password,
    val id: UUID,
    val platformId: UUID?
)
