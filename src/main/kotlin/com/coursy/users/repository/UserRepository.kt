package com.coursy.users.repository

import com.coursy.users.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    fun removeUserById(id: UUID)
    fun findByEmail(email: String): User?
}
