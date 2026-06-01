package com.organizastudy.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.organizastudy.model.Subject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SubjectRepository {
    private val db = FirebaseFirestore.getInstance()
    private fun col(uid: String) = db.collection("users").document(uid).collection("subjects")

    fun getFlow(uid: String): Flow<List<Subject>> = callbackFlow {
        val l = col(uid).orderBy("nome").addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(snap?.documents?.mapNotNull { it.toObject<Subject>()?.copy(id = it.id) } ?: emptyList())
        }
        awaitClose { l.remove() }
    }

    suspend fun adicionar(uid: String, s: Subject): Result<Unit> = try {
        col(uid).add(mapOf("userId" to uid, "nome" to s.nome, "cor" to s.cor, "minutosTotais" to 0L)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun atualizar(uid: String, s: Subject): Result<Unit> = try {
        col(uid).document(s.id).update(mapOf("nome" to s.nome, "cor" to s.cor)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deletar(uid: String, id: String): Result<Unit> = try {
        col(uid).document(id).delete().await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun somarMinutos(uid: String, subjectId: String, min: Long): Result<Unit> = try {
        col(uid).document(subjectId).update("minutosTotais", FieldValue.increment(min)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
