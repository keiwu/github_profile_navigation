package kei.su.githubprofile.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kei.su.githubprofile.MainActivity
import kei.su.githubprofile.destinations.ClickedUserInfoDestination
import kei.su.githubprofile.domain.GithubViewModel

/**
 * @param userName for use of the searching of this user's follower
 * @param navigator to navigate to the other screen
 *
 * Display the user's follower in a list.
 * When scroll to the bottom of the list, if there are more items it will append to the end of the list
 *
 */
@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
@Destination
fun PaginatedFollower(userName: String, navigator: DestinationsNavigator){
    val viewModel = hiltViewModel<GithubViewModel>()

    val followers = viewModel.getPaginatedFollowers(userName).collectAsLazyPagingItems()
    var clickedFollower : String? by remember { mutableStateOf(null) }
    var showFollower: Boolean by remember { mutableStateOf(false) }

    LazyColumn{
        stickyHeader {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(all = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$userName's followers",
                    fontSize = 24.sp
                )
            }
        }
        items(
            count = followers.itemCount,
            key = {
                it
            },

            ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        clickedFollower = followers[it]?.login
                        showFollower = true
                    }
            ) {
                GlideImage(
                    model = followers[it]?.avatar_url,
                    contentDescription = "avatar",
                    modifier = Modifier
                        .padding(5.dp, 5.dp, 5.dp, 5.dp)
                        .size(35.dp)
                )
                Text(
                    text = "${followers[it]?.login}",
                    modifier = Modifier
                        .padding(5.dp, 0.dp, 0.dp, 0.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        when (followers.loadState.refresh){
            is LoadState.Error -> {
                Log.d(MainActivity.TAG, "Refresh load error")
            }

            is LoadState.Loading -> {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ){
                        Text(text = "Refresh Loading")
                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            }

            is LoadState.NotLoading -> {
                // handle LoadState.NotLoading
                if (followers.loadState.append.endOfPaginationReached){
                    Log.d(MainActivity.TAG, "End of database")
                } else {
                    Log.d(MainActivity.TAG, "Loading is halted")
                }
            }
        }

        when (followers.loadState.append){
            is LoadState.Error -> {
                Log.d(MainActivity.TAG, "Append load error")
            }

            is LoadState.Loading -> {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,

                        ){
                        Text(text = "Append Refresh Loading")
                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            }


            is LoadState.NotLoading -> {
                // handle LoadState.NotLoading
                if (followers.loadState.append.endOfPaginationReached){
                    Log.d(MainActivity.TAG, "End of database")
                } else {
                    Log.d(MainActivity.TAG, "Loading is halted")
                }
            }
        }
    }


    if (showFollower){
        clickedFollower?.let {
            navigator.navigate(ClickedUserInfoDestination(it))
        }

        showFollower = false
    }
}