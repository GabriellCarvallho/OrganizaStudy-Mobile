package com.organizastudy.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.organizastudy.ui.screens.*
import com.organizastudy.viewmodel.*
import com.organizastudy.viewmodel.LivroViewModel
import com.organizastudy.ui.screens.MateriaisScreen

@Composable
fun AppNavigation() {
    val nav = rememberNavController()

    val authVm:    AuthViewModel    = viewModel()
    val subjectVm: SubjectViewModel = viewModel()
    val taskVm:    TaskViewModel    = viewModel()
    val timerVm:   TimerViewModel   = viewModel()

    val rotaAtual = nav.currentBackStackEntryAsState()
        .value?.destination?.route ?: Rotas.HOME

    val inicio = if (authVm.usuarioLogado != null) Rotas.HOME else Rotas.LOGIN

    fun navegar(destino: String) {
        nav.navigate(destino) {
            popUpTo(Rotas.HOME) { saveState = true }
            launchSingleTop = true
            restoreState    = true
        }
    }

    NavHost(navController = nav, startDestination = inicio) {

        composable(Rotas.LOGIN) {
            LoginScreen(
                vm         = authVm,
                onSucesso  = { nav.navigate(Rotas.HOME) { popUpTo(Rotas.LOGIN) { inclusive = true } } },
                onCadastro = { nav.navigate(Rotas.CADASTRO) }
            )
        }

        composable(Rotas.CADASTRO) {
            CadastroScreen(
                vm        = authVm,
                onSucesso = { nav.navigate(Rotas.HOME) { popUpTo(Rotas.LOGIN) { inclusive = true } } },
                onVoltar  = { nav.popBackStack() }
            )
        }

        composable(Rotas.HOME) {
            val uid = authVm.usuarioLogado?.uid ?: ""
            DashboardScreen(
                uid       = uid,
                email     = authVm.usuarioLogado?.email ?: "",
                subjectVm = subjectVm,
                taskVm    = taskVm,
                rotaAtual = rotaAtual,
                onNavegar = ::navegar,
                onLogout  = {
                    authVm.logout()
                    nav.navigate(Rotas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Rotas.DISCIPLINAS) {
            val uid = authVm.usuarioLogado?.uid ?: ""
            SubjectsScreen(
                uid       = uid,
                subjectVm = subjectVm,
                timerVm   = timerVm,
                rotaAtual = rotaAtual,
                onNavegar = ::navegar
            )
        }

        composable(Rotas.TAREFAS) {
            val uid = authVm.usuarioLogado?.uid ?: ""
            val disciplinas by subjectVm.todasSemFiltro.collectAsState()
            TasksScreen(
                uid         = uid,
                disciplinas = disciplinas,
                vm          = taskVm,
                rotaAtual   = rotaAtual,
                onNavegar   = ::navegar
            )
        }

        composable(Rotas.BUSCA) {
            val uid = authVm.usuarioLogado?.uid ?: ""
            BuscaScreen(
                uid       = uid,
                subjectVm = subjectVm,
                taskVm    = taskVm,
                rotaAtual = rotaAtual,
                onNavegar = ::navegar
            )
        }

        // API REST — Retrofit
        composable(Rotas.MATERIAIS) {
            val livroVm: LivroViewModel = viewModel()
            MateriaisScreen(
                vm        = livroVm,
                rotaAtual = rotaAtual,
                onNavegar = ::navegar
            )
        }
    }
}