
## Instruções para Agentes de IA

Este documento define regras obrigatórias, inegociáveis e cumulativas para qualquer Agente de IA que atue neste repositório.

Qualquer violação destas regras é considerada erro grave de processo.

Este projeto utiliza documentação viva.
Nada é considerado concluído sem documentação adequada.

Arquivo central da verdade técnica:
- PROJECT_SUMMARY.md

## 0. Regra Fundamental de Autoridade

Sempre que uma solicitação não for claramente compatível com estas regras, o agente DEVE parar e perguntar.

É proibido:
- Assumir intenção do usuário
- Improvisar soluções fora do padrão definido

Princípios:
- Estabilidade acima de automação
- Resolver um problema por vez
- Criar primeiro, integrar depois

## 1. Regra de Comandos do Usuário (Palavras‑Chave de Controle)

Toda interação qu commeçar com: Conversa:
- ou não code ou mensagem semelhante
- É proibido codar
- Pode ler arquivos somente isso
- Responder somente via chat
- Nenhum arquivo pode ser criado ou alterado
- Mesmo que dê a entender que é para codar, não code quando tiver o Conversa: no inicio da mensagem
Perguntas que terminarem com "documente!":
- É proibido codar
- Investigar o problema na aplicação
- Criar ou editar documento em docs/BUGS
- Nome do arquivo deve ser objetivo
- Descrever problema, causa e passos realizados
- Marcar cada passo concluído e testado como PRONTA
- Apresentar o relatório no chat
- Aguardar decisão explícita do usuário

## 2. Regra de Centralização da Documentação (docs/)

O diretório docs/ é a fonte única, obrigatória e oficial de documentação do projeto.

É proibido:
- Considerar documentação apenas no chat
- Considerar documentação apenas no código
- Criar documentação fora de docs/

Toda decisão técnica, correção, implementação ou mudança estrutural
deve possuir registro em docs/.

## 3. Regra de Governança das Regras de Negócio

As regras de negócio ativas DEVEM estar em:
- docs/regras_negocio/README.md

Esse arquivo representa a versão vigente das regras.

É obrigatório:
- Versionar regras em docs/regras_negocio/versoes
- Nunca alterar versões antigas
- Manter histórico de mudanças (changelog)

## 4. Regra de Registro de Alterações

Toda alteração no sistema DEVE ser registrada.

Tipos de alteração:
- Implementação de funcionalidade
- Correção de bug
- Ajuste técnico relevante

Cada alteração deve possuir documentação própria,
permitindo rastreabilidade histórica.
Cada Alteração deve seguir passo a passo, definir os passos e aguardar a aprovação fazendo passo por vez.

## 5. Regra de Implementações (Responsabilidade Única)

Uma implementação resolve UM único problema.

É obrigatório:
- Alterar poucos arquivos
- Não misturar múltiplos objetivos
- Documentar em docs/implementacoes
- Versionar incrementalmente (vX.Y.Z)
- Nunca sobrescrever versões antigas