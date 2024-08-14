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

enum class Screens {
    SPLASH_SCREEN,
    HOME_SCREEN,
    USER_DETAILS_SCREEN
}

sealed class NavigationItem(val route: String) {
    data object Splash : NavigationItem(Screens.SPLASH_SCREEN.name)
    data object HomeScreen : NavigationItem(Screens.HOME_SCREEN.name)
    data object UserDetailsScreen : NavigationItem(
        Screens.USER_DETAILS_SCREEN.name + "/{id}/{login}/{url}/{avatarUrl}/{htmlUrl}"
    ) {
        fun createRoute(id: String, login: String, url: String, avatarUrl: String, htmlUrl: String):
            String {
            val encodedUrl = Uri.encode(url)
            val encodedAvatarUrl = Uri.encode(avatarUrl)
            val encodedHtmlUrl = Uri.encode(htmlUrl)
            return "${Screens.USER_DETAILS_SCREEN.name}/$id/$login/$encodedUrl/" +
                "$encodedAvatarUrl/$encodedHtmlUrl"
        }
    }
}
