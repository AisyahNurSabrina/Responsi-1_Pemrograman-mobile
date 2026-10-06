package com.pemmob.h1d024128.animeexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pemmob.h1d024128.animeexplorer.ui.navigation.AnimeNavigation
import com.pemmob.h1d024128.animeexplorer.ui.theme.AnimeExplorerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AnimeExplorerTheme {
                AnimeNavigation()
            }
        }
    }
}