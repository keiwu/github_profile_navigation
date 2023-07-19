package kei.su.githubprofile.data.models

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kei.su.githubprofile.service.GithubApiService


/**
 * @param githubApiService provide service to get data from the cloud
 * @param userName name of the user from whom to retrieve the list of the following data
 * @param perPage how many items to retrieve for each page
 *
 * Get the following list from the cloud for this specific user
 *
 */
class FollowingPagingSource(
    val githubApiService: GithubApiService,
    val userName: String,
    val perPage: Int
) : PagingSource<Int, UserItem>() {
    override fun getRefreshKey(state: PagingState<Int, UserItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)

        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserItem> {
        return try {
            val page = params.key ?: 1
            val response = githubApiService.getPagingFollowings(userName = userName, page = page, perPage = perPage)

            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (response.isEmpty()) null else page.plus(1)
            )
        } catch (e : Exception) {
            LoadResult.Error(e)
        }
    }
}