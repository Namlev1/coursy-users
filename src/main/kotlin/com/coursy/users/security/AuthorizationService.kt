package com.coursy.users.security

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.coursy.users.failure.AuthorizationFailure
import com.coursy.users.model.Role
import com.coursy.users.model.User
import getPlatformId
import getRole
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthorizationService {
    fun canCreateUserWithRole(
        jwt: PreAuthenticatedAuthenticationToken?,
        targetTenantId: UUID?,
        targetRole: Role
    ): Boolean {
        if (targetRole in listOf(Role.ROLE_TENANT, Role.ROLE_PLATFORM_USER)) {
            return true
        }

        if (jwt == null) {
            return false
        }

        val principalRole = jwt.getRole()
        val principalPlatformId = jwt.getPlatformId()


        return when (principalRole) {
            Role.ROLE_HOST_OWNER, Role.ROLE_HOST_ADMIN -> true
            Role.ROLE_PLATFORM_OWNER, Role.ROLE_PLATFORM_ADMIN -> {

                when (targetRole) {
                    Role.ROLE_PLATFORM_ADMIN -> targetTenantId == principalPlatformId
                    Role.ROLE_PLATFORM_OWNER -> false
                    else -> false
                }
            }

            else -> false
        }
    }

    fun canRemoveUser(jwt: PreAuthenticatedAuthenticationToken, user: User): Boolean {
        val principalRole = jwt.getRole()

        val principalPlatformId = jwt.getPlatformId()
        val targetUserPlatformId = user.platformId

        return when (principalRole) {
            Role.ROLE_HOST_OWNER -> true

            Role.ROLE_HOST_ADMIN -> {

                user.role in listOf(
                    Role.ROLE_TENANT,
                    Role.ROLE_PLATFORM_OWNER,
                    Role.ROLE_PLATFORM_ADMIN,
                    Role.ROLE_PLATFORM_USER
                )
            }

            Role.ROLE_PLATFORM_OWNER -> {

                targetUserPlatformId == principalPlatformId
            }

            Role.ROLE_PLATFORM_ADMIN -> {

                targetUserPlatformId == principalPlatformId &&
                        user.role == Role.ROLE_PLATFORM_USER
            }

            Role.ROLE_TENANT, Role.ROLE_PLATFORM_USER -> false
        }
    }

    fun canUpdateUserRole(jwt: PreAuthenticatedAuthenticationToken, targetUser: User, newRole: Role): Boolean {
        val principalRole = jwt.getRole()

        val principalPlatformId = jwt.getPlatformId()
        val targetUserPlatformId = targetUser.platformId

        return when (principalRole) {
            Role.ROLE_HOST_OWNER -> true

            Role.ROLE_HOST_ADMIN -> {

                newRole in listOf(
                    Role.ROLE_HOST_ADMIN,
                    Role.ROLE_PLATFORM_OWNER,
                    Role.ROLE_PLATFORM_ADMIN
                )
            }

            Role.ROLE_PLATFORM_OWNER -> {

                targetUserPlatformId == principalPlatformId
            }

            Role.ROLE_PLATFORM_ADMIN -> {

                targetUserPlatformId == principalPlatformId &&
                        newRole in listOf(
                    Role.ROLE_PLATFORM_ADMIN,
                    Role.ROLE_PLATFORM_USER
                )
            }

            Role.ROLE_TENANT, Role.ROLE_PLATFORM_USER -> false
        }
    }

    fun canFetchUser(jwt: PreAuthenticatedAuthenticationToken, user: User): Boolean {
        val accessLevel = getUserAccessLevel(jwt) ?: return false
        return accessLevel.canAccess(user)
    }

    fun getUserFetchSpecification(jwt: PreAuthenticatedAuthenticationToken): Either<AuthorizationFailure, Specification<User>> {
        val accessLevel = getUserAccessLevel(jwt)
            ?: return AuthorizationFailure.InsufficientRole.left()
        return accessLevel.toSpecification().right()
    }

    private fun getUserAccessLevel(jwt: PreAuthenticatedAuthenticationToken): UserAccessLevel? {
        val principalRole = jwt.getRole()

        val principalPlatformId = jwt.getPlatformId()

        return when (principalRole) {
            Role.ROLE_HOST_OWNER, Role.ROLE_HOST_ADMIN -> UserAccessLevel.All

            Role.ROLE_PLATFORM_OWNER, Role.ROLE_PLATFORM_ADMIN ->
                UserAccessLevel.PlatformAll(principalPlatformId)

            Role.ROLE_PLATFORM_USER ->
                UserAccessLevel.PlatformFiltered(
                    principalPlatformId,
                    listOf(Role.ROLE_PLATFORM_USER)
                )

            Role.ROLE_TENANT -> null
        }
    }
}