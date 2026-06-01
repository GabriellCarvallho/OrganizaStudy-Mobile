package com.organizastudy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organizastudy.model.Prioridade
import com.organizastudy.model.StatusTarefa
import com.organizastudy.model.Subject
import com.organizastudy.model.Task
import com.organizastudy.ui.components.BottomNavBar
import com.organizastudy.ui.theme.*
import com.organizastudy.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(uid: String, disciplinas: List<Subject>, vm: TaskViewModel, rotaAtual: String, onNavegar: (String) -> Unit) {
    val tarefas      by vm.tarefas.collectAsState()
    val todas        by vm.todas.collectAsState()
    val filtroStatus by vm.filtroStatus.collectAsState()
    val filtroPrio   by vm.filtroPrio.collectAsState()
    val msg          by vm.msg.collectAsState()
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var mostrarAdd by remember { mutableStateOf(false) }
    var editando   by remember { mutableStateOf<Task?>(null) }
    var deletando  by remember { mutableStateOf<Task?>(null) }

    val pendentes  = todas.count { it.status == StatusTarefa.PENDENTE.name }
    val concluidas = todas.count { it.status == StatusTarefa.CONCLUIDA.name }

    LaunchedEffect(uid) { vm.carregar(uid) }
    LaunchedEffect(msg) { msg?.let { scope.launch { snack.showSnackbar(it) }; vm.limparMsg() } }

    data class Chip(val label: String, val status: String?, val prio: String?)
    val chips = listOf(
        Chip("Todas", null, null), Chip("Pendentes", StatusTarefa.PENDENTE.name, null),
        Chip("Concluídas", StatusTarefa.CONCLUIDA.name, null),
        Chip("ALTA", null, Prioridade.ALTA.name), Chip("MÉDIA", null, Prioridade.MEDIA.name), Chip("BAIXA", null, Prioridade.BAIXA.name)
    )

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
            item {
                Column(Modifier.padding(horizontal = 20.dp).padding(top = 24.dp, bottom = 8.dp)) {
                    Text("Tarefas", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextoPrimario)
                    Text("$pendentes pendente(s) · $concluidas concluída(s)", fontSize = 13.sp, color = TextoSecundario)
                }
            }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(chips) { chip ->
                        val sel = filtroStatus == chip.status && filtroPrio == chip.prio
                        FilterChip(
                            selected = sel,
                            onClick  = { vm.setFiltroStatus(if (sel) null else chip.status); vm.setFiltroPrio(if (sel) null else chip.prio) },
                            label    = { Text(chip.label, fontSize = 13.sp) },
                            colors   = FilterChipDefaults.filterChipColors(selectedContainerColor = Azul, selectedLabelColor = Color.White, containerColor = Color.White),
                            border   = FilterChipDefaults.filterChipBorder(enabled = true, selected = sel, selectedBorderColor = Azul, borderColor = BordaCard)
                        )
                    }
                }
            }
            if (tarefas.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null, Modifier.size(56.dp), tint = Color(0xFFD1D5DB))
                        Spacer(Modifier.height(12.dp))
                        Text("Nenhuma tarefa encontrada.", textAlign = TextAlign.Center, color = TextoSecundario)
                    }
                }
            } else {
                items(tarefas, key = { it.id }) { t ->
                    TarefaCard(t = t, onToggle = { vm.alternarStatus(uid, t) }, onEditar = { editando = t }, onDeletar = { deletando = t }, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
                }
            }
        }
    }

    if (mostrarAdd) TaskFormDialog(disciplinas = disciplinas, onConfirmar = { vm.adicionar(uid, it.copy(userId = uid)); mostrarAdd = false }, onDismiss = { mostrarAdd = false })
    editando?.let { t -> TaskFormDialog(editando = t, disciplinas = disciplinas, onConfirmar = { vm.atualizar(uid, it); editando = null }, onDismiss = { editando = null }) }
    deletando?.let { t ->
        AlertDialog(
            onDismissRequest = { deletando = null }, title = { Text("Remover tarefa") }, text = { Text("Deseja remover \"${t.titulo}\"?") },
            confirmButton = { TextButton(onClick = { vm.deletar(uid, t.id); deletando = null }) { Text("Remover", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { deletando = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun TarefaCard(t: Task, onToggle: () -> Unit, onEditar: () -> Unit, onDeletar: () -> Unit, modifier: Modifier = Modifier) {
    val concluida = t.status == StatusTarefa.CONCLUIDA.name
    val corPrio = when (t.prioridade) { Prioridade.ALTA.name -> CorAlta; Prioridade.MEDIA.name -> CorMedia; else -> CorBaixa }
    val corSub  = runCatching { Color(android.graphics.Color.parseColor(t.subjectCor)) }.getOrDefault(Azul)

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor =  if(concluida) Color (0xFFF9FA) else Color.White), elevation = CardDefaults.cardElevation(0.dp), border = BorderStroke(1.dp, if(concluida) Color(0xFF5E7EB) else BordaCard)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = concluida, onCheckedChange = { onToggle() }, colors = CheckboxDefaults.colors(checkedColor = Azul, uncheckedColor = Color(0xFFD1D5DB)))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(t.titulo, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = if (concluida) TextoSecundario else TextoPrimario,
                    textDecoration = if (concluida) TextDecoration.LineThrough else TextDecoration.None)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 3.dp)) {
                    if (t.subjectNome.isNotBlank()) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(corSub))
                        Text(t.subjectNome, fontSize = 11.sp, color = TextoSecundario)
                        if (t.prazo.isNotBlank()) Text("·", fontSize = 11.sp, color = TextoSecundario)
                    }
                    if (t.prazo.isNotBlank()) Text(t.prazo, fontSize = 11.sp, color = TextoSecundario)
                }
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(corPrio).padding(horizontal = 8.dp, vertical = 3.dp)) {
                Text(t.prioridade, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            IconButton(onClick = onEditar) { Icon(Icons.Default.Edit, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(16.dp)) }
            IconButton(onClick = onDeletar) { Icon(Icons.Default.Delete, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(16.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormDialog(editando: Task? = null, disciplinas: List<Subject>, onConfirmar: (Task) -> Unit, onDismiss: () -> Unit) {
    var titulo      by remember { mutableStateOf(editando?.titulo ?: "") }
    var prazo       by remember { mutableStateOf(editando?.prazo ?: "") }
    var prioridade  by remember { mutableStateOf(editando?.prioridade ?: Prioridade.MEDIA.name) }
    var subjectId   by remember { mutableStateOf(editando?.subjectId ?: "") }
    var subjectNome by remember { mutableStateOf(editando?.subjectNome ?: "") }
    var subjectCor  by remember { mutableStateOf(editando?.subjectCor ?: "#1A73E8") }
    var expandPrio  by remember { mutableStateOf(false) }
    var expandDisc  by remember { mutableStateOf(false) }

    AlertDialog(onDismissRequest = onDismiss, shape = RoundedCornerShape(20.dp),
        title = { Text(if (editando == null) "Nova tarefa" else "Editar tarefa", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título *") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul))
                OutlinedTextField(value = prazo, onValueChange = { prazo = it }, label = { Text("Prazo (dd/MM/yyyy)") }, singleLine = true, leadingIcon = { Icon(Icons.Default.Event, null) }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul))
                ExposedDropdownMenuBox(expanded = expandPrio, onExpandedChange = { expandPrio = it }) {
                    OutlinedTextField(value = prioridade, onValueChange = {}, readOnly = true, label = { Text("Prioridade") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandPrio) }, shape = RoundedCornerShape(12.dp), modifier = Modifier.menuAnchor().fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul))
                    ExposedDropdownMenu(expanded = expandPrio, onDismissRequest = { expandPrio = false }) {
                        Prioridade.entries.forEach { p -> DropdownMenuItem(text = { Text(p.name) }, onClick = { prioridade = p.name; expandPrio = false }) }
                    }
                }
                if (disciplinas.isNotEmpty()) {
                    ExposedDropdownMenuBox(expanded = expandDisc, onExpandedChange = { expandDisc = it }) {
                        OutlinedTextField(value = if (subjectNome.isBlank()) "Sem disciplina" else subjectNome, onValueChange = {}, readOnly = true, label = { Text("Disciplina") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandDisc) }, shape = RoundedCornerShape(12.dp), modifier = Modifier.menuAnchor().fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul))
                        ExposedDropdownMenu(expanded = expandDisc, onDismissRequest = { expandDisc = false }) {
                            DropdownMenuItem(text = { Text("Sem disciplina") }, onClick = { subjectId = ""; subjectNome = ""; subjectCor = "#1A73E8"; expandDisc = false })
                            disciplinas.forEach { s -> DropdownMenuItem(text = { Text(s.nome) }, onClick = { subjectId = s.id; subjectNome = s.nome; subjectCor = s.cor; expandDisc = false }) }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (titulo.isNotBlank()) onConfirmar(Task(id = editando?.id ?: "", userId = editando?.userId ?: "", subjectId = subjectId, subjectNome = subjectNome, subjectCor = subjectCor, titulo = titulo.trim(), prazo = prazo.trim(), prioridade = prioridade, status = editando?.status ?: StatusTarefa.PENDENTE.name))
            }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Azul)) { Text(if (editando == null) "Adicionar" else "Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color(0xFF6B7280)) } }
    )
}
