package com.organizastudy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organizastudy.ui.theme.Azul
import com.organizastudy.ui.theme.AzulEscuro
import com.organizastudy.viewmodel.AuthState
import com.organizastudy.viewmodel.AuthViewModel

@Composable
fun CadastroScreen(vm: AuthViewModel, onSucesso: () -> Unit, onVoltar: () -> Unit) {
    val state by vm.state.collectAsState()
    val focus = LocalFocusManager.current
    var email     by remember { mutableStateOf("") }
    var senha     by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verSenha  by remember { mutableStateOf(false) }

    LaunchedEffect(state) { if (state is AuthState.Sucesso) onSucesso() }

    Box(Modifier.fillMaxSize()) {
        // Header azul
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.30f)
                .background(Brush.verticalGradient(listOf(AzulEscuro, Azul))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Outlined.School, null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text("Criar conta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            IconButton(onClick = { vm.resetar(); onVoltar() }, modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
                Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White)
            }
        }

        // Card branco
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.78f).align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).background(Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // E-mail
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("E-mail", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF9CA3AF)) },
                        singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul, unfocusedBorderColor = Color(0xFFE5E7EB)),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
                    )
                }

                // Senha
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Senha (mín. 6 caracteres)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                    OutlinedTextField(
                        value = senha, onValueChange = { senha = it },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF9CA3AF)) },
                        trailingIcon = {
                            IconButton(onClick = { verSenha = !verSenha }) {
                                Icon(if (verSenha) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = Color(0xFF9CA3AF))
                            }
                        },
                        visualTransformation = if (verSenha) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul, unfocusedBorderColor = Color(0xFFE5E7EB)),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
                    )
                }

                // Confirmar
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Confirmar senha", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                    OutlinedTextField(
                        value = confirmar, onValueChange = { confirmar = it },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF9CA3AF)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul, unfocusedBorderColor = Color(0xFFE5E7EB)),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                    )
                }

                if (state is AuthState.Erro) {
                    Text((state as AuthState.Erro).msg, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = { vm.cadastrar(email, senha, confirmar) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Azul),
                    enabled = state !is AuthState.Carregando
                ) {
                    if (state is AuthState.Carregando)
                        CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                    else
                        Text("Criar conta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
