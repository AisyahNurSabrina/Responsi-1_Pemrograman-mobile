package com.pemmob.h1d024128.animeexplorer.ui

import retrofit2.HttpException
import java.io.IOException

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

// Mengubah exception teknis menjadi pesan yang mudah dipahami pengguna.
fun Throwable.toUserMessage(): String = when (this) {
    is IOException -> "Tidak bisa terhubung ke server. Periksa koneksi internet kamu."
    is HttpException -> "Server sedang bermasalah (kode ${code()}). Coba lagi sebentar lagi."
    else -> message ?: "Terjadi kesalahan, coba lagi."
}
