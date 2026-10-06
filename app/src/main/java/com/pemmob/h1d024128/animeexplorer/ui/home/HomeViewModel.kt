package com.pemmob.h1d024128.animeexplorer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.h1d024128.animeexplorer.data.model.Anime
import com.pemmob.h1d024128.animeexplorer.data.model.displayTitle
import com.pemmob.h1d024128.animeexplorer.data.model.releaseYear
import com.pemmob.h1d024128.animeexplorer.data.remote.RetrofitInstance
import com.pemmob.h1d024128.animeexplorer.data.repository.AnimeRepository
import com.pemmob.h1d024128.animeexplorer.ui.UiState
import com.pemmob.h1d024128.animeexplorer.ui.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    RATING("Rating"),
    TERBARU("Terbaru"),
    EPISODE("Episode")
}

data class HomeFilter(
    val query: String = "",
    val sort: SortOption = SortOption.RATING,
    val onlyFavorites: Boolean = false
)

class HomeViewModel(
    private val repository: AnimeRepository = AnimeRepository(RetrofitInstance.api)
) : ViewModel() {

    // Data mentah dari API
    private val _rawState = MutableStateFlow<UiState<List<Anime>>>(UiState.Loading)

    // State kontrol UI
    private val _filter = MutableStateFlow(HomeFilter())
    private val _favorites = MutableStateFlow<Set<Int>>(emptySet())
    private val _isGrid = MutableStateFlow(false)

    val filter: StateFlow<HomeFilter> = _filter.asStateFlow()
    val favorites: StateFlow<Set<Int>> = _favorites.asStateFlow()
    val isGrid: StateFlow<Boolean> = _isGrid.asStateFlow()

    // State yang dibaca UI: data API + filter + sort digabung (Loading / Success / Error)
    val uiState: StateFlow<UiState<List<Anime>>> =
        combine(_rawState, _filter, _favorites) { state, options, favs ->
            when (state) {
                is UiState.Success -> UiState.Success(state.data.applyFilter(options, favs))
                else -> state
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    init {
        loadAnime()
    }

    fun loadAnime() {
        viewModelScope.launch {
            _rawState.value = UiState.Loading
            _rawState.value = try {
                val list = repository.getTopAnime()
                    .filter { it.score != null }
                    .sortedByDescending { it.score }
                UiState.Success(list)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserMessage())
            }
        }
    }

    fun onSearchChange(query: String) {
        _filter.update { it.copy(query = query) }
    }

    fun onSortChange(sort: SortOption) {
        _filter.update { it.copy(sort = sort) }
    }

    fun toggleOnlyFavorites() {
        _filter.update { it.copy(onlyFavorites = !it.onlyFavorites) }
    }

    fun toggleViewMode() {
        _isGrid.update { !it }
    }

    fun toggleFavorite(id: Int) {
        _favorites.update { current ->
            if (id in current) current - id else current + id
        }
    }
}

// Pencarian + filter favorit + pengurutan memakai lambda & collection
private fun List<Anime>.applyFilter(options: HomeFilter, favorites: Set<Int>): List<Anime> {
    val query = options.query.trim()

    return this
        .filter { anime ->
            query.isEmpty() ||
                    anime.displayTitle().contains(query, ignoreCase = true) ||
                    (anime.title ?: "").contains(query, ignoreCase = true)
        }
        .filter { anime -> !options.onlyFavorites || anime.malId in favorites }
        .let { list ->
            when (options.sort) {
                SortOption.RATING -> list.sortedByDescending { it.score ?: 0.0 }
                SortOption.TERBARU -> list.sortedByDescending { it.releaseYear() ?: 0 }
                SortOption.EPISODE -> list.sortedByDescending { it.episodes ?: 0 }
            }
        }
}
