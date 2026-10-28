package com.example.cakelistapp.domain.model

enum class CakeLoadError {
    NETWORK,
    SERVER,
    INVALID_DATA,
    UNKNOWN,
}

class CakeLoadException(
    val error: CakeLoadError,
    cause: Throwable? = null,
) : Exception(error.name, cause)
