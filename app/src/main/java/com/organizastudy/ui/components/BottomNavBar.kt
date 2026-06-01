package com.organizastudy.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organizastudy.navigation.Rotas
import com.organizastudy.ui.theme.Azul

data class ItemNav(val rota: String, val label: String, val icone: ImageVector)


val itensNav = listOf(
    ItemNav(Rotas.HOME, "Início", Icons.Filled.Home),
    ItemNav(Rotas.DISCIPLINAS, "Disciplinas", Icons.Outlined.MenuBook),
    ItemNav(Rotas.TAREFAS,     "Tarefas",     Icons.Outlined.CheckBox),
    ItemNav(Rotas.BUSCA,       "Busca",       Icons.Filled.Search),
)

@Composable
fun BottomNavBar(rotaAtual: String, onNavegar: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.height(64.dp)
    ) {
        itensNav.forEach { item ->
            val sel = rotaAtual == item.rota
            NavigationBarItem(
                selected = sel,
                onClick  = { if (!sel) onNavegar(item.rota) },
                icon     = { Icon(item.icone, contentDescription = item.label) },
                label    = { Text(item.label, fontSize = 11.sp) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Azul,
                    selectedTextColor   = Azul,
                    unselectedIconColor = Color(0xFF9CA3AF),
                    unselectedTextColor = Color(0xFF9CA3AF),
                    indicatorColor      = Color(0xFFE8F0FE)
                )
            )
        }
    }
}
