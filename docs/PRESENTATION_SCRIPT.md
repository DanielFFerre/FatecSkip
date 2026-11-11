# Roteiro de Apresentação — Motorola Moto E40

**Duração alvo:** 6 minutos (faixa aceitável: 5–7 minutos)

**Dispositivo:** Motorola Moto E40, tela HD+ 720 × 1600 px, largura lógica aproximada de 360 dp
**Objetivo:** demonstrar o fluxo do estudante, a persistência local e o despertador sem prometer precisão ou regras acadêmicas além do que o app implementa.

## Preparação antes da banca

Faça estes passos antes de iniciar o cronômetro:

- Instale e abra a versão debug; conclua o onboarding previamente ou deixe-o pronto para a demonstração.
- Deixe internet disponível para demonstrar a sincronização. O fallback offline em runtime usa `DefaultFatecData`.
- Confira que o semestre selecionado tem disciplinas e que a grade contém aula no dia da apresentação. O botão **“Vou Faltar Hoje (+Falta)”** só aparece quando há aula cadastrada hoje.
- Cadastre, se necessário, um evento crítico de teste ou ajuste uma disciplina para que o veredito escolhido seja fácil de explicar. Restaure os valores demonstrativos depois.
- Em Configurações, conceda notificações e permissão de alarmes exatos quando o Android oferecer essas opções. Confira também as permissões de tela cheia / tela de bloqueio do aparelho.
- Para a demonstração do alarme, deixe o aparelho acessível, com volume audível e sem modo Não Perturbe que suprima o toque. Use **Testar Alarme Real (5s)** apenas quando estiver pronto.
- Se a apresentação começar na tela inicial do app, não é necessário forçar o onboarding. Não existe Activity de splash dedicada: a inicialização mostra o indicador/branding do app enquanto lê preferências; o Room é inicializado pela aplicação e pode ser populado na criação do banco.

## 00:00–01:00 — Contexto e abertura

**Ação:** abra o SIGA na cama e mostre a inicialização/tela inicial.

**Fala sugerida:**

“O SIGA na cama ajuda o estudante de DSM da Fatec Diadema a consultar a grade, os eventos e o saldo de faltas num só lugar. O tema Soneca Gamer combina fundo escuro com cores de estado. A inicialização prepara Room, DataStore e os serviços; quando o onboarding já foi concluído, chegamos diretamente ao Oráculo.”

**Mostrar:** ícone, nome do app, indicador de carregamento se aparecer e navegação inferior.

**Nota:** a tela de carregamento do `AppNavHost` espera o DataStore; não a descreva como splash screen nativa do Android.

## 01:00–02:30 — Onboarding e sincronização

**Ação:** se a banca solicitar, demonstre o onboarding após redefini-lo previamente em Configurações. Se não, mostre o semestre já selecionado e navegue às disciplinas.

**Fala sugerida:**

“No onboarding escolhemos o semestre. A aplicação consulta o catálogo JSON via Retrofit e persiste os dados no Room. A sincronização também pode ser refeita em Configurações. Sem resposta online, o fallback atualmente usado é `DefaultFatecData`, então o app continua funcional offline.”

**Mostrar:** seleção de semestre, lista **Minhas Disciplinas**, docente, horário, dias e contador de faltas.

**Evitar:** desligar a rede no meio do fluxo sem ter confirmado os dados offline; o arquivo JSON em assets não é o fallback carregado pelo código atual.

## 02:30–03:30 — Oráculo e regras da Fatec

**Ação:** toque em **“POSSO FALTAR HOJE?”** e depois no ícone de informações do card.

**Fala sugerida:**

“O Oráculo avalia primeiro feriados e ausência de aulas, depois eventos críticos, o limite absoluto de faltas e a margem de segurança escolhida. O veredito não substitui o SIGA nem a secretaria. As regras rápidas deixam claro que atestado curto não significa abono automático e que frequência pode impedir o Exame Final.”

**Mostrar:** mensagem e estado do veredito; diálogo **Regras de Sobrevivência (Fatec)**.

**Nota:** o resultado muda com a data, grade, eventos e faltas armazenados. Explique o resultado real que aparecer, sem depender de um veredito específico.

## 03:30–04:30 — Edição e grade flexível

**Ação:** abra o lápis de uma disciplina, mostre os dados editáveis e o ajuste fino; feche sem salvar alterações acidentais. Abra **+ Matéria Extra**, escolha semestre e mostre o dropdown.

**Fala sugerida:**

“O card registra o bloco de aulas do dia; o lápis permite corrigir docente, horário, dias e ajustar faltas unidade por unidade. A matéria extra pesquisa o catálogo do semestre escolhido e preenche os dados ao selecionar um código. Essas alterações são persistidas localmente e marcadas como personalizadas.”

**Mostrar:** no modal extra, as duas linhas de seletores e a lista do dropdown. Para concluir uma inclusão, use uma matéria realmente desejada e confirme a mensagem; caso contrário, cancele.

## 04:30–06:30 — Alarme real e registro de falta

**Ação:** em Configurações, mostre o horário e a seção **Despertador do Oráculo & Preview**. Inicie **Testar Alarme Real (5s)**; quando a tela de alarme abrir, mostre o veredito e as ações. Se houver aula hoje, toque em **“🛌 Vou Faltar Hoje (+Falta)”**. Se não houver aula cadastrada hoje, demonstre Soneca ou Dispensar e explique a condição do botão.

**Fala sugerida:**

“O AlarmManager dispara o receiver, que abre a Activity sobre a tela bloqueada e apresenta uma notificação de alarme. A soneca agenda dez minutos; dispensar encerra o toque. Quando há disciplinas na grade de hoje, o botão de falta soma as aulas do encontro no Room e fecha a tarefa do alarme.”

**Mostrar:** tela de alarme, som/vibração, botão **Soneca (+10 min)**, botão **Dispensar (Vou pra Aula)** e, quando aplicável, **Vou Faltar Hoje (+Falta)**. Depois, retorne à lista para conferir o saldo atualizado.

**Nota de confiabilidade:** alarmes exatos, notificações em tela cheia e exibição sobre bloqueio dependem de permissões e políticas do Android/dispositivo. Se o sistema atrasar ou não abrir a Activity, explique o fallback inexato e mostre o estado da permissão; não prometa garantia em todos os aparelhos.

## Fechamento (até 07:00)

**Fala sugerida:**

“A solução integra interface Compose, estado assíncrono, dados locais, sincronização REST e notificações/alarmes nativos. Os próximos passos são ampliar os testes automatizados, validar regras com fontes oficiais e evoluir recursos como sons personalizados e backup.”

## Checklist de recuperação rápida

| Situação | Ação |
|---|---|
| Notificação ou teste não aparece | Verifique permissão de notificações e o canal do app nas Configurações do Android. |
| Alarme não toca no minuto exato | Confira autorização de alarme exato; explique o fallback do sistema. |
| Activity não abre sobre a tela bloqueada | Desbloqueie o aparelho e demonstre a tela pelo teste novamente; verifique permissões e política do fabricante. |
| Botão de registrar falta não aparece | Confirme que a grade ativa tem uma disciplina cujo dia de aula corresponde à data corrente. |
| Sem internet | Explique que o fallback runtime é `DefaultFatecData`; use dados já carregados no Room. |
| Onboarding já concluído | Apresente a Home; a redefinição pode ser feita antes da banca em Configurações. |

**Após a apresentação:** restaure as faltas e os eventos modificados para a demonstração e desative o alarme de teste.
