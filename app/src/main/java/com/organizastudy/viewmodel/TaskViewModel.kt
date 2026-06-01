package com.organizastudy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizastudy.data.repository.TaskRepository
import com.organizastudy.model.StatusTarefa
import com.organizastudy.model.Task
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repo = TaskRepository()
    private val _todas = MutableStateFlow<List<Task>>(emptyList())
    private val _query = MutableStateFlow("")
    private val _filtroStatus = MutableStateFlow<String?>(null)
    private val _filtroPrio   = MutableStateFlow<String?>(null)

    val query: StateFlow<String> = _query
    val filtroStatus: StateFlow<String?> = _filtroStatus
    val filtroPrio:   StateFlow<String?> = _filtroPrio

    val tarefas: StateFlow<List<Task>> = combine(_todas, _query, _filtroStatus, _filtroPrio) { lista, q, st, pr ->
        lista
            .filter { if (q.isBlank()) true else it.titulo.contains(q, ignoreCase = true) }
            .filter { if (st == null) true else it.status == st }
            .filter { if (pr == null) true else it.prioridade == pr }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todas: StateFlow<List<Task>> = _todas

    private val _msg = MutableStateFlow<String?>(null)
    val msg: StateFlow<String?> = _msg

    fun carregar(uid: String) {
        viewModelScope.launch { repo.getFlow(uid).collect { _todas.value = it } }
    }

    fun onQuery(q: String) { _query.value = q }
    fun setFiltroStatus(s: String?) { _filtroStatus.value = s }
    fun setFiltroPrio(p: String?)   { _filtroPrio.value = p }

    fun adicionar(uid: String, t: Task) {
        if (t.titulo.isBlank()) { _msg.value = "Título não pode estar vazio."; return }
        viewModelScope.launch {
            repo.adicionar(uid, t).fold(
                onSuccess = { _msg.value = "Tarefa adicionada!" },
                onFailure = { _msg.value = "Erro: ${it.localizedMessage}" }
            )
        }
    }

    fun atualizar(uid: String, t: Task) {
        viewModelScope.launch {
            repo.atualizar(uid, t).fold(
                onSuccess = { _msg.value = "Tarefa atualizada!" },
                onFailure = { _msg.value = "Erro: ${it.localizedMessage}" }
            )
        }
    }

    fun alternarStatus(uid: String, t: Task) {
        val novo = if (t.status == StatusTarefa.PENDENTE.name) StatusTarefa.CONCLUIDA.name else StatusTarefa.PENDENTE.name
        viewModelScope.launch { repo.atualizarStatus(uid, t.id, novo) }
    }

    fun deletar(uid: String, id: String) {
        viewModelScope.launch {
            repo.deletar(uid, id).fold(
                onSuccess = { _msg.value = "Tarefa removida." },
                onFailure = { _msg.value = "Erro: ${it.localizedMessage}" }
            )
        }
    }

    fun limparMsg() { _msg.value = null }
}
