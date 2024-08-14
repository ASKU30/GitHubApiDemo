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
package com.example.githubapidemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubapidemo.domain.GetGitHubUsersUseCase
import com.example.githubapidemo.domain.GitHubUser
import com.example.githubapidemo.model.UIState
import com.example.githubapidemo.utils.dispachperProvider.DispatcherProvider
import com.example.githubapidemo.utils.networkhelper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GitHubViewModel @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val getGitHubUsersUseCase: GetGitHubUsersUseCase,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _gitUsersData = MutableStateFlow<UIState<List<GitHubUser>>>(UIState.Empty)
    val gitUsersData: StateFlow<UIState<List<GitHubUser>>> = _gitUsersData.asStateFlow()

    fun fetchGitHubUsersData() {
        viewModelScope.launch(dispatcher.io) {
            _gitUsersData.emit(UIState.Loading)
            try {
                if (!networkHelper.isNetworkAvailable()) {
                    _gitUsersData.emit(UIState.Failure("Network is not available"))
                    return@launch
                }

                val data = getGitHubUsersUseCase()
                if (data.isNotEmpty()) {
                    _gitUsersData.emit(UIState.Success(data))
                } else {
                    _gitUsersData.emit(UIState.Empty)
                }
            } catch (e: Exception) {
                _gitUsersData.emit(UIState.Failure("An error occurred: ${e.message}"))
            }
        }
    }
}
