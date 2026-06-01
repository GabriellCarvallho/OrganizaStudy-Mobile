package com.organizastudy.model

enum class Prioridade { ALTA, MEDIA, BAIXA }
enum class StatusTarefa { PENDENTE, CONCLUIDA }

data class Task(
    val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val subjectNome: String = "",
    val subjectCor: String = "#1A73E8",
    val titulo: String = "",
    val descricao: String = "",
    val prazo: String = "",
    val prioridade: String = Prioridade.MEDIA.name,
    val status: String = StatusTarefa.PENDENTE.name
)
