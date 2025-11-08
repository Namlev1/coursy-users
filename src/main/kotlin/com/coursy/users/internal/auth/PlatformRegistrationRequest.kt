package com.coursy.users.internal.auth

import java.util.*

data class PlatformRegistrationRequest(
    val userId: UUID,
    val platformId: UUID
)
