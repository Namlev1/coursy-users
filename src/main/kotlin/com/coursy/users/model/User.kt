package com.coursy.users.model

import com.coursy.users.types.Email
import com.coursy.users.types.Name
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import org.hibernate.Hibernate
import java.util.*

@Entity(name = "_user")
class User(
    @Id
    var id: UUID = UUID.randomUUID(),
    var platformId: UUID?,
    var email: Email,
    var firstName: Name,
    var lastName: Name,
    @Enumerated(EnumType.STRING)
    var role: Role
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
