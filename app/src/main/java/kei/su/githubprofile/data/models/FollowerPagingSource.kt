package kei.su.githubprofile.data.models

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kei.su.githubprofile.service.GithubApiService


/**
 * @param githubApiService provide service to get the data from cloud
 * @param userName the name of the user to pass to the service to get data from
 *
 * Get the followers of the given user in paginated form
 */
class FollowerPagingSource(
    private val githubApiService: GithubApiService,
    private val userName: String
): PagingSource<Int, UserItem>() {
    override fun getRefreshKey(state: PagingState<Int, UserItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserItem> {
        return try {
            val page = params.key ?: 1
            val response = githubApiService.getPagingFollowers(userName = userName, page = page)

            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (response.isEmpty()) null else page.plus(1),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}