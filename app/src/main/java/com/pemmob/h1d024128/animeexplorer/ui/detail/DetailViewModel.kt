package com.pemmob.h1d024128.animeexplorer.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.h1d024128.animeexplorer.data.model.Anime
import com.pemmob.h1d024128.animeexplorer.data.remote.RetrofitInstance
import com.pemmob.h1d024128.animeexplorer.data.repository.AnimeRepository
import com.pemmob.h1d024128.animeexplorer.ui.UiState
import com.pemmob.h1d024128.animeexplorer.ui.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: AnimeRepository = AnimeRepository(RetrofitInstance.api)
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Anime>>(UiState.Loading)
    val uiState: StateFlow<UiState<Anime>> = _uiState.asStateFlow()

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(repository.getAnimeDetail(id))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserMessage())
            }
        }
    }
}
