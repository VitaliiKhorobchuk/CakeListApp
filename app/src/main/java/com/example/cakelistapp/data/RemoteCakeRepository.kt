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
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

// TODO: If offline use becomes a requirement, add a local cache with an explicit freshness
// policy. Today process recreation fetches again and unavailable connectivity shows an error.
class RemoteCakeRepository(private val api: CakeApi) : CakeRepository {
    override suspend fun getCakes(): List<Cake> {
        try {
            val response = api.getCakes()
            if (!response.isSuccessful) throw HttpException(response)
            val cakes = response.body() ?: throw CakeLoadException(CakeLoadError.INVALID_DATA)
            return cakes.map { dto ->
                dto?.toDomain() ?: throw CakeLoadException(CakeLoadError.INVALID_DATA)
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: HttpException) {
            throw CakeLoadException(CakeLoadError.SERVER, exception)
        } catch (exception: JsonParseException) {
            throw CakeLoadException(CakeLoadError.INVALID_DATA, exception)
        } catch (exception: IllegalStateException) {
            // Gson reports an unexpected JSON container (for example an object instead of a list) this way.
            // TODO: Translate parsing failures inside a converter so unrelated IllegalStateExceptions
            // are not misclassified as invalid API data when this repository grows.
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
