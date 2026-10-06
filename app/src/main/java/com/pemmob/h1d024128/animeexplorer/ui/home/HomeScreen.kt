package com.pemmob.h1d024128.animeexplorer.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pemmob.h1d024128.animeexplorer.data.model.Anime
import com.pemmob.h1d024128.animeexplorer.data.model.displayTitle
import com.pemmob.h1d024128.animeexplorer.data.model.releaseYear
import com.pemmob.h1d024128.animeexplorer.ui.UiState
import com.pemmob.h1d024128.animeexplorer.ui.components.ErrorView
import com.pemmob.h1d024128.animeexplorer.ui.components.FavoriteButton
import com.pemmob.h1d024128.animeexplorer.ui.components.ScoreBadge
import com.pemmob.h1d024128.animeexplorer.ui.components.SkeletonCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAnimeClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isGrid by viewModel.isGrid.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Anime Explorer",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            SearchField(
                query = filter.query,
                onQueryChange = viewModel::onSearchChange
            )

            FilterRow(
                filter = filter,
                isGrid = isGrid,
                onSortChange = viewModel::onSortChange,
                onToggleFavorites = viewModel::toggleOnlyFavorites,
                onToggleGrid = viewModel::toggleViewMode
            )

            when (val state = uiState) {

                // LOADING
                is UiState.Loading -> LoadingList(modifier = Modifier.weight(1f))

                // ERROR
                is UiState.Error -> ErrorView(
                    message = state.message,
                    onRetry = viewModel::loadAnime,
                    modifier = Modifier.weight(1f)
                )

                // DATA
                is UiState.Success -> AnimeContent(
                    animeList = state.data,
                    favorites = favorites,
                    isGrid = isGrid,
                    onAnimeClick = onAnimeClick,
                    onToggleFavorite = viewModel::toggleFavorite,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Cari judul anime...") },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Hapus pencarian"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(
    filter: HomeFilter,
    isGrid: Boolean,
    onSortChange: (SortOption) -> Unit,
    onToggleFavorites: () -> Unit,
    onToggleGrid: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SortOption.entries.forEach { option ->
            FilterChip(
                selected = filter.sort == option,
                onClick = { onSortChange(option) },
                label = { Text(option.label) }
            )
        }

        FilterChip(
            selected = filter.onlyFavorites,
            onClick = onToggleFavorites,
            label = { Text("♥ Favorit") }
        )

        FilterChip(
            selected = isGrid,
            onClick = onToggleGrid,
            label = { Text("Grid") }
        )
    }
}

@Composable
private fun LoadingList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(6) {
            SkeletonCard()
        }
    }
}

@Composable
private fun AnimeContent(
    animeList: List<Anime>,
    favorites: Set<Int>,
    isGrid: Boolean,
    onAnimeClick: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (animeList.isEmpty()) {
        EmptyView(modifier = modifier)
    } else {
        Column(modifier = modifier) {

            Text(
                text = "${animeList.size} anime",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            if (isGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = animeList, key = { it.malId }) { anime ->
                        AnimeGridCard(
                            anime = anime,
                            isFavorite = anime.malId in favorites,
                            onClick = { onAnimeClick(anime.malId) },
                            onFavoriteClick = { onToggleFavorite(anime.malId) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = animeList, key = { it.malId }) { anime ->
                        AnimeListCard(
                            anime = anime,
                            isFavorite = anime.malId in favorites,
                            onClick = { onAnimeClick(anime.malId) },
                            onFavoriteClick = { onToggleFavorite(anime.malId) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tidak ada anime yang cocok",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Coba kata kunci lain atau matikan filter favorit.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeListCard(
    anime: Anime,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreBadge(score = anime.score)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = anime.displayTitle(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = anime.metaInfo(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FavoriteButton(isFavorite = isFavorite, onClick = onFavoriteClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeGridCard(
    anime: Anime,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreBadge(score = anime.score, size = 52.dp)
                FavoriteButton(isFavorite = isFavorite, onClick = onFavoriteClick)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = anime.displayTitle(),
                style = MaterialTheme.typography.titleSmall,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = anime.metaInfo(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// "2013 • 25 eps • TV" — memakai collection (listOfNotNull) dan lambda (let)
private fun Anime.metaInfo(): String =
    listOfNotNull(
        releaseYear()?.toString(),
        episodes?.let { "$it eps" },
        type
    ).joinToString(" • ").ifEmpty { "-" }
