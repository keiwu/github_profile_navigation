package kei.su.githubprofile.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kei.su.githubprofile.data.models.FollowerPagingSource
import kei.su.githubprofile.data.models.FollowingPagingSource
import kei.su.githubprofile.util.Resource
import kei.su.githubprofile.service.GithubApiService
import kei.su.githubprofile.data.models.Users
import kei.su.githubprofile.data.models.UserItem
import kei.su.githubprofile.data.models.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GithubRepositoryImplementation @Inject constructor(private val api: GithubApiService) : GithubRepository {
    override suspend fun getProfile(userName: String): Resource<UserProfile> {
        return try {
            val response = api.getUserProfile(userName)

            val result = response.body()

            if (response.isSuccessful && result != null){
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e : Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override fun getFollowersInPagination(userName: String): Flow<PagingData<UserItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = perPage
            ),
            pagingSourceFactory = {
                FollowerPagingSource(
                    githubApiService = api,
                    userName = userName
                )
            }
        ).flow
    }

    override fun getFollowingsInPagination(
        userName: String,
        perPage: Int
    ): Flow<PagingData<UserItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = perPage,
            ),
            pagingSourceFactory = {
                FollowingPagingSource(
                    githubApiService = api,
                    userName = userName,
                    perPage = perPage
                )
            }
        ).flow

    }
}