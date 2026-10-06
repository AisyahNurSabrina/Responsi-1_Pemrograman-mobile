package com.pemmob.h1d024128.animeexplorer.data.repository

import com.pemmob.h1d024128.animeexplorer.data.model.Anime
import com.pemmob.h1d024128.animeexplorer.data.remote.TenraiApiService

class AnimeRepository(private val api: TenraiApiService) {

    suspend fun getTopAnime(): List<Anime> = api.getTopAnime().data

    suspend fun getAnimeDetail(id: Int): Anime = api.getAnimeDetail(id).data
}