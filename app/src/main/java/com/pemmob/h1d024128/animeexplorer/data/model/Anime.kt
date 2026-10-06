package com.pemmob.h1d024128.animeexplorer.data.model

import com.google.gson.annotations.SerializedName

data class AnimeListResponse(
    val data: List<Anime>
)

data class AnimeDetailResponse(
    val data: Anime
)

data class Anime(
    @SerializedName("mal_id")
    val malId: Int,

    val title: String?,

    @SerializedName("title_english")
    val titleEnglish: String?,

    @SerializedName("title_japanese")
    val titleJapanese: String?,

    val score: Double?,

    val year: Int?,

    val episodes: Int?,

    val status: String?,

    val type: String?,

    val synopsis: String?,

    val genres: List<Genre>?,

    val aired: Aired?
)

data class Genre(
    val name: String?
)

data class Aired(
    val prop: AiredProp?
)

data class AiredProp(
    val from: AiredDate?
)

data class AiredDate(
    val year: Int?
)

// Mengambil tahun rilis.
// Jika field year kosong, gunakan tahun dari tanggal tayang pertama.
fun Anime.releaseYear(): Int? {
    return year ?: aired?.prop?.from?.year
}

// Mengambil judul anime dengan beberapa fallback.
fun Anime.displayTitle(): String {
    return titleEnglish
        ?: title
        ?: titleJapanese
        ?: "Tanpa judul"
}