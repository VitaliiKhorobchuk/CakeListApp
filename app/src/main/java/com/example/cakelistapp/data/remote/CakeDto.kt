package com.example.cakelistapp.data.remote

import com.example.cakelistapp.domain.model.Cake
import com.example.cakelistapp.domain.model.CakeLoadError
import com.example.cakelistapp.domain.model.CakeLoadException
import com.google.gson.annotations.SerializedName

data class CakeDto(
    @SerializedName("title") val title: String?,
    @SerializedName("desc") val description: String?,
    @SerializedName("image") val imageUrl: String?,
)

internal fun CakeDto.toDomain(): Cake {
    val cakeTitle = title?.takeIf { it.isNotBlank() }
        ?: throw CakeLoadException(CakeLoadError.INVALID_DATA)
    val cakeDescription = description
        ?: throw CakeLoadException(CakeLoadError.INVALID_DATA)
    return Cake(
        title = cakeTitle,
        description = cakeDescription,
        imageUrl = imageUrl.orEmpty(),
    )
}
