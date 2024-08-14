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
package com.example.githubapidemo.model

import com.squareup.moshi.Json

data class GitHubUserItem(
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "events_url") val eventsUrl: String,
    @Json(name = "followers_url") val followersUrl: String,
    @Json(name = "following_url") val followingUrl: String,
    @Json(name = "gists_url") val gistsUrl: String,
    @Json(name = "gravatar_id") val gravatarId: String,
    @Json(name = "html_url") val htmlUrl: String,
    @Json(name = "id") val id: Int,
    @Json(name = "login") val login: String,
    @Json(name = "node_id") val nodeId: String,
    @Json(name = "organizations_url") val organizationsUrl: String,
    @Json(name = "received_events_url") val receivedEventsUrl: String,
    @Json(name = "repos_url") val reposUrl: String,
    @Json(name = "site_admin") val siteAdmin: Boolean,
    @Json(name = "starred_url") val starredUrl: String,
    @Json(name = "subscriptions_url") val subscriptionsUrl: String,
    @Json(name = "type") val type: String,
    @Json(name = "url") val url: String
)
