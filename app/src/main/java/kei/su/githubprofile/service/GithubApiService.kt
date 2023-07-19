package kei.su.githubprofile.service

import kei.su.githubprofile.data.models.Users
import kei.su.githubprofile.data.models.UserProfile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {
    @GET("users/{userName}")
    suspend fun getUserProfile(@Path("userName") userName: String) : Response<UserProfile>

    @GET("users/{userName}/followers")
    suspend fun getPagingFollowers(@Path("userName") userName: String, @Query("page") page: Int) : Users

    @GET("users/{userName}/following")
    suspend fun getPagingFollowings(@Path("userName") userName: String, @Query("page") page: Int, @Query("per_page") perPage: Int) : Users
}