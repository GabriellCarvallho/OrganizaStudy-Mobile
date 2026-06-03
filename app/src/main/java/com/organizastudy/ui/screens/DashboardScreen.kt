package com.organizastudy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organizastudy.model.Prioridade
import com.organizastudy.model.StatusTarefa
import com.organizastudy.model.Task
import com.organizastudy.navigation.Rotas
import com.organizastudy.ui.components.AvatarIniciais
import com.organizastudy.ui.components.BottomNavBar
import com.organizastudy.ui.theme.*
import com.organizastudy.viewmodel.SubjectViewModel
import com.organizastudy.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    uid: String,
    email: String,
    subjectVm: SubjectViewModel,
    taskVm: TaskViewModel,
    rotaAtual: String,
    onNavegar: (String) -> Unit,
    onLogout: () -> Unit
) {
    val disciplinas  by subjectVm.todasSemFiltro.collectAsState()
    val todasTarefas by taskVm.todas.collectAsState()

    val hoje  = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }
    val nome  = email.substringBefore("@").replaceFirstChar { it.uppercase() }

    val tarefasHoje    = todasTarefas.filter { it.prazo == hoje }
    val pendentesTotal = todasTarefas.count { it.status == StatusTarefa.PENDENTE.name }
    val totalHoras     = disciplinas.sumOf { it.minutosTotais } / 3600

    // Streak = número de disciplinas com pelo menos 1 segundo estudado
    val streak = disciplinas.count { it.minutosTotais > 0 }

    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            subjectVm.carregar(uid)
            taskVm.carregar(uid)
        }
    }

    Scaffold(
        bottomBar      = { BottomNavBar(rotaAtual, onNavegar) },
        containerColor = FundoTela
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad),           // ← padding do Scaffold resolve sobreposição
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            // ── Header azul ───────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(AzulEscuro, Azul)))
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 24.dp)
                ) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Olá,", fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
                                Text(nome, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            // Botão sair — sem emoji, ícone limpo
                            IconButton(onClick = onLogout) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                ) {
                                    Icon(Icons.Default.Logout, "Sair", tint = Color.White)
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard(Modifier.weight(1f), Icons.Outlined.AccessTime,  "esta semana",  "${totalHoras}h")
                            StatCard(Modifier.weight(1f), Icons.Outlined.Assignment,  "pendentes",    "$pendentesTotal")
                            StatCard(Modifier.weight(1f), Icons.Outlined.LocalLibrary,"streak",       "${streak}d")
                        }
                    }
                }
            }

            // ── Tarefas de hoje ───────────────────────────────────────────
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tarefas de hoje", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextoPrimario)
                    TextButton(onClick = { onNavegar(Rotas.TAREFAS) }) {
                        Text("Ver todas >", fontSize = 13.sp, color = Azul)
                    }
                }
            }

            val tarefasMostrar = if (tarefasHoje.isNotEmpty()) tarefasHoje
            else todasTarefas.filter { it.status == StatusTarefa.PENDENTE.name }

            if (tarefasMostrar.isEmpty()) {
                item {
                    Card(
                        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BordaCard)
                    ) {
                        Row(
                            Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = CorBaixa, modifier = Modifier.size(22.dp))
                            Text("Nenhuma tarefa pendente!", color = TextoSecundario, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                items(tarefasMostrar.take(3)) { t ->
                    TarefaHojeCard(
                        t        = t,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }

            // ── Continue estudando ────────────────────────────────────────
            if (disciplinas.isNotEmpty()) {
                item {
                    Text(
                        "Continue estudando",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextoPrimario,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                }
                items(disciplinas.sortedByDescending { it.minutosTotais }.take(3)) { s ->
                    val cor = runCatching {
                        Color(android.graphics.Color.parseColor(s.cor))
                    }.getOrDefault(Azul)
                    val h = s.minutosTotais / 60
                    val m = s.minutosTotais % 60

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BordaCard)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarIniciais(nome = s.nome, cor = cor, tamanho = 42.dp, fontSize = 13)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(s.nome, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextoPrimario)
                                Text("${h}h ${m}min acumulados", fontSize = 12.sp, color = TextoSecundario)
                            }
                            OutlinedButton(
                                onClick = { onNavegar(Rotas.DISCIPLINAS) },
                                shape  = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Azul),
                                border = BorderStroke(1.dp, Azul),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Iniciar", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ── StatCard ──────────────────────────────────────────────────────────────────
@Composable
fun StatCard(modifier: Modifier, icone: ImageVector, label: String, valor: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icone, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
            }
            Text(valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// ── TarefaHojeCard ────────────────────────────────────────────────────────────
@Composable
fun TarefaHojeCard(t: Task, modifier: Modifier = Modifier) {
    val corPrio = when (t.prioridade) {
        Prioridade.ALTA.name  -> CorAlta
        Prioridade.MEDIA.name -> CorMedia
        else                   -> CorBaixa
    }
    val corBarra = runCatching {
        Color(android.graphics.Color.parseColor(t.subjectCor))
    }.getOrDefault(Azul)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        border   = BorderStroke(1.dp, BordaCard)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(corBarra)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(t.titulo, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextoPrimario)
                Text(
                    if (t.prazo.isNotBlank()) t.prazo else "Sem prazo",
                    fontSize = 12.sp, color = TextoSecundario
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(corPrio)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(t.prioridade, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}