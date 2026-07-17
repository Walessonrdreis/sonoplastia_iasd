# Regras de Negócio - Sonoplastia Scheduler

Este diretório contém a especificação e governança de todas as regras de negócio vigentes para o aplicativo.

## 1. Perfis e Permissões

### Administrador
- **Gerenciar Voluntários:** Ativação/Inativação, alteração de nível técnico.
- **Criar/Editar Cultos e Eventos:** Cadastro de datas, horários e quantidade de operadores necessários.
- **Gestão de Escalas:** Criação manual de escalas, edição e exclusão de voluntários escalados.
- **Visualização Completa:** Acesso a todas as métricas de confirmação e solicitações de substituição.

### Voluntário (Sonoplasta)
- **Atualizar Perfil:** Manutenção de nome, telefone, e-mail e nível técnico.
- **Disponibilidade Semanal:** Indicação de horários disponíveis para cada dia da semana.
- **Visualização e Confirmação:** Verificação de escalas atribuídas com opções de "Confirmar" ou "Recusar".
- **Substituição:** Solicitação de substituição com motivo quando impossibilitado de comparecer.

---

## 2. Cultos Padrão e Recorrência (Horários Oficiais de Sonoplastia)
O sistema provisiona automaticamente e aceita a criação dos cultos semanais recorrentes da igreja:
- **Quarta-Feira (Culto de Oração):** 20:00 - 21:00 (Requer 1 Operador)
- **Sábado de Manhã (Culto de Sábado):** 09:00 - 11:45 (Requer 2 Operadores)
- **Sábado de Tarde (Culto Jovem - JA):** 17:00 - 18:00 (Requer 1 Operador)
- **Domingo (Culto de Evangelismo):** 19:00 - 20:00 (Requer 2 Operadores)
- **Eventos Especiais:** Possibilidade de outros horários personalizados (ex: Semana de Oração, Reuniões administrativas).

---

## 3. Abordagens de Preenchimento da Escala (Multi-Opções)
A escala de sonoplastia do mês possui três abordagens integradas para preenchimento de suas vagas:
1. **Geração Automática por Disponibilidade (Sugestão IA):** O administrador utiliza a aba "SUGERIDOS (IA)" que lista os voluntários em ordem de prioridade técnica e equidade, detectando conflitos de horários em tempo real.
2. **Auto-Preenchimento por Adesão (Self-Selection):** Os próprios voluntários visualizam todos os cultos cadastrados do mês na aba "Agenda" de seus painéis e se candidatam livremente às vagas abertas ("Quero Ajudar"), permitindo que assumam múltiplos dias de acordo com seu desejo pessoal, ou saiam de escalas caso necessário.
3. **Atribuição Direta (Manual):** O administrador pode, a qualquer momento, clicar em uma vaga disponível e designar qualquer voluntário cadastrado de forma direta.

---

## 3. Algoritmo de Sugestão e Escala Automática
A sugestão automática de voluntários deve priorizar operadores ordenando-os com base nos seguintes critérios cumulativos:
1. **Disponibilidade:** Horário do evento deve estar inteiramente contido na disponibilidade semanal informada pelo voluntário.
2. **Sem Conflitos:** O voluntário não pode possuir outra escala ativa no mesmo intervalo de horário.
3. **Equidade (Menos Escalas Recentes):** Voluntários com menor número de escalas recentes têm prioridade superior para evitar sobrecarga.
4. **Status Ativo:** Apenas voluntários com status `ACTIVE` podem ser sugeridos.
