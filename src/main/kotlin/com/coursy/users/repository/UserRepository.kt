package com.coursy.users.repository

import com.coursy.users.model.User
import com.coursy.users.types.Email
import com.coursy.users.types.Login
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: Email): Boolean
    fun existsByLogin(login: Login): Boolean
    fun removeUserById(id: UUID)
}
