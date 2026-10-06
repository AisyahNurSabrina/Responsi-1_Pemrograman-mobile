package com.pemmob.h1d024128.animeexplorer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pemmob.h1d024128.animeexplorer.ui.detail.DetailScreen
import com.pemmob.h1d024128.animeexplorer.ui.detail.DetailViewModel
import com.pemmob.h1d024128.animeexplorer.ui.home.HomeScreen
import com.pemmob.h1d024128.animeexplorer.ui.home.HomeViewModel

@Composable
fun AnimeNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {

            val viewModel: HomeViewModel = viewModel()

            HomeScreen(
                viewModel = viewModel,
                onAnimeClick = { animeId ->
                    navController.navigate("detail/$animeId")
                }
            )
        }

        composable(
            route = "detail/{animeId}",
            arguments = listOf(
                navArgument("animeId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val animeId =
                backStackEntry.arguments?.getInt("animeId")

            if (animeId != null) {

                val viewModel: DetailViewModel = viewModel()

                DetailScreen(
                    animeId = animeId,
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}