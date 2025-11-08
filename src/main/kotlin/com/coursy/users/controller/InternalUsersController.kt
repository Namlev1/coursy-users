package com.coursy.users.controller

import com.coursy.users.internal.auth.PlatformRegistrationRequest
import com.coursy.users.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RequestMapping("/api/internal/users")
@RestController
class InternalUsersController(
    private val userService: UserService,
    private val httpFailureResolver: HttpFailureResolver
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UUID) = userService
        .getUser(id)
        .fold(
            { failure -> httpFailureResolver.handleFailure(failure) },
            { response -> ResponseEntity.status(HttpStatus.OK).body(response) }
        )

    @PostMapping("/owner")
    fun createOwner(@RequestBody request: PlatformRegistrationRequest) = userService
        .createOwner(request)
        .fold(
            { failure -> httpFailureResolver.handleFailure(failure) },
            { response -> ResponseEntity.status(HttpStatus.OK).body(response) }
        )
}