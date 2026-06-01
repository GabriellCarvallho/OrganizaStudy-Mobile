package com.organizastudy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizastudy.data.repository.SubjectRepository
import com.organizastudy.model.Subject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SubjectViewModel : ViewModel() {
    private val repo = SubjectRepository()
    private val _todas = MutableStateFlow<List<Subject>>(emptyList())
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _sessaoAtiva = MutableStateFlow<Subject?>(null)
    val sessaoAtiva: StateFlow<Subject?> = _sessaoAtiva

    val disciplinas: StateFlow<List<Subject>> = combine(_todas, _query) { lista, q ->
        if (q.isBlank()) lista else lista.filter { it.nome.contains(q, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todasSemFiltro: StateFlow<List<Subject>> = _todas

    private val _msg = MutableStateFlow<String?>(null)
    val msg: StateFlow<String?> = _msg

    fun carregar(uid: String) {
        viewModelScope.launch { repo.getFlow(uid).collect { _todas.value = it } }
    }

    fun onQuery(q: String) { _query.value = q }
    fun iniciarSessao(s: Subject) { _sessaoAtiva.value = s }
    fun encerrarSessao() { _sessaoAtiva.value = null }

    fun adicionar(uid: String, nome: String, cor: String) {
        if (nome.isBlank()) { _msg.value = "Nome não pode estar vazio."; return }
        viewModelScope.launch {
            repo.adicionar(uid, Subject(nome = nome, cor = cor)).fold(
                onSuccess = { _msg.value = "\"$nome\" adicionada!" },
                onFailure = { _msg.value = "Erro: ${it.localizedMessage}" }
            )
        }
    }

    fun atualizar(uid: String, s: Subject) {
        viewModelScope.launch {
            repo.atualizar(uid, s).fold(
                onSuccess = { _msg.value = "Disciplina atualizada!" },
                onFailure = { _msg.value = "Erro: ${it.localizedMessage}" }
            )
        }
    }

    fun deletar(uid: String, id: String) {
        viewModelScope.launch {
            repo.deletar(uid, id).fold(
                onSuccess = { _msg.value = "Disciplina removida." },
                onFailure = { _msg.value = "Erro: ${it.localizedMessage}" }
            )
        }
    }

    fun somarMinutos(uid: String, subjectId: String, min: Long) {
        viewModelScope.launch { repo.somarMinutos(uid, subjectId, min) }
    }

    fun limparMsg() { _msg.value = null }
}
