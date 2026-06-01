package com.organizastudy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun iniciais(nome: String): String {
    val p = nome.trim().split(" ").filter { it.isNotBlank() }
    return when {
        p.isEmpty() -> "?"
        p.size == 1 -> p[0].take(2).uppercase()
        else        -> "${p[0].first()}${p[1].first()}".uppercase()
    }
}

@Composable
fun AvatarIniciais(nome: String, cor: Color, tamanho: Dp = 44.dp, fontSize: Int = 14) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(tamanho).clip(CircleShape).background(cor)
    ) {
        Text(iniciais(nome), color = Color.White, fontSize = fontSize.sp, fontWeight = FontWeight.Bold)
    }
}
