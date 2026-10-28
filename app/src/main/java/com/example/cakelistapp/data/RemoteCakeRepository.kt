package com.example.cakelistapp.data

import com.example.cakelistapp.data.remote.CakeApi
import com.example.cakelistapp.data.remote.toDomain
import com.example.cakelistapp.domain.model.Cake
import com.example.cakelistapp.domain.model.CakeLoadError
import com.example.cakelistapp.domain.model.CakeLoadException
import com.example.cakelistapp.domain.repository.CakeRepository
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
import retrofit2.HttpException

class RemoteCakeRepository(private val api: CakeApi) : CakeRepository {
    override suspend fun getCakes(): List<Cake> {
        try {
            val response = api.getCakes()
                ?: throw CakeLoadException(CakeLoadError.INVALID_DATA)
            return response.map { dto ->
                dto?.toDomain() ?: throw CakeLoadException(CakeLoadError.INVALID_DATA)
            }
        } catch (exception: HttpException) {
            throw CakeLoadException(CakeLoadError.SERVER, exception)
        } catch (exception: JsonParseException) {
            throw CakeLoadException(CakeLoadError.INVALID_DATA, exception)
        } catch (exception: MalformedJsonException) {
            throw CakeLoadException(CakeLoadError.INVALID_DATA, exception)
        } catch (exception: EOFException) {
            throw CakeLoadException(CakeLoadError.INVALID_DATA, exception)
        } catch (exception: IOException) {
            throw CakeLoadException(CakeLoadError.NETWORK, exception)
        }
    }
}
