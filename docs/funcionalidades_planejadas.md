# Planejamento de Funcionalidades - Sonoplastia Scheduler

Este documento define e organiza todas as funcionalidades planejadas para o sistema de escala do **Ministério de Sonoplastia**, detalhando o que já foi implementado e o que ainda precisa ser desenvolvido.

---

## 📌 Quadro de Controle e Check-ins (Roteiro de Progresso)

Abaixo estão os check-ins organizados por etapas para acompanhar as entregas de cada ciclo de desenvolvimento:

### 🌟 Fase 1: Fundação & Acesso Rápido
- [x] **Check-in 1.1:** Tela de Login integrada ao Room Database
- [x] **Check-in 1.2:** Tela de Cadastro de novos Voluntários
- [x] **Check-in 1.3:** Variável `DESENVOLVIMENTO=true` no `.env` e botão de bypass administrativo
- [x] **Check-in 1.4:** Tela de Gerenciamento de Voluntários (busca, alteração de nível, função e status)

### 📅 Fase 2: Disponibilidade & Autonomia do Voluntário
- [ ] **Check-in 2.1:** Tela `/availability` interativa para salvar dias/turnos que o voluntário pode servir
- [x] **Check-in 2.2:** Integração real dos botões de **Confirmar / Recusar Escala** na tela inicial
- [x] **Check-in 2.3:** Módulo interativo "Agenda do Mês" para auto-seleção (Candidatar-se / Sair de vagas)
- [ ] **Check-in 2.4:** Criação do fluxo de justificativa e solicitação de substituição (`SubstitutionRequest`)
- [ ] **Check-in 2.5:** Histórico completo de escalas individuais e tela de Perfil do usuário

### 🛠️ Fase 3: Administração de Cultos & Criação de Escalas
- [x] **Check-in 3.1:** Cadastro e edição de Cultos Semanais e Eventos Especiais (Definição de vagas)
- [x] **Check-in 3.2:** Tela de Geração Automática baseada nos horários regulamentares da igreja (Templates)
- [x] **Check-in 3.3:** Tela de Distribuição Manual de escalas (vincular operadores elegíveis a eventos)
- [x] **Check-in 3.4:** Desenvolvimento do algoritmo inteligente de sugestão (equidade, disponibilidade e nível técnico)
- [ ] **Check-in 3.5:** Painel de aprovação/revisão de Substituições pendentes

### 🔔 Fase 4: Comunicação & Polimento Geral
- [ ] **Check-in 4.1:** Central de Notificações local (avisos de escalas e respostas de substituições)
- [ ] **Check-in 4.2:** Exportação de escalas ou compartilhamento em texto formatado para grupos de mensagens
- [ ] **Check-in 4.3:** Validação geral de acessibilidade, tamanhos de toque e contraste do tema escuro

---

## 🛠️ Detalhamento dos Módulos

### 1. Módulo de Autenticação e Perfil
Responsável pelo controle de acesso e identificação dos voluntários e administradores.

- [x] **Tela de Login:** Login seguro com e-mail e senha no banco local.
- [x] **Modo Desenvolvedor / Bypass:** Variável `.env` (`DESENVOLVIMENTO=true`) que libera o botão de acesso rápido direto ao perfil administrador para aceleração de testes de usabilidade.
- [x] **Cadastro de Conta:** Registro de novos voluntários com Nome, E-mail, Telefone, Nível Técnico padrão (Iniciante) e Senha.
- [ ] **Tela de Perfil:** Visualização dos dados do usuário logado, possibilidade de editar nome, e-mail, telefone e senha, além de visualizar o histórico individual de escalas.

---

### 2. Módulo do Voluntário (Painel Geral)
Área de trabalho do operador de som escalado.

- [x] **Visualização da Próxima Escala (Parcial):** Card na Home mostrando o próximo evento escalado com data, hora e status.
- [ ] **Ações na Escala:** Integração real dos cliques de **Confirmar** e **Recusar** atualizando o status na tabela de `Schedule` no banco local.
- [ ] **Fluxo de Recusa e Substituição:**
  - Ao clicar em "Recusar", abrir diálogo/tela solicitando justificativa.
  - Registrar uma solicitação na tabela `SubstitutionRequest`.
  - Enviar notificação para o painel administrativo.
- [ ] **Gestão de Disponibilidade Semanal:**
  - *Pronto:* Visualizador compacto de disponibilidade na tela inicial.
  - *A fazer:* Implementação da tela `/availability` onde o voluntário pode selecionar ativamente quais dias da semana e turnos (Manhã, Tarde, Noite) está disponível e salvar no banco de dados.
- [ ] **Agenda de Escalas Completas:** Lista cronológica de todas as escalas passadas e futuras atribuídas ao usuário logado.
- [ ] **Central de Avisos e Notificações:** Tela para visualização das notificações recebidas pelo voluntário (ex: novas escalas atribuídas, respostas de pedidos de substituição, recados gerais do líder).

---

### 3. Módulo Administrativo (Painel de Controle)
Ferramentas para liderança gerenciar a equipe e organizar os cultos.

- [x] **Indicadores Rápidos (KPIs):** Contador de Voluntários ativos, Eventos futuros, Escalas pendentes e Substituições aguardando aprovação.
- [x] **Gerenciamento de Voluntários:**
  - Filtro e busca em tempo real (Nome, E-mail, Telefone).
  - Alteração do nível de proficiência técnica (`Iniciante`, `Intermediário`, `Avançado`, `Líder`).
  - Alteração de perfil de acesso (`VOLUNTEER` ou `ADMIN`).
  - Chave de Ativação/Inativação (`ACTIVE` ou `INACTIVE`) para afastar temporariamente voluntários das escalas automáticas.
- [x] **Gestão de Cultos e Eventos:**
  - Cadastro de novos cultos e eventos pontuais (data, horário de início, horário de término, nome do evento).
  - Configuração do número de operadores necessários para cada evento específico.
- [x] **Criação e Distribuição de Escalas:**
  - **Distribuição Manual:** Tela para selecionar um culto e associar voluntários da lista que estejam com status "disponível" naquele dia/turno.
  - **Sugestão Automática (Algoritmo inteligente):** Botão para sugerir escala automática cruzando a disponibilidade semanal, o nível técnico requerido do evento e equilibrando a quantidade de escalas de cada voluntário no mês (princípio de equidade para evitar sobrecarga).
- [ ] **Painel de Substituições:**
  - Visualizar solicitações de substituição abertas com motivo/justificativa.
  - Atribuir um substituto e aprovar o pedido de troca de forma fluida.
