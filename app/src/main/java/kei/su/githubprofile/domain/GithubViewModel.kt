package kei.su.githubprofile.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kei.su.githubprofile.data.models.UserItem
import kei.su.githubprofile.util.Resource
import kei.su.githubprofile.data.models.UserProfile
import kei.su.githubprofile.repository.GithubRepositoryImplementation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GithubViewModel @Inject constructor(private val repository: GithubRepositoryImplementation): ViewModel() {
    sealed class GithubEvent<out T> {
        class Success<out T>(val data: T): GithubEvent<T>()
        class Failure(val errorText: String): GithubEvent<Nothing>()
        object Loading: GithubEvent<Nothing>()
        object Empty: GithubEvent<Nothing>()
    }

    private val _profile = MutableStateFlow<GithubEvent<UserProfile>>(GithubEvent.Empty)
    val profile: StateFlow<GithubEvent<UserProfile>> = _profile

    private val _followerProfile = MutableStateFlow<GithubEvent<UserProfile>>(GithubEvent.Empty)
    val followerProfile: StateFlow<GithubEvent<UserProfile>> = _followerProfile


    /**
     * @param userName name of the user to get the profile from
     *
     * Get the user's profile from the cloud
     *
     */

    fun getUserProfile(userName: String) {
        viewModelScope.launch {
            _profile.value = GithubEvent.Loading
            when (val response = repository.getProfile(userName)){
                is Resource.Success ->
                    if (response.data != null) {
                        _profile.value = GithubEvent.Success(response.data)
                    } else {
                        _profile.value = GithubEvent.Failure("Unexpected error")
                    }
                is Resource.Error -> {
                    _profile.value = GithubEvent.Failure(response.message!!)
                }
            }
        }
    }

    /**
     * @param userName the name of the user for retrieve the follower list
     *
     * Get the list of followers of this user
     */
    fun getFollowerProfile(userName: String) {
        viewModelScope.launch {
            _followerProfile.value = GithubEvent.Loading
            when (val response = repository.getProfile(userName)){
                is Resource.Success ->
                    if (response.data != null) {
                        _followerProfile.value = GithubEvent.Success(response.data)
                    } else {
                        _followerProfile.value = GithubEvent.Failure("Unexpected error")
                    }
                is Resource.Error -> {
                    _followerProfile.value = GithubEvent.Failure(response.message!!)
                }
            }
        }
    }


    /**
     * @param userName the name of the user to retrieve the paginated followers
     *
     * Get the paginated follower list of the user
     *
     */
    fun getPaginatedFollowers(userName: String): Flow<PagingData<UserItem>> =
        repository.getFollowersInPagination(userName).cachedIn(viewModelScope)


    /**
     *  @param userName the name of the user to retrieve the paginated followings
     *
     *  Get the paginated following list of the user
     */
    fun getPaginatedFollowings(userName: String): Flow<PagingData<UserItem>> =
        repository.getFollowingsInPagination(userName = userName, perPage = repository.perPage)
}