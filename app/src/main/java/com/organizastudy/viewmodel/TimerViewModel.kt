package com.organizastudy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class TimerEstado { PARADO, RODANDO, PAUSADO }

class TimerViewModel : ViewModel() {
    private val _segundos = MutableStateFlow(0L)
    val segundos: StateFlow<Long> = _segundos
    private val _estado = MutableStateFlow(TimerEstado.PARADO)
    val estado: StateFlow<TimerEstado> = _estado
    private var job: Job? = null

    fun iniciar() {
        if (_estado.value == TimerEstado.RODANDO) return
        _estado.value = TimerEstado.RODANDO
        job = viewModelScope.launch { while (true) { delay(1000L); _segundos.value++ } }
    }

    fun pausar() { job?.cancel(); _estado.value = TimerEstado.PAUSADO }

    fun encerrar(): Long {
        job?.cancel()
        val min = _segundos.value / 60
        _segundos.value = 0L
        _estado.value = TimerEstado.PARADO
        return min
    }

    fun formatar(s: Long) = "%02d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)
}
