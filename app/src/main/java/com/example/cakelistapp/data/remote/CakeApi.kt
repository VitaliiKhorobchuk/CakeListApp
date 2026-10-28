package com.example.cakelistapp.data.remote

import retrofit2.Response
import retrofit2.http.GET

// TODO: For larger feeds, consider Paging 3 once the backend supports page/cursor requests.
// Coordinate ordering and duplicate handling across pages, and expose append loading/retry states.
// The current static JSON endpoint returns the entire list, so it cannot reduce network load via paging.
interface CakeApi {
    @GET("Waracle/mobile-coding-test-api/refs/heads/main/cakes")
    suspend fun getCakes(): Response<List<CakeDto?>>
}
