package com.pemmob.h1d024128.animeexplorer.data.remote

import com.pemmob.h1d024128.animeexplorer.data.model.AnimeDetailResponse
import com.pemmob.h1d024128.animeexplorer.data.model.AnimeListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TenraiApiService {

    @GET("top/anime")
    suspend fun getTopAnime(@Query("limit") limit: Int = 25): AnimeListResponse

    @GET("anime/{id}")
    suspend fun getAnimeDetail(@Path("id") id: Int): AnimeDetailResponse
}