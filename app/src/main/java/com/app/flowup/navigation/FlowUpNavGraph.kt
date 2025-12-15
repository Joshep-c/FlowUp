package com.app.flowup.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.flowup.ui.screens.addactivity.AddActivityScreen
import com.app.flowup.ui.screens.home.HomeScreen

/**
 * Grafo de navegación de la aplicación.
 * Define todas las pantallas y las transiciones entre ellas.
 *
 * @param navController Controlador de navegación
 * @param startDestination Pantalla inicial (por defecto: Home)
 */
@Composable
fun FlowUpNavGraph(
    navController: NavHostController,
    startDestination: String = NavRoutes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // PANTALLA PRINCIPAL (HOME)

        composable(route = NavRoutes.HOME) {
            HomeScreen(
                onNavigateToAddActivity = {
                    navController.navigate(NavRoutes.ADD_ACTIVITY)
                }
            )
        }

        // PANTALLA AGREGAR ACTIVIDAD

        composable(route = NavRoutes.ADD_ACTIVITY) {
            AddActivityScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // PANTALLA EDITAR ACTIVIDAD (FUTURA)

        // TODO: Implementar cuando se requiera funcionalidad de edición
        /*
        composable(
            route = NavRoutes.EDIT_ACTIVITY,
            arguments = listOf(
                navArgument("activityId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getLong("activityId") ?: return@composable
            EditActivityScreen(
                activityId = activityId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        */
    }
}

