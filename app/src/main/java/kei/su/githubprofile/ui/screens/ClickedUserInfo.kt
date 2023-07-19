package kei.su.githubprofile.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kei.su.githubprofile.MainActivity
import kei.su.githubprofile.destinations.PaginatedFollowerDestination
import kei.su.githubprofile.destinations.PaginatedFollowingsDestination
import kei.su.githubprofile.domain.GithubViewModel

/**
 * @param navigator to navigate to another screen
 * @param userName the name of the clicked user
 *
 * Present a screen with the info of the clicked user.
 * This screen can be used for user from both the follower or following list
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
@Destination
fun ClickedUserInfo(navigator: DestinationsNavigator, userName: String){
    val githubViewModel = hiltViewModel<GithubViewModel>()

    LaunchedEffect(key1 = Unit) {
        githubViewModel.getFollowerProfile(userName)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {

        when (val githubEvent = githubViewModel.followerProfile.collectAsStateWithLifecycle().value) {
            is GithubViewModel.GithubEvent.Success -> {
                Log.d(MainActivity.TAG, "followerProfile email " + githubEvent.data.avatar_url)

                Column(modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlideImage(
                        model = githubEvent.data.avatar_url,
                        contentDescription = "avatar",
                    )

                    Text(
                        text = "${githubEvent.data.login}"
                    )

                    Text(
                        modifier = Modifier.clickable {

                            Log.d(MainActivity.TAG, "followers clicked")
                            navigator.navigate(PaginatedFollowerDestination(userName = userName))
                        },
                        text = "Followers: ${githubEvent.data.followers}",
                        color = Color.Blue
                    )

                    Text(
                        modifier = Modifier.clickable {
                            Log.d(MainActivity.TAG, "following clicked")
                            navigator.navigate(PaginatedFollowingsDestination(userName = userName))

                        },
                        text = "Following: ${githubEvent.data.following}",
                        color = Color.Blue
                    )

                    Text(
                        modifier = Modifier.padding(all = 5.dp),
                        text = "Description: ${githubEvent.data.bio}"
                    )
                }
            }
            is GithubViewModel.GithubEvent.Failure -> {
                Log.d(MainActivity.TAG, "error follower profile" + githubEvent.errorText)

                if (githubEvent.errorText == "") {

                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "No user found"
                    )
                }
            }

            is GithubViewModel.GithubEvent.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

            }

            is GithubViewModel.GithubEvent.Empty -> {
                Log.d(MainActivity.TAG, "Event is empty")
            }
        }
    }
}
