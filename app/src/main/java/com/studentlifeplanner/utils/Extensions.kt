package com.studentlifeplanner.utils

import android.content.Context
import android.widget.Toast
import java.security.MessageDigest

// String Extensions
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidUsername(): Boolean {
    return this.length in Constants.MIN_USERNAME_LENGTH..Constants.MAX_USERNAME_LENGTH &&
            this.matches(Regex("^[a-zA-Z0-9_]+$"))
}

fun String.isValidPassword(): Boolean {
    return this.length in Constants.MIN_PASSWORD_LENGTH..Constants.MAX_PASSWORD_LENGTH &&
            this.matches(Regex(".*[0-9].*"))
}

fun String.hashPassword(): String {
    val bytes = this.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}
