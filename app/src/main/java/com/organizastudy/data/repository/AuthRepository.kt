package com.organizastudy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun login(email: String, senha: String): Result<FirebaseUser> = try {
        Result.success(auth.signInWithEmailAndPassword(email, senha).await().user!!)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun cadastrar(email: String, senha: String): Result<FirebaseUser> = try {
        Result.success(auth.createUserWithEmailAndPassword(email, senha).await().user!!)
    } catch (e: Exception) { Result.failure(e) }

    fun logout() = auth.signOut()
}
