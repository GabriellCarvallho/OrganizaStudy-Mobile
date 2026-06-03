package com.organizastudy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organizastudy.ui.components.BottomNavBar
import com.organizastudy.ui.theme.*
import com.organizastudy.viewmodel.LivroState
import com.organizastudy.viewmodel.LivroViewModel

@Composable
fun MateriaisScreen(
    vm: LivroViewModel,
    rotaAtual: String,
    onNavegar: (String) -> Unit
) {
    val state by vm.state.collectAsState()
    val focus = LocalFocusManager.current
    var query by remember { mutableStateOf("") }

    Scaffold(
        bottomBar      = { BottomNavBar(rotaAtual, onNavegar) },
        containerColor = FundoTela
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                "Materiais de Estudo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrimario
            )
            Text(
                "Busque livros acadêmicos por disciplina",
                fontSize = 13.sp,
                color = TextoSecundario,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // ── Campo de busca ────────────────────────────────────────────
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Ex: Cálculo, Física, POO...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = Color(0xFF9CA3AF))
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = ""; vm.resetar() }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = Azul,
                    unfocusedBorderColor    = Color(0xFFE5E7EB),
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focus.clearFocus()
                    vm.buscar(query)
                })
            )

            Spacer(Modifier.height(8.dp))

            // ── Botão buscar ──────────────────────────────────────────────
            Button(
                onClick = { focus.clearFocus(); vm.buscar(query) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Azul),
                enabled = query.isNotBlank() && state !is LivroState.Carregando
            ) {
                Text("Buscar livros", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))

            // ── Estados da tela ───────────────────────────────────────────
            when (val s = state) {

                is LivroState.Idle -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.MenuBook, null,
                                Modifier.size(56.dp),
                                tint = Color(0xFFD1D5DB)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Digite uma disciplina e\nbusque materiais de estudo",
                                color = TextoSecundario,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                is LivroState.Carregando -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Azul)
                            Spacer(Modifier.height(12.dp))
                            Text("Buscando livros...", color = TextoSecundario)
                        }
                    }
                }

                is LivroState.Erro -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.WifiOff, null,
                                Modifier.size(48.dp),
                                tint = CorAlta
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                s.msg,
                                color = CorAlta,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                is LivroState.Sucesso -> {
                    Text(
                        "${s.livros.size} livro(s) encontrado(s)",
                        fontSize = 12.sp,
                        color = TextoSecundario,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(s.livros) { livro ->
                            Card(
                                Modifier.fillMaxWidth(),
                                shape  = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(0.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BordaCard)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth().padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Ícone do livro
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .padding(end = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.MenuBook,
                                            null,
                                            tint = Azul,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            livro.titulo,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextoPrimario,
                                            maxLines = 2
                                        )
                                        livro.autores?.firstOrNull()?.let { autor ->
                                            Text(
                                                autor,
                                                fontSize = 12.sp,
                                                color = TextoSecundario
                                            )
                                        }
                                        livro.ano?.let { ano ->
                                            Text(
                                                "Publicado em $ano",
                                                fontSize = 11.sp,
                                                color = Color(0xFF9CA3AF)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}