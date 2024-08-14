/*
 * Copyright 2024 Ashok Nayak.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.githubapidemo.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.githubapidemo.screens.HomeScreen
import com.example.githubapidemo.screens.SplashScreen
import com.example.githubapidemo.screens.UserInfoDetails

@Composable
fun ScreensNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavigationItem.Splash.route) {
        composable(route = NavigationItem.Splash.route) {
            SplashScreen(navController)
        }

        composable(route = NavigationItem.HomeScreen.route) {
            HomeScreen(navController)
        }

        composable(
            route = NavigationItem.UserDetailsScreen.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("login") { type = NavType.StringType },
                navArgument("url") { type = NavType.StringType },
                navArgument("avatarUrl") { type = NavType.StringType },
                navArgument("htmlUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Extracting arguments from the NavBackStackEntry
            val userId = backStackEntry.arguments?.getString("id")
            val loginName = backStackEntry.arguments?.getString("login")
            val url = backStackEntry.arguments?.getString("url")?.let { Uri.decode(it) }
            val avatarUrl = backStackEntry.arguments?.getString("avatarUrl")?.let { Uri.decode(it) }
            val htmlUrl = backStackEntry.arguments?.getString("htmlUrl")?.let { Uri.decode(it) }

            // Passing arguments to the UserInfoDetails composable
            UserInfoDetails(
                navController,
                id = userId ?: "",
                login = loginName ?: "",
                url = url ?: "",
                avatarUrl = avatarUrl ?: "",
                htmlUrl = htmlUrl ?: ""
            )
        }
    }
}
