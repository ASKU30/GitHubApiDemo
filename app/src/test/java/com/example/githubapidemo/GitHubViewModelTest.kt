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
package com.example.githubapidemo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.githubapidemo.model.GitHubUserItem
import com.example.githubapidemo.model.UIState
import com.example.githubapidemo.repository.GithubUserRepository
import com.example.githubapidemo.utils.dispachperProvider.DispatcherProvider
import com.example.githubapidemo.utils.networkhelper.NetworkHelper
import com.example.githubapidemo.viewmodel.GitHubViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class GitHubViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: GithubUserRepository

    @Mock
    private lateinit var networkHelper: NetworkHelper

    private lateinit var viewModel: GitHubViewModel
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        testDispatcher = StandardTestDispatcher()

        Dispatchers.setMain(testDispatcher)

        val dispatcherProvider = object : DispatcherProvider {
            override val main = testDispatcher
            override val io = testDispatcher
            override val default = testDispatcher
        }

        viewModel = GitHubViewModel(dispatcherProvider, repository, networkHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchGitHubUsersData returns success when data is available`() = runTest {
        // Given
        val gitHubUsers = listOf(
            GitHubUserItem(
                login = "mojombo",
                avatarUrl = "https://avatars.githubusercontent.com/u/1?v=3",
                htmlUrl = "https://github.com/mojombo",
                id = 1,
                nodeId = "MDQ6VXNlcjE=",
                url = "https://api.github.com/users/mojombo",
                followersUrl = "https://api.github.com/users/mojombo/followers",
                followingUrl = "https://api.github.com/users/mojombo/following{/other_user}",
                gistsUrl = "https://api.github.com/users/mojombo/gists{/gist_id}",
                gravatarId = "",
                organizationsUrl = "https://api.github.com/users/mojombo/orgs",
                receivedEventsUrl = "https://api.github.com/users/mojombo/received_events",
                type = "User",
                reposUrl = "https://api.github.com/users/mojombo/repos",
                eventsUrl = "https://api.github.com/users/mojombo/events{/privacy}",
                siteAdmin = false,
                starredUrl = "https://api.github.com/users/mojombo/starred{/owner}{/repo}",
                subscriptionsUrl = "https://api.github.com/users/mojombo/subscriptions"
            ),
            GitHubUserItem(
                login = "defunkt",
                avatarUrl = "https://avatars.githubusercontent.com/u/2?v=3",
                htmlUrl = "https://github.com/defunkt",
                id = 2,
                nodeId = "MDQ6VXNlcjI=",
                url = "https://api.github.com/users/defunkt",
                followersUrl = "https://api.github.com/users/defunkt/followers",
                followingUrl = "https://api.github.com/users/defunkt/following{/other_user}",
                gistsUrl = "https://api.github.com/users/defunkt/gists{/gist_id}",
                gravatarId = "",
                organizationsUrl = "https://api.github.com/users/defunkt/orgs",
                receivedEventsUrl = "https://api.github.com/users/defunkt/received_events",
                type = "User",
                reposUrl = "https://api.github.com/users/defunkt/repos",
                eventsUrl = "https://api.github.com/users/defunkt/events{/privacy}",
                siteAdmin = false,
                starredUrl = "https://api.github.com/users/defunkt/starred{/owner}{/repo}",
                subscriptionsUrl = "https://api.github.com/users/defunkt/subscriptions"
            )
        )

        // Stubbing repository and network helper
        `when`(networkHelper.isNetworkAvailable()).thenReturn(true)
        `when`(repository.getGitUsers()).thenReturn(gitHubUsers)

        // When
        viewModel.fetchGitHubUsersData() // Trigger the flow to emit values

        // Then
        viewModel.gitUsersData.test {
            assertEquals(UIState.Empty, awaitItem())
            assertEquals(UIState.Loading, awaitItem())
            assertEquals(UIState.Success(gitHubUsers), awaitItem())
            cancelAndConsumeRemainingEvents() // Optional: Ensure no other events remain
        }
    }

    @Test
    fun `fetchGitHubUsersData returns error when no data is available`() = runTest {
        // Given
        `when`(networkHelper.isNetworkAvailable()).thenReturn(true)
        `when`(repository.getGitUsers()).thenReturn(emptyList())

        // When
        viewModel.fetchGitHubUsersData() // Trigger the flow to emit values

        // Then
        viewModel.gitUsersData.test {
            // Verify the initial state
            assertEquals(UIState.Empty, awaitItem())

            // Verify the loading state
            assertEquals(UIState.Loading, awaitItem())

            // Verify the empty state
            assertEquals(UIState.Empty, awaitItem())

            // Ensure no additional events remain
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetchGitHubUsersData returns failure when an exception is thrown`() = runTest {
        // Given
        `when`(networkHelper.isNetworkAvailable()).thenReturn(true)
        `when`(repository.getGitUsers()).thenThrow(RuntimeException("No data available"))

        // When
        viewModel.fetchGitHubUsersData() // Trigger the flow to emit values

        // Then
        viewModel.gitUsersData.test {
            // Verify the initial state
            assertEquals(UIState.Empty, awaitItem())

            // Verify the loading state
            assertEquals(UIState.Loading, awaitItem())

            // Verify the failure state with the actual formatted error message
            assertEquals(UIState.Failure("An error occurred: No data available"), awaitItem())

            // Ensure no additional events remain
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `test fetchGitHubUsersData when network is not available`(): Unit = runTest {
        // Given
        `when`(networkHelper.isNetworkAvailable()).thenReturn(false)

        // When
        viewModel.fetchGitHubUsersData() // Trigger the flow to emit values

        advanceUntilIdle()
        // Then
        val result = viewModel.gitUsersData.first()
        assertEquals(UIState.Failure("Network is not available"), result)
    }
}
