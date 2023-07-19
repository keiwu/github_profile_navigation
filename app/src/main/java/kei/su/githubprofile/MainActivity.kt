package kei.su.githubprofile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import kei.su.githubprofile.domain.GithubViewModel
import kei.su.githubprofile.ui.theme.GitHubProfileTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kei.su.githubprofile.MainActivity.Companion.TAG
import kei.su.githubprofile.destinations.PaginatedFollowerDestination
import kei.su.githubprofile.destinations.PaginatedFollowingsDestination

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        val TAG = this.javaClass.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GitHubProfileTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    // need to rebuild project after adding the following to get rid of error:
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }
            }
        }
    }
}



/**
 * @param navigator to navigate to other screen
 *
 * Initial search screen to search for user profile by entering user name
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(start = true)
fun SearchView(navigator: DestinationsNavigator) {
    val githubViewModel = hiltViewModel<GithubViewModel>()
    var searchText by remember {
        mutableStateOf(TextFieldValue(""))
    }

    var hideKeyboard by remember { mutableStateOf (false) }

    if (hideKeyboard){
        LocalFocusManager.current.clearFocus()
        //reset back to clearFocus to false so the keyboard will stay showing
        hideKeyboard = false
    }

    Column(modifier = Modifier
        .fillMaxWidth(),
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .align(CenterHorizontally),
            horizontalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = searchText,
                onValueChange = { newText ->
                    searchText = newText
                },
                label = {
                    Text("Enter user name")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "user search"
                    )
                },
                maxLines = 1
            )

            Button(
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(5.dp)
                ,
                onClick = {
                    githubViewModel.getUserProfile(searchText.text)
                    hideKeyboard = true
                }
            )
            {
                Text(text = "Search")
            }
        }

         SearchResult(githubViewModel, navigator, searchText.text)
    }
}


/**
 * @param githubViewModel GithubViewModel
 * @param navigator to navigate to other screens
 * @param userName
 *
 * Present the search result for the specific user name
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SearchResult(githubViewModel: GithubViewModel, navigator: DestinationsNavigator, userName: String){
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {

        when (val githubEvent = githubViewModel.profile.collectAsStateWithLifecycle().value) {
            is GithubViewModel.GithubEvent.Success -> {
                Column(modifier = Modifier
                    .align(Center)
                    .fillMaxWidth(),
                    horizontalAlignment = CenterHorizontally
                ) {
                    GlideImage(
                        model = githubEvent.data.avatar_url,
                        contentDescription = "avatar",
                    )

                    Text(
                        text = "${githubEvent.data.login}"
                    )

                    // display the followers count for this user
                    // clicking on this follower count will navigate to a screen with all the followers list paginated
                    Text(
                        modifier = Modifier.clickable {
                          navigator.navigate(PaginatedFollowerDestination(userName = userName))
                        },
                        text = "Followers: ${githubEvent.data.followers}",
                        color = Color.Blue
                    )

                    // display the followings count for this user
                    // clicking on this followings count will navigate to a screen with all the followings list paginated
                    Text(
                        modifier = Modifier.clickable {
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
                Log.d(TAG, githubEvent.errorText)
                if (githubEvent.errorText == ""){
                    Text(
                        modifier = Modifier.align(Center),
                        text = "No user found"
                    )
                }
            }
            is GithubViewModel.GithubEvent.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Center)
                )
            }

            else -> {
                Log.d("TAG", "idle state")
            }
        }
    }
}

