# PROJECT_SUMMARY - Sonoplastia Scheduler

Este é o arquivo central da verdade técnica para o aplicativo **Sonoplastia Scheduler**.

## 1. Visão Geral do Projeto
O aplicativo destina-se à gestão da escala de sonoplastas de uma igreja, permitindo que os voluntários informem sua disponibilidade semanal e que os administradores organizem as escalas de cultos e eventos de forma manual ou sugerida automaticamente.

## 2. Tecnologias Utilizadas
- **Linguagem:** Kotlin
- **Interface:** Jetpack Compose (Material Design 3 - Tema Sophisticated Dark)
- **Banco de Dados Local:** Room Database (Offline First)
- **Gerenciamento de Estado:** ViewModel & StateFlow
- **Navegação:** Navigation Component (Compose)
- **Segurança & Variáveis:** Secrets Gradle Plugin com `.env` / `.env.example`

## 3. Arquitetura
O app segue as boas práticas de arquitetura recomendadas para Android:
- **MVVM (Model-View-ViewModel):** Separação clara de lógica de apresentação e dados.
- **Repository Pattern:** Abstração da fonte de dados local (Room).
- **Offline First:** Operação totalmente funcional sem internet instantânea.

## 4. Estrutura do Banco de Dados (Room Entities)
- **User:** Cadastro dos voluntários e administradores (id, name, email, phone, role, status, level).
- **Availability:** Dias da semana e horários disponíveis por voluntário.
- **Event:** Cultos recorrentes e eventos especiais com quantidade necessária de operadores.
- **Schedule:** Atribuições de operadores aos eventos (status: ESCALADO, CONFIRMADO, etc.).
- **SubstitutionRequest:** Pedidos de substituição criados por voluntários.
- **Notification:** Avisos e convites locais enviados aos usuários.

---

## Histórico de Versões e Modificações

| Versão | Descrição | Status |
|---|---|---|
| **v1.0.0** | Implementação inicial do banco de dados, fluxos de autenticação, home do voluntário, painel administrativo e design "Sophisticated Dark". | Concluído |
| **v1.1.0** | Integração da variável de ambiente `DESENVOLVIMENTO` em `.env` e botão de bypass para login administrativo imediato. | Concluído |
| **v1.2.0** | Implementação da tela de Gerenciamento de Voluntários (AdminVolunteersScreen), adicionando listagem, busca por termo, controle de perfil/função, alteração de nível técnico e ativação/inativação via switch. | Concluído |
| **v1.3.0** | Implementação da tela de Criação de Escalas (AdminCreateScheduleScreen), incluindo adição de cultos/eventos, controle de vagas, remoção/atualização de operadores e algoritmo inteligente de auto-sugestão baseado em disponibilidade, conflitos e equidade. | Concluído |
| **v1.4.0** | Implementação da Geração de Escala Mensal Automática baseada em templates de cultos fixos e do Módulo de Auto-Preenchimento por Adesão (Auto-Seleção/Self-Selection) permitindo que os voluntários escolham seus dias de serviço, visualizem equipes e gerenciem suas vagas. | Concluído |
| **v1.4.1** | Criação automática de voluntário padrão no banco de dados e implementação de botões de login rápido (Admin / Voluntário) para testes dinâmicos de telas. | Concluído |
| **v1.5.0** | Reformulação completa da tela do Administrador (Tabs), adicionando painel de aprovação/revisão de solicitações de substituição pendentes e central de envio de comunicados em massa. Botão de solicitação de substituição na home do voluntário. | Concluído |
| **v1.6.0** | Refatoração da navegação na área do voluntário substituindo a barra de abas inferior por um Menu Lateral (Hamburguer) tradicional. Inclui cabeçalho de perfil de operador, nível técnico, acesso a comunicados com contador ativo, indicação de disponibilidade, retorno contextualizado para modo administrador (quando em modo espelho) e saída segura. | Concluído |
