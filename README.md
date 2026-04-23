# 📱 OrganizaStudy Mobile

> Transformando desorganização acadêmica em produtividade real.

Um ecossistema mobile que permite estudantes gerenciarem disciplinas, cronometrarem sessões de estudo e acompanharem seu desempenho — tudo em tempo real.

---

## 🎯 O Problema & A Solução

Muitos estudantes perdem o ritmo de aprendizado por não saberem quanto tempo dedicaram a cada matéria, ou por deixarem tarefas se acumularem sem controle.

O **OrganizaStudy** resolve isso centralizando o fluxo real de estudo em três etapas:

| Etapa | O que faz |
|-------|-----------|
| 📋 **Planejamento** | Gestão de disciplinas e tarefas por prioridade |
| ⏱️ **Execução** | Timer integrado que registra o esforço automaticamente |
| 📊 **Análise** | Dashboard com métricas de tempo e streaks de consistência |

---

## ✨ Funcionalidades

### 🔐 Gestão de Acesso
- **Firebase Auth** — Fluxo seguro de Cadastro, Login e Recuperação de senha
- **Persistência de sessão** — Acesso rápido sem re-login

### 📚 Organização Acadêmica
- **Disciplinas (CRUD)** — Personalização por cores e busca dinâmica
- **Gestão de Tarefas** — Priorização (Alta / Média / Baixa) com prazos integrados

### ⏱️ Ciclo de Estudo Ativo
- **Timer Inteligente** — Registro automático de horas no Firestore vinculado à disciplina
- **Dashboard** — Visualização de horas totais, tarefas pendentes e dias consecutivos (Streak)

---

## 🛠️ Stack Técnica

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Kotlin |
| UI | Jetpack Compose (Material Design 3) |
| Arquitetura | MVVM + Clean Architecture |
| Banco de Dados | Cloud Firestore (tempo real) |
| Injeção de Dependência | Hilt |
| Navegação | Navigation Compose com Safe Args |
| Autenticação | Firebase Auth |

---

## 📸 Interface

<table>
  <tr>
    <td align="center"><b>Login / Cadastro</b></td>
    <td align="center"><b>Dashboard</b></td>
  </tr>
  <tr>
    <td><img src="screenshots/login_cadastro.png" width="200"/></td>
    <td><img src="screenshots/dashboard.png" width="200"/></td>
  </tr>
  <tr>
    <td align="center"><b>Disciplinas</b></td>
    <td align="center"><b>Tarefas</b></td>
  </tr>
  <tr>
    <td><img src="screenshots/disciplinas.png" width="200"/></td>
    <td><img src="screenshots/tarefas.png" width="200"/></td>
  </tr>
  <tr>
    <td align="center" colspan="2"><b>Busca Global</b></td>
  </tr>
  <tr>
    <td align="center" colspan="2"><img src="screenshots/busca_global.png" width="200"/></td>
  </tr>
</table>

---

## 🚀 Como Executar

**1. Clone o repositório**
```bash
git clone https://github.com/seu-usuario/organizastudy-mobile.git
```

**2. Abra no Android Studio**
- Use a versão **Ladybug** ou superior

**3. Configure o Firebase**
- Crie um projeto no [Console do Firebase](https://console.firebase.google.com/)
- Baixe o arquivo `google-services.json` e adicione-o na pasta `/app`

**4. Execute o projeto**
- Rode o **Gradle Sync**
- Clique em **Run** no emulador ou dispositivo físico

---

## 👨‍💻 Autor

Desenvolvido por **Gabriel Pereira de Carvalho**  
