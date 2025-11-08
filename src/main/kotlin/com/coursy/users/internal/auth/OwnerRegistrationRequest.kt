package com.coursy.users.internal.auth

import java.util.*

data class OwnerRegistrationRequest(
    val currentUserId: UUID,
    val newUserId: UUID,
    val platformId: UUID
)
