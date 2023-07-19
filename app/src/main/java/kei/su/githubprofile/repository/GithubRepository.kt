package kei.su.githubprofile.repository

import androidx.paging.PagingData
import kei.su.githubprofile.data.models.Users
import kei.su.githubprofile.data.models.UserItem
import kei.su.githubprofile.util.Resource
import kei.su.githubprofile.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
    val perPage: Int
        get() = 30

    val page: Int
        get() = 1

    suspend fun getProfile(userName: String): Resource<UserProfile>

    fun getFollowersInPagination(userName: String): Flow<PagingData<UserItem>>

    fun getFollowingsInPagination(userName: String, perPage: Int): Flow<PagingData<UserItem>>
}