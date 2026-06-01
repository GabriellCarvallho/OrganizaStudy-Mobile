package com.organizastudy.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.organizastudy.model.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private fun col(uid: String) = db.collection("users").document(uid).collection("tasks")

    fun getFlow(uid: String): Flow<List<Task>> = callbackFlow {
        val l = col(uid).addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(snap?.documents?.mapNotNull { it.toObject<Task>()?.copy(id = it.id) } ?: emptyList())
        }
        awaitClose { l.remove() }
    }

    suspend fun adicionar(uid: String, t: Task): Result<Unit> = try {
        col(uid).add(mapOf(
            "userId" to uid, "subjectId" to t.subjectId, "subjectNome" to t.subjectNome,
            "subjectCor" to t.subjectCor, "titulo" to t.titulo, "descricao" to t.descricao,
            "prazo" to t.prazo, "prioridade" to t.prioridade, "status" to t.status
        )).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun atualizar(uid: String, t: Task): Result<Unit> = try {
        col(uid).document(t.id).update(mapOf(
            "titulo" to t.titulo, "descricao" to t.descricao, "prazo" to t.prazo,
            "prioridade" to t.prioridade, "status" to t.status,
            "subjectId" to t.subjectId, "subjectNome" to t.subjectNome, "subjectCor" to t.subjectCor
        )).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun atualizarStatus(uid: String, id: String, status: String): Result<Unit> = try {
        col(uid).document(id).update("status", status).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deletar(uid: String, id: String): Result<Unit> = try {
        col(uid).document(id).delete().await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
