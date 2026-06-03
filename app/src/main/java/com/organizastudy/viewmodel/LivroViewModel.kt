package com.organizastudy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizastudy.data.repository.LivroRepository
import com.organizastudy.model.Livro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estados possíveis da tela de Materiais
sealed class LivroState {
    object Idle                                : LivroState()
    object Carregando                          : LivroState()
    data class Sucesso(val livros: List<Livro>) : LivroState()
    data class Erro(val msg: String)            : LivroState()
}

class LivroViewModel : ViewModel() {

    private val repo = LivroRepository()

    private val _state = MutableStateFlow<LivroState>(LivroState.Idle)
    val state: StateFlow<LivroState> = _state

    fun buscar(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _state.value = LivroState.Carregando
            _state.value = repo.buscarLivros(query).fold(
                onSuccess = { LivroState.Sucesso(it.livros) },
                onFailure = { LivroState.Erro("Erro ao buscar: ${it.message}") }
            )
        }
    }

    fun resetar() {
        _state.value = LivroState.Idle
    }
}