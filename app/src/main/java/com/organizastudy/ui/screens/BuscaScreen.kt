package com.organizastudy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organizastudy.model.Prioridade
import com.organizastudy.model.Subject
import com.organizastudy.model.Task
import com.organizastudy.ui.components.AvatarIniciais
import com.organizastudy.ui.components.BottomNavBar
import com.organizastudy.ui.theme.*
import com.organizastudy.viewmodel.SubjectViewModel
import com.organizastudy.viewmodel.TaskViewModel

@Composable
fun BuscaScreen(uid: String, subjectVm: SubjectViewModel, taskVm: TaskViewModel, rotaAtual: String, onNavegar: (String) -> Unit) {
    val disciplinas by subjectVm.todasSemFiltro.collectAsState()
    val tarefas     by taskVm.todas.collectAsState()

    var query by remember { mutableStateOf("") }
    val focusReq = remember { FocusRequester() }

    val discFilt  = if (query.isBlank()) emptyList() else disciplinas.filter { it.nome.contains(query, ignoreCase = true) }
    val tarefFilt = if (query.isBlank()) emptyList() else tarefas.filter { it.titulo.contains(query, ignoreCase = true) || it.subjectNome.contains(query, ignoreCase = true) }
    val total     = discFilt.size + tarefFilt.size

    LaunchedEffect(uid) { subjectVm.carregar(uid); taskVm.carregar(uid); focusReq.requestFocus() }

    Scaffold(bottomBar = { BottomNavBar(rotaAtual, onNavegar) }, containerColor = FundoTela) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            // Header
            Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text("Busca", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextoPrimario, modifier = Modifier.padding(bottom = 12.dp))
                OutlinedTextField(
                    value = query, onValueChange = { query = it },
                    placeholder = { Text("Buscar disciplinas, tarefas...", color = Color(0xFF9CA3AF)) },
                    leadingIcon  = { Icon(Icons.Default.Search, null, tint = Azul) },
                    trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Default.Close, null) } },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().focusRequester(focusReq),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul, unfocusedBorderColor = Color(0xFFE5E7EB))
                )
                if (query.isNotBlank()) Text("$total resultado(s) encontrado(s)", fontSize = 12.sp, color = TextoSecundario, modifier = Modifier.padding(top = 8.dp))
            }

            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                when {
                    query.isBlank() -> item {
                        Column(Modifier.fillParentMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.Search, null, Modifier.size(56.dp), tint = Color(0xFFD1D5DB))
                            Spacer(Modifier.height(12.dp))
                            Text("Digite para buscar", color = TextoSecundario, textAlign = TextAlign.Center)
                        }
                    }
                    total == 0 -> item {
                        Column(Modifier.fillParentMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text("Nenhum resultado para \"$query\"", color = TextoSecundario, textAlign = TextAlign.Center)
                        }
                    }
                    else -> {
                        if (discFilt.isNotEmpty()) {
                            item { Text("DISCIPLINAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoSecundario, letterSpacing = 1.sp, modifier = Modifier.padding(vertical = 10.dp)) }
                            items(discFilt) { s -> BuscaDiscCard(s, Modifier.padding(vertical = 4.dp)) }
                        }
                        if (tarefFilt.isNotEmpty()) {
                            item { Text("TAREFAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoSecundario, letterSpacing = 1.sp, modifier = Modifier.padding(top = 16.dp, bottom = 10.dp)) }
                            items(tarefFilt) { t -> BuscaTarefaCard(t, Modifier.padding(vertical = 4.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BuscaDiscCard(s: Subject, modifier: Modifier = Modifier) {
    val cor = runCatching { Color(android.graphics.Color.parseColor(s.cor)) }.getOrDefault(Azul)
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp), border = androidx.compose.foundation.BorderStroke(1.dp, BordaCard)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciais(s.nome, cor, 40.dp, 13)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(s.nome, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextoPrimario)
                Text("${s.minutosTotais / 60}h ${s.minutosTotais % 60}min estudados", fontSize = 12.sp, color = TextoSecundario)
            }
            Icon(Icons.Default.NorthEast, null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun BuscaTarefaCard(t: Task, modifier: Modifier = Modifier) {
    val corPrio = when (t.prioridade) { Prioridade.ALTA.name -> CorAlta; Prioridade.MEDIA.name -> CorMedia; else -> CorBaixa }
    val corSub  = runCatching { Color(android.graphics.Color.parseColor(t.subjectCor)) }.getOrDefault(Azul)
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp), border = androidx.compose.foundation.BorderStroke(1.dp, BordaCard)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp).clip(CircleShape).background(Azul)) {
                Icon(Icons.Default.Assignment, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(t.titulo, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextoPrimario)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (t.subjectNome.isNotBlank()) { Box(Modifier.size(8.dp).clip(CircleShape).background(corSub)); Text(t.subjectNome, fontSize = 11.sp, color = TextoSecundario) }
                    if (t.prazo.isNotBlank()) Text("· ${t.prazo}", fontSize = 11.sp, color = TextoSecundario)
                }
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(corPrio).padding(horizontal = 6.dp, vertical = 2.dp)) {
                Text(t.prioridade, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
