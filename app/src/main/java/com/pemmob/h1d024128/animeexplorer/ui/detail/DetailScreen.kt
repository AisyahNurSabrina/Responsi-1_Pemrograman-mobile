package com.pemmob.h1d024128.animeexplorer.ui.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pemmob.h1d024128.animeexplorer.data.model.Anime
import com.pemmob.h1d024128.animeexplorer.data.model.displayTitle
import com.pemmob.h1d024128.animeexplorer.data.model.releaseYear
import com.pemmob.h1d024128.animeexplorer.ui.UiState
import com.pemmob.h1d024128.animeexplorer.ui.components.ErrorView
import com.pemmob.h1d024128.animeexplorer.ui.components.LoadingView
import com.pemmob.h1d024128.animeexplorer.ui.components.Pill
import com.pemmob.h1d024128.animeexplorer.ui.theme.AnimePurple
import com.pemmob.h1d024128.animeexplorer.ui.theme.AnimePurpleDark
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    animeId: Int,
    viewModel: DetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(animeId) {
        viewModel.loadDetail(animeId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Anime",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->

        when (val state = uiState) {

            // LOADING
            is UiState.Loading -> LoadingView(
                modifier = Modifier.padding(paddingValues),
                message = "Memuat detail..."
            )

            // ERROR
            is UiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadDetail(animeId) },
                modifier = Modifier.padding(paddingValues)
            )

            // DATA
            is UiState.Success -> DetailContent(
                anime = state.data,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(
    anime: Anime,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val synopsis = anime.synopsis ?: "Sinopsis tidak tersedia."
    val genres = anime.genres?.mapNotNull { it.name }.orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // ===== HERO =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(listOf(AnimePurpleDark, AnimePurple)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 28.dp)
        ) {
            Column {
                Text(
                    text = anime.displayTitle(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                anime.titleJapanese?.let { japaneseTitle ->
                    Text(
                        text = japaneseTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                anime.status?.let { status ->
                    Pill(
                        text = status,
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }
            }
        }

        // ===== STAT TILES =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatTile(
                label = "Rating",
                value = anime.score?.let { String.format(Locale.US, "★ %.2f", it) } ?: "-",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = "Tahun",
                value = anime.releaseYear()?.toString() ?: "-",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = "Episode",
                value = anime.episodes?.toString() ?: "-",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = "Tipe",
                value = anime.type ?: "-",
                modifier = Modifier.weight(1f)
            )
        }

        // ===== GENRE =====
        if (genres.isNotEmpty()) {
            Text(
                text = "Genre",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genres.forEach { genre ->
                    Pill(text = genre)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // ===== SINOPSIS (bisa dibuka/tutup) =====
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sinopsis",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = synopsis,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = if (expanded) Int.MAX_VALUE else 6,
                    overflow = TextOverflow.Ellipsis
                )

                if (synopsis.length > 280) {
                    TextButton(onClick = { expanded = !expanded }) {
                        Text(if (expanded) "Tutup" else "Baca selengkapnya")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}