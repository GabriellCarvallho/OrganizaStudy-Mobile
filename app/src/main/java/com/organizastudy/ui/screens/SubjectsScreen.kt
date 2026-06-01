package com.organizastudy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organizastudy.model.Subject
import com.organizastudy.ui.components.AvatarIniciais
import com.organizastudy.ui.components.BottomNavBar
import com.organizastudy.ui.components.SubjectFormDialog
import com.organizastudy.ui.theme.*
import com.organizastudy.viewmodel.SubjectViewModel
import com.organizastudy.viewmodel.TimerEstado
import com.organizastudy.viewmodel.TimerViewModel
import kotlinx.coroutines.launch

@Composable
fun SubjectsScreen(
    uid: String,
    subjectVm: SubjectViewModel,
    timerVm: TimerViewModel,
    rotaAtual: String,
    onNavegar: (String) -> Unit
) {
    val disciplinas by subjectVm.disciplinas.collectAsState()
    val query       by subjectVm.query.collectAsState()
    val msg         by subjectVm.msg.collectAsState()
    val sessao      by subjectVm.sessaoAtiva.collectAsState()
    val segundos    by timerVm.segundos.collectAsState()
    val timerEstado by timerVm.estado.collectAsState()

    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var mostrarAdd   by remember { mutableStateOf(false) }
    var editando     by remember { mutableStateOf<Subject?>(null) }
    var deletando    by remember { mutableStateOf<Subject?>(null) }
    var confirmarEnc by remember { mutableStateOf(false) }

    LaunchedEffect(uid) { subjectVm.carregar(uid) }
    LaunchedEffect(msg) {
        msg?.let { scope.launch { snack.showSnackbar(it) }; subjectVm.limparMsg() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snack) },
        bottomBar    = { BottomNavBar(rotaAtual, onNavegar) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarAdd = true }, containerColor = Azul, shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        },
        containerColor = FundoTela
    ) { pad ->
        LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(bottom = 80.dp)) {

            // Cabeçalho + Busca
            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 24.dp, bottom = 12.dp)) {
                    Text("Disciplinas", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextoPrimario)
                    Text("${disciplinas.size} disciplina(s) cadastrada(s)", fontSize = 13.sp, color = TextoSecundario)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = query, onValueChange = { subjectVm.onQuery(it) },
                        placeholder = { Text("Buscar disciplina...", color = Color(0xFF9CA3AF)) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF9CA3AF)) },
                        trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { subjectVm.onQuery("") }) { Icon(Icons.Default.Close, null) } },
                        singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Azul, unfocusedBorderColor = Color(0xFFE5E7EB),
                            unfocusedContainerColor = Color.White, focusedContainerColor = Color.White
                        )
                    )
                }
            }

            // Sessão em andamento (RF03)
            if (sessao != null && timerEstado != TimerEstado.PARADO) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp)).background(Azul).padding(16.dp)
                    ) {
                        Column {
                            Text("SESSÃO EM ANDAMENTO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f), letterSpacing = 1.sp)
                            Text(sessao!!.nome, fontSize = 14.sp, color = Color.White, modifier = Modifier.padding(top = 2.dp))
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(timerVm.formatar(segundos), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = { if (timerEstado == TimerEstado.RODANDO) timerVm.pausar() else timerVm.iniciar() },
                                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))
                                    ) { Icon(if (timerEstado == TimerEstado.RODANDO) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.White) }
                                    IconButton(
                                        onClick = { confirmarEnc = true },
                                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))
                                    ) { Icon(Icons.Default.Stop, null, tint = Color.White) }
                                }
                            }
                        }
                    }
                }
            }

            // Lista vazia
            if (disciplinas.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MenuBook, null, Modifier.size(56.dp), tint = Color(0xFFD1D5DB))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (query.isBlank()) "Nenhuma disciplina.\nToque em + para adicionar."
                            else "Nenhum resultado para \"$query\".",
                            textAlign = TextAlign.Center, color = TextoSecundario, fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(disciplinas, key = { it.id }) { s ->
                    DisciplinaCard(
                        s = s,
                        emSessao = sessao?.id == s.id && timerEstado != TimerEstado.PARADO,
                        onIniciar = { subjectVm.iniciarSessao(s); timerVm.iniciar() },
                        onEditar  = { editando = s },
                        onDeletar = { deletando = s },
                        modifier  = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    if (mostrarAdd) SubjectFormDialog(
        onConfirmar = { nome, cor -> subjectVm.adicionar(uid, nome, cor); mostrarAdd = false },
        onDismiss = { mostrarAdd = false }
    )

    editando?.let { s ->
        SubjectFormDialog(
            editando = s,
            onConfirmar = { nome, cor -> subjectVm.atualizar(uid, s.copy(nome = nome, cor = cor)); editando = null },
            onDismiss = { editando = null }
        )
    }

    deletando?.let { s ->
        AlertDialog(
            onDismissRequest = { deletando = null },
            title = { Text("Remover \"${s.nome}\"?") },
            text  = { Text("Esta ação não pode ser desfeita.") },
            confirmButton = { TextButton(onClick = { subjectVm.deletar(uid, s.id); deletando = null }) { Text("Remover", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { deletando = null }) { Text("Cancelar") } }
        )
    }

    if (confirmarEnc) {
        AlertDialog(
            onDismissRequest = { confirmarEnc = false },
            title = { Text("Encerrar sessão?") },
            text  = { Text("Salvar ${segundos / 60} minuto(s) para \"${sessao?.nome}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    val min = timerVm.encerrar()
                    sessao?.let { subjectVm.somarMinutos(uid, it.id, min) }
                    subjectVm.encerrarSessao()
                    confirmarEnc = false
                }) { Text("Salvar") }
            },
            dismissButton = { TextButton(onClick = { confirmarEnc = false }) { Text("Continuar") } }
        )
    }
}

@Composable
fun DisciplinaCard(s: Subject, emSessao: Boolean, onIniciar: () -> Unit, onEditar: () -> Unit, onDeletar: () -> Unit, modifier: Modifier = Modifier) {
    val cor = runCatching { Color(android.graphics.Color.parseColor(s.cor)) }.getOrDefault(Azul)
    val h = s.minutosTotais / 60; val m = s.minutosTotais % 60

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BordaCard)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciais(nome = s.nome, cor = cor, tamanho = 46.dp, fontSize = 14)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(s.nome, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextoPrimario)
                Text("${h}h ${m}min estudados", fontSize = 12.sp, color = TextoSecundario)
            }
            IconButton(onClick = onEditar) { Icon(Icons.Default.Edit, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(18.dp)) }
            IconButton(onClick = onDeletar) { Icon(Icons.Default.Delete, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(18.dp)) }
            if (emSessao) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(CorMedia.copy(alpha = 0.15f))) {
                    Icon(Icons.Default.Pause, null, tint = CorMedia, modifier = Modifier.size(20.dp))
                }
            } else {
                IconButton(onClick = onIniciar, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Azul.copy(alpha = 0.1f))) {
                    Icon(Icons.Default.PlayArrow, null, tint = Azul, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
