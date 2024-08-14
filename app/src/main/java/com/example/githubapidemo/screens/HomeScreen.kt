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
package com.example.githubapidemo.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.githubapidemo.domain.GitHubUser
import com.example.githubapidemo.model.UIState
import com.example.githubapidemo.navigation.NavigationItem
import com.example.githubapidemo.viewmodel.GitHubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
            .systemBarsPadding()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "GitHub Users List",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onBackPressedDispatcher?.onBackPressed() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = topAppBarColors(
                        containerColor = Color(0xFF6200EE), // Purple color
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            content = {
                Surface(
                    color = MaterialTheme.colorScheme.error,
                    contentColor = Color.Black,
                    modifier = Modifier.padding(it)
                ) {
                    ShowData(navController = navController)
                }
            }
        )
    }
}

@Composable
fun ShowData(viewModel: GitHubViewModel = hiltViewModel(), navController: NavHostController) {
    val uiStateNews: UIState<List<GitHubUser>> by
    viewModel.gitUsersData.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.fetchGitHubUsersData()
    }
    when (uiStateNews) {
        is UIState.Success -> {
            val gitHubUserItemList = (uiStateNews as UIState.Success<List<GitHubUser>>).data
            LazyColumn {
                items(gitHubUserItemList) { gitHubUserItem ->
                    GitUsersRow(gitHubUserItem) {
                        navController.navigate(
                            NavigationItem.UserDetailsScreen.createRoute(
                                gitHubUserItem.id.toString(),
                                gitHubUserItem.login,
                                gitHubUserItem.url,
                                gitHubUserItem.avatarUrl,
                                gitHubUserItem.htmlUrl
                            )
                        )
                    }
                }
            }
        }
        is UIState.Empty -> DisplayUIState(message = "Empty")
        is UIState.Failure -> DisplayUIState(message = "Failed")
        is UIState.Loading -> DisplayUIState(message = "Loading", isLoading = true)
    }
}

@Composable
fun DisplayUIState(message: String, isLoading: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(text = message)
        }
    }
}

@Composable
fun GitUsersRow(gitUser: GitHubUser, onItemClick: (GitHubUser) -> Unit) {
    Card(
        modifier = Modifier.run {
            fillMaxWidth()
                .padding(10.dp)
        },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(Color.White),
        onClick = {
            onItemClick(gitUser)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .padding(12.dp)
                    .size(100.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                tonalElevation = 4.dp,
                color = Color.Black
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = gitUser.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(4.dp)) {
                Text(text = gitUser.login, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(
                    text = "id : " + gitUser.id,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 0.dp)
                )
                Text(
                    text = "Type : " + gitUser.type,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }
        }
    }
}
