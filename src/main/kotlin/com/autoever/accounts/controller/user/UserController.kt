package com.autoever.accounts.controller.user

import org.springframework.stereotype.Controller

@Controller(UserController.BASE_URL)
class UserController {
    companion object {
        const val BASE_URL = "/users"
    }
}