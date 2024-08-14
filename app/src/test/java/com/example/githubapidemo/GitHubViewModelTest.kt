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
import com.example.githubapidemo.domain.GetGitHubUsersUseCase
import com.example.githubapidemo.domain.GitHubUser
import com.example.githubapidemo.model.UIState
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class GitHubViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getGitHubUsersUseCase: GetGitHubUsersUseCase

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

        viewModel = GitHubViewModel(dispatcherProvider, getGitHubUsersUseCase, networkHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test fetchGitHubUsersData success`() = runTest {
        // Given
        val userList = listOf(
            GitHubUser(
                avatarUrl = "avatar_url",
                eventsUrl = "events_url",
                followersUrl = "followers_url",
                followingUrl = "following_url",
                gistsUrl = "gists_url",
                gravatarId = "",
                htmlUrl = "html_url",
                id = 1,
                login = "mojombo",
                nodeId = "MDQ6VXNlcjE=",
                organizationsUrl = "organizations_url",
                receivedEventsUrl = "received_events_url",
                reposUrl = "repos_url",
                siteAdmin = false,
                starredUrl = "starred_url",
                subscriptionsUrl = "subscriptions_url",
                type = "User",
                url = "url"
            )
        )

        `when`(getGitHubUsersUseCase()).thenReturn(userList)
        `when`(networkHelper.isNetworkAvailable()).thenReturn(true)

        // When
        viewModel.fetchGitHubUsersData()

        advanceUntilIdle()

        // Then
        val result = viewModel.gitUsersData.first()
        Assert.assertTrue(result is UIState.Success)
        Assert.assertEquals(userList, (result as UIState.Success).data)
    }

    @Test
    fun `test fetchGitHubUsersData network unavailable`() = runTest {
        // Given
        `when`(networkHelper.isNetworkAvailable()).thenReturn(false)

        // When
        viewModel.fetchGitHubUsersData()

        advanceUntilIdle()

        // Then
        val result = viewModel.gitUsersData.first()
        Assert.assertTrue(result is UIState.Failure)
        Assert.assertEquals("Network is not available", (result as UIState.Failure).message)
    }

    @Test
    fun `test fetchGitHubUsersData failure`() = runTest {
        // Given
        `when`(networkHelper.isNetworkAvailable()).thenReturn(true)
        `when`(getGitHubUsersUseCase()).thenThrow(RuntimeException("Something went wrong"))

        // When
        viewModel.fetchGitHubUsersData()

        advanceUntilIdle()

        // Then
        val result = viewModel.gitUsersData.first()
        if (result is UIState.Failure) {
            println("Actual error message: ${result.message}")
            Assert.assertEquals(
                "An error occurred: Something went wrong",
                result.message
            )
        } else {
            Assert.fail("Expected UIState.Failure but got $result")
        }
    }

    @Test
    fun `test fetchGitHubUsersData with empty list`() = runTest {
        // Given
        `when`(networkHelper.isNetworkAvailable()).thenReturn(true)
        `when`(getGitHubUsersUseCase()).thenReturn(emptyList())

        // When
        viewModel.fetchGitHubUsersData()

        advanceUntilIdle()

        // Then
        val result = viewModel.gitUsersData.first()
        Assert.assertTrue(result is UIState.Empty)
    }
}
