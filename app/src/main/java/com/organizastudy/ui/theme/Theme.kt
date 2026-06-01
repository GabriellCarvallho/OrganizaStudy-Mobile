package com.organizastudy.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Azul            = Color(0xFF1A73E8)
val AzulEscuro      = Color(0xFF1557B0)
val FundoTela       = Color(0xFFF5F7FA)
val TextoPrimario   = Color(0xFF1A1A2E)
val TextoSecundario = Color(0xFF6B7280)
val BordaCard       = Color(0xFFE5E7EB)
val CorAlta         = Color(0xFFEF4444)
val CorMedia        = Color(0xFFF59E0B)
val CorBaixa        = Color(0xFF10B981)

val CoresDisciplinaHex = listOf(
    "#1A73E8","#10B981","#F59E0B","#EF4444",
    "#8B5CF6","#EC4899","#06B6D4","#FF6B35",
    "#14B8A6","#6366F1"
)

private val Cores = lightColorScheme(
    primary          = Azul,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFE8F0FE),
    background       = FundoTela,
    surface          = Color.White,
    onSurface        = TextoPrimario,
    onSurfaceVariant = TextoSecundario,
    outline          = BordaCard,
    error            = CorAlta
)

@Composable
fun OrganizaStudyTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Cores, content = content)
}
