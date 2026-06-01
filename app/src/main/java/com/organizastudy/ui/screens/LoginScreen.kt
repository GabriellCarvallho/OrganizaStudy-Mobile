package com.organizastudy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organizastudy.ui.theme.Azul
import com.organizastudy.ui.theme.AzulEscuro
import com.organizastudy.viewmodel.AuthState
import com.organizastudy.viewmodel.AuthViewModel

@Composable
fun LoginScreen(vm: AuthViewModel, onSucesso: () -> Unit, onCadastro: () -> Unit) {
    val state by vm.state.collectAsState()
    val focus = LocalFocusManager.current
    var email    by remember { mutableStateOf("") }
    var senha    by remember { mutableStateOf("") }
    var verSenha by remember { mutableStateOf(false) }

    LaunchedEffect(state) { if (state is AuthState.Sucesso) onSucesso() }

    Box(Modifier.fillMaxSize()) {
        // Header azul
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.42f)
                .background(Brush.verticalGradient(listOf(AzulEscuro, Azul))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Outlined.School, null, tint = Color.White, modifier = Modifier.size(38.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("OrganizaStudy", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Sua rotina de estudos, no controle.", fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f), modifier = Modifier.padding(top = 4.dp))
            }
        }

        // Card branco
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.68f).align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).background(Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // E-mail
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("E-mail", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        placeholder = { Text("aluno@ifpb.edu.br", color = Color(0xFF9CA3AF)) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF9CA3AF)) },
                        singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Azul, unfocusedBorderColor = Color(0xFFE5E7EB)),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
                    )
                }

                // Senha
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Senha", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus(); vm.login(email, senha) })
                    )
                }

                if (state is AuthState.Erro) {
                    Text((state as AuthState.Erro).msg, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = { vm.login(email, senha) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Azul),
                    enabled = state !is AuthState.Carregando
                ) {
                    if (state is AuthState.Carregando)
                        CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                    else
                        Text("Entrar  →", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                TextButton(onClick = {}, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Esqueceu sua senha?", color = Color(0xFF6B7280), fontSize = 13.sp)
                }

                Spacer(Modifier.weight(1f))

                Text(
                    buildAnnotatedString {
                        append("Não tem conta? ")
                        withStyle(SpanStyle(color = Azul, fontWeight = FontWeight.SemiBold)) { append("Cadastre-se") }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).clickable { vm.resetar(); onCadastro() }.padding(bottom = 8.dp),
                    fontSize = 14.sp, textAlign = TextAlign.Center
                )
            }
        }
    }
}
