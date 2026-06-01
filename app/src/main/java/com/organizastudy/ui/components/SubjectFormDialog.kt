package com.organizastudy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organizastudy.model.Subject
import com.organizastudy.ui.theme.Azul
import com.organizastudy.ui.theme.CoresDisciplinaHex

@Composable
fun SubjectFormDialog(
    editando: Subject? = null,
    onConfirmar: (nome: String, cor: String) -> Unit,
    onDismiss: () -> Unit
) {
    var nome by remember { mutableStateOf(editando?.nome ?: "") }
    var cor  by remember { mutableStateOf(editando?.cor  ?: CoresDisciplinaHex.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = { Text(if (editando == null) "Nova disciplina" else "Editar disciplina", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = nome, onValueChange = { nome = it },
                    label = { Text("Nome da disciplina") }, singleLine = true,
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul)
                )
                Text("Cor:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.fillMaxWidth().height(88.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(CoresDisciplinaHex) { hex ->
                        val c = runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Azul)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(c)
                                .then(if (hex == cor) Modifier.border(3.dp, Color.DarkGray, CircleShape) else Modifier)
                                .clickable { cor = hex }
                        ) {
                            if (hex == cor) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (nome.isNotBlank()) onConfirmar(nome.trim(), cor) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Azul)
            ) { Text(if (editando == null) "Adicionar" else "Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color(0xFF6B7280)) } }
    )
}
