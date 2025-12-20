package com.agrodiary.common

import android.util.Patterns

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        // Простая проверка: не пустой, содержит только цифры и символы форматирования
        val phoneRegex = Regex("^[+]?[0-9\\-\\s()]{7,20}$")
        return phone.isNotBlank() && phone.matches(phoneRegex)
    }

    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 2
    }

    fun isValidPrice(price: Double): Boolean {
        return price >= 0.0
    }

    fun isValidWeight(weight: Float): Boolean {
        return weight >= 0.0f
    }
    
    fun isValidQuantity(quantity: Double): Boolean {
        return quantity >= 0.0
    }
}
