package com.three_squared.animationtest

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.drawerlayout.widget.DrawerLayout
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.three_squared.animationtest.ui.theme.AnimationTestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val toolBar : Toolbar = findViewById(R.id.toolbar)
        //toolBar.inflateMenu(R.menu.app_menu)

        /*toolBar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                println("item pressed")
                if (item != null) {
                    val title = item.title
                    println("item title is $title")
                } else {
                    println("No item pressed")
                }
                return false
            }

        })*/
        setSupportActionBar(toolBar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        appBarConfiguration = AppBarConfiguration(
            navController.graph, drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //for bottom nav
        //val navController = findNavController(R.id.nav_host_fragment)

        val bottomNav : BottomNavigationView = findViewById(R.id.bottonNav)

        bottomNav.setupWithNavController(navController)

        /*val composeView : ComposeView = findViewById(R.id.compose_view)

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Box() {
                    Column {
                        val searchButton = SearchButton()
                        searchButton.searchButton()
                        val errorMessage = ErrorMessage()
                        errorMessage.errorMessage()
                    }
                }
            }
        }*/
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println("item pressed was ${item.title}")
        return super.onOptionsItemSelected(item)
    }
}

/*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val searchButton = SearchButton()
        setContent {
            AnimationTestTheme {
                // A surface container using the 'background' color from the theme
                searchButton.searchButton()
            }
        }
    }
}*/

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AnimationTestTheme {
        Greeting("Android")
    }
}

@Preview()
@Composable
fun animationPreview() {
    val searchButton = SearchButton()
    searchButton.searchButton()
}

@Preview
@Composable
fun errorPreview() {
    val errorMessage = ErrorMessage()
    errorMessage.errorMessage()
}

class ErrorMessage {

    @Composable
    fun errorMessage() {
        var currentState by remember { mutableStateOf(ErrorMessageState.SHOW) }

        val errorMessageAnimation = errorMessageAnimation(state = currentState)

        Box(
            Modifier
                .height(40.dp)
                .fillMaxWidth().clickable {
                    if (currentState == ErrorMessageState.SHOW) {
                        currentState = ErrorMessageState.HIDE
                    } else {
                        currentState = ErrorMessageState.SHOW
                    }
                }
                .alpha(errorMessageAnimation.alpha.value)) {
            Row {
                Icon(Icons.Rounded.Warning, contentDescription = "error", tint = Color.Red)
                Text("Error message", color = Color.Red)
            }
        }
    }

    @Composable
    fun errorMessageAnimation(state : ErrorMessageState) : ErrorMessageAnimationData {
        val transition = updateTransition(state, label = "errorMessageAnimation",)

        val alpha = transition.animateFloat(label = "errorMessageAlphaAnimation") { state ->
            when (state) {
                ErrorMessageState.HIDE -> 0f
                ErrorMessageState.SHOW -> 1f
            }
        }

        return remember(transition) { ErrorMessageAnimationData(alpha) }
    }
}

data class ErrorMessageAnimationData (val alpha : State<Float>)

enum class ErrorMessageState {
    SHOW,
    HIDE
}

class SearchButton {

    @Composable
    fun searchButton() {

        var currentState by remember { mutableStateOf(SearchButtonState.ENABLED) }

        val animationData = buttonAnimation(buttonState = currentState)

        Button(onClick = {
            val testBuildConfig : String = BuildConfig.apiKey

            val key = testBuildConfig;//Base64.getDecoder().decode(testBuildConfig).decodeToString()
            println("test data was $key")

            when(currentState) {
                SearchButtonState.ENABLED -> currentState = SearchButtonState.LOADING
                SearchButtonState.LOADING -> currentState = SearchButtonState.DISABLED
                SearchButtonState.DISABLED -> currentState = SearchButtonState.ENABLED
            }
        },
            Modifier
                .alpha(animationData.alpha.value)
                .height(animationData.height.value)
                .width(animationData.width.value),
            colors = ButtonDefaults.buttonColors( backgroundColor = animationData.color.value)) {
            buttonContent(buttonState = currentState)
        }
    }
    
    @Composable
    fun buttonContent(buttonState : SearchButtonState) {
        if (buttonState == SearchButtonState.LOADING) {
            CircularProgressIndicator(Modifier.height(40.dp))
        } else if (buttonState == SearchButtonState.ENABLED) {
            Text("Search")
        } else {
            Text("Disabled")
        }
    }

    @Composable
    fun buttonAnimation(buttonState : SearchButtonState) : ButtonAnimationData {
        val transition = updateTransition(buttonState, label = "buttonAnimation",)
        val color = transition.animateColor(label = "buttonColorAnimation") { state ->
            when (state) {
                SearchButtonState.DISABLED -> Color.Gray
                SearchButtonState.ENABLED -> MaterialTheme.colors.primary
                SearchButtonState.LOADING -> Color.White
            }
        }

        val alpha = transition.animateFloat(label = "buttonAlphaAnimation") { state ->
            when (state) {
                SearchButtonState.DISABLED -> 0.2f
                SearchButtonState.ENABLED -> 1f
                SearchButtonState.LOADING -> 1f
            }
        }

        val width = transition.animateDp(label = "buttonWidthAnimation") { state ->
            when(state) {
                SearchButtonState.DISABLED -> 100.dp
                SearchButtonState.ENABLED -> 150.dp
                SearchButtonState.LOADING -> 100.dp
            }
        }

        val height = transition.animateDp(label = "buttonColorAnimation") { state ->
            when(state) {
                SearchButtonState.DISABLED -> 40.dp
                SearchButtonState.ENABLED -> 40.dp
                SearchButtonState.LOADING -> 60.dp
            }
        }

        return remember(transition) { ButtonAnimationData(color, alpha, height, width) }
    }

}

data class ButtonAnimationData(val color: State<Color>,
                               val alpha : State<Float>,
                               val height : State<Dp>,
                               val width : State<Dp>)


enum class SearchButtonState {
    ENABLED,
    DISABLED,
    LOADING,
}