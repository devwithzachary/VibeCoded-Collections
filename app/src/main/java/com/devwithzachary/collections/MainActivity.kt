package com.devwithzachary.collections

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devwithzachary.collections.ui.collectiondetails.CollectionDetailsScreen
import com.devwithzachary.collections.ui.collections.CollectionsScreen
import com.devwithzachary.collections.ui.theme.CollectionsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollectionsTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "collections",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("collections") {
                            CollectionsScreen(
                                onCollectionClick = { collectionId ->
                                    navController.navigate("collections/$collectionId")
                                }
                            )
                        }
                        composable(
                            "collections/{collectionId}",
                            arguments = listOf(navArgument("collectionId") { type = NavType.IntType })
                        ) {
                            CollectionDetailsScreen()
                        }
                    }
                }
            }
        }
    }
}
