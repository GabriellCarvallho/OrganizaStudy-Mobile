package com.organizastudy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.organizastudy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle       : AuthState()
    object Carregando : AuthState()
    data class Sucesso(val user: FirebaseUser) : AuthState()
    data class Erro(val msg: String)           : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state
    val usuarioLogado get() = repo.currentUser

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) { _state.value = AuthState.Erro("Preencha todos os campos."); return }
        viewModelScope.launch {
            _state.value = AuthState.Carregando
            _state.value = repo.login(email.trim(), senha).fold(
                onSuccess = { AuthState.Sucesso(it) },
                onFailure = { AuthState.Erro(traduzir(it.message)) }
            )
        }
    }

    fun cadastrar(email: String, senha: String, confirmar: String) {
        if (email.isBlank() || senha.isBlank()) { _state.value = AuthState.Erro("Preencha todos os campos."); return }
        if (senha != confirmar) { _state.value = AuthState.Erro("As senhas não coincidem."); return }
        if (senha.length < 6)  { _state.value = AuthState.Erro("Senha deve ter no mínimo 6 caracteres."); return }
        viewModelScope.launch {
            _state.value = AuthState.Carregando
            _state.value = repo.cadastrar(email.trim(), senha).fold(
                onSuccess = { AuthState.Sucesso(it) },
                onFailure = { AuthState.Erro(traduzir(it.message)) }
            )
        }
    }

    fun logout() { repo.logout(); _state.value = AuthState.Idle }
    fun resetar() { _state.value = AuthState.Idle }

    private fun traduzir(msg: String?) = when {
        msg == null              -> "Erro desconhecido."
        "email" in msg           -> "E-mail inválido ou já cadastrado."
        "password" in msg        -> "Senha incorreta."
        "credential" in msg      -> "E-mail ou senha incorretos."
        "network" in msg         -> "Sem conexão com a internet."
        "CONFIGURATION" in msg   -> "Verifique o google-services.json."
        else                     -> msg
    }
}
