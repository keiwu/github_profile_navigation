package kei.su.githubprofile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kei.su.githubprofile.service.GithubApiService
import kei.su.githubprofile.repository.GithubRepository
import kei.su.githubprofile.repository.GithubRepositoryImplementation

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://api.github.com/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideProfileAPI(): GithubApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GithubApiService::class.java)

    @Singleton
    @Provides
    fun provideProfileRepository(api: GithubApiService): GithubRepository = GithubRepositoryImplementation(api)
}