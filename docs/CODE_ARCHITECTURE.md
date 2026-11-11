# Arquitetura de Código — SIGA na cama

Guia de arguição técnica com o fluxo de execução e o vínculo entre requisitos acadêmicos e arquivos implementados.

## Visão em camadas

```text
MainActivity
  └── AppNavHost ── Navigation Compose
       ├── OnboardingScreen / OnboardingViewModel
       ├── HomeScreen / HomeViewModel ── AddExtraSubjectDialog / EditSubjectDialog
       ├── EventsScreen / EventsViewModel
       └── SettingsScreen / SettingsViewModel
              │
              ├── UserPreferencesRepository ── DataStore
              └── AcademicRepository
                    ├── DAOs / Room / SQLite
                    └── FatecApiService / Retrofit / JSON

FatecSkipApp ── inicialização de dependências e reagendamento
DailyCheckWorker ── notificações periódicas e preview
AlarmScheduler ── AlarmManager ── AlarmReceiver
                                  └── AlarmRingingActivity
```

## 1. Camada de apresentação — UI e Compose

### Entrada e navegação

- `MainActivity.kt` habilita edge-to-edge e monta o tema e `AppNavHost`.
- `ui/navigation/AppNavHost.kt` lê o estado de onboarding no DataStore e direciona o primeiro uso para onboarding ou, nos usos seguintes, para a tela inicial.
- `ui/navigation/Screen.kt` declara as rotas de onboarding, Oráculo, eventos e configurações. A barra inferior aparece nas telas principais.
- `ui/theme/Color.kt`, `Theme.kt` e `Type.kt` centralizam o tema “Soneca Gamer”.

### Telas, estado e ViewModels

| Tela | Responsabilidade | ViewModel |
|---|---|---|
| `ui/screens/onboarding/OnboardingScreen.kt` | Captura a configuração inicial e o semestre. | `OnboardingViewModel.kt` |
| `ui/screens/home/HomeScreen.kt` | Exibe o Oráculo, grade e faltas; abre regras, edição e inclusão de matérias. | `HomeViewModel.kt` |
| `ui/screens/events/EventsScreen.kt` | Exibe e permite gerenciar eventos. | `EventsViewModel.kt` |
| `ui/screens/settings/SettingsScreen.kt` | Preferências, sincronização, notificações, alarme e preview. | `SettingsViewModel.kt` |
| `ui/screens/alarm/AlarmRingingActivity.kt` | Tela Compose de alarme em Activity separada, com som, vibração e ações. | Usa repositório e preferências da aplicação; não tem ViewModel próprio. |

`HomeViewModel` expõe fluxos observáveis de disciplinas e preferências, chama `AcademicRepository.evaluateSkipVerdict` e encaminha atualizações de faltas. `SettingsViewModel` grava preferências, sincroniza a API e aciona o scheduler de alarmes.

### Requisitos demonstráveis na UI

- Navegação entre as telas principais via Navigation Compose.
- Veredito contextual e lista “Minhas Disciplinas”.
- Card com falta por bloco, decremento unitário e ícone de edição.
- Modal de edição de disciplina e `AddExtraSubjectDialog` com catálogo e inclusão manual.
- Diálogo de regras acadêmicas acessível pelo card do Oráculo.
- Configuração e teste do alarme, horário e preview em Configurações.

## 2. Camada de dados e persistência — Room / SQLite

### Modelo e acesso

- `data/local/AppDatabase.kt` declara o banco Room, entidades e DAOs; no primeiro `onCreate`, popula a base com grade, feriados e desculpas locais.
- `data/local/entities/SubjectEntity.kt` representa disciplina, semestre, dias, aulas por encontro, faltas, limite, docente, horário, `isCustomized` e `isExtension`.
- `data/local/SubjectDao.kt` fornece consultas por semestre e operações de inserção, atualização e remoção.
- `EventDao.kt` e `ExcuseDao.kt` persistem agenda e textos de desculpa.
- `data/local/entities/EventEntity.kt` e `ExcuseEntity.kt` completam os modelos.

### Proteção de dados personalizados

`AcademicRepository.updateSubjectDetails`, `addCustomSubject` e `addExtraSubject` gravam disciplinas como `isCustomized = true`. O fallback offline remove somente disciplinas não personalizadas por meio de `clearAllNonCustomizedSubjects()`.

Na sincronização online, valores existentes como saldo de faltas, docente e horário são recuperados em certos casos e a flag é preservada. Entretanto, outros campos da entidade podem ser atualizados pela API; não se deve afirmar que a flag protege integralmente todas as edições em qualquer sincronização.

### Frequência e registro de ausência

- `AcademicRepository.evaluateSkipVerdict` verifica feriado/domingo, falta de aulas, evento crítico, limite absoluto e margem de segurança.
- `registerAbsenceForToday(semester, date)` busca disciplinas do semestre, filtra as que têm aula no dia e soma `classesPerDay` ao saldo, respeitando o teto implementado.
- O retorno da função é a quantidade de disciplinas atualizadas, não o número total de aulas/faltas somadas.

Esse fluxo atende à persistência local e à regra de registro por encontro. Para arguição, explique que `maxAbsences` está associado à disciplina; os valores usuais de 80h/40h não substituem os dados salvos.

## 3. Camada de preferências — DataStore

`data/preferences/UserPreferencesRepository.kt` centraliza DataStore Preferences com `Flow` e operações suspensas.

Preferências existentes incluem apelido, semestre, nível de coragem, URL da API, permissão lógica de notificações e conclusão do onboarding. O alarme acrescenta:

- `alarmEnabledFlow`, padrão desligado;
- `alarmHourFlow` e `alarmMinuteFlow`, com padrão 06:30;
- `nightPreviewEnabledFlow`, padrão desligado.

`SettingsViewModel` observa os flows, atualiza valores e agenda/cancela os alarmes correspondentes. A permissão de alarme exato é gerenciada pelo Android e não é substituída pelo valor salvo no DataStore.

## 4. Camada de rede — API REST e fallback

- `data/remote/FatecApiService.kt` define a chamada Retrofit GET para uma URL.
- `data/remote/dto/FatecDataDto.kt` descreve calendário, desculpas e currículos JSON.
- `data/repository/AcademicRepository.syncFromApi()` busca os dados, separa docente informado entre parênteses, reaproveita alguns valores existentes e atualiza os dados Room.
- `OnboardingViewModel` solicita sincronização durante o onboarding; `SettingsViewModel` também sincroniza quando o usuário altera o semestre ou solicita manualmente.
- `data/local/DefaultFatecData.kt` é o fallback efetivamente usado pelo repositório quando a resposta online não está disponível.

**Distinção importante para a banca:** o projeto contém `app/src/main/assets/fatec_api.json` e um `fatec_api.json` na raiz, mas `syncFromApi` não carrega esses arquivos como fallback local. O fallback runtime é `DefaultFatecData`. Evite afirmar que o asset JSON é lido automaticamente sem implementar esse caminho.

## 5. Camada de background, notificações e alarmes

### WorkManager: `DailyCheckWorker`

`worker/DailyCheckWorker.kt` faz trabalho periódico e produz notificações de contexto para o dia seguinte. Quando recebe `IS_NIGHT_PREVIEW = true`, consulta `evaluateSkipVerdict` usando a data de amanhã e envia o resumo do Oráculo. O Worker é adequado a trabalho adiável, não a precisão de relógio.

`worker/NotificationHelper.kt` cria o canal e mostra notificações regulares. O recurso `res/drawable/ic_notification_bed.xml` é usado como ícone pequeno monocromático.

### AlarmManager: scheduler e receiver

- `worker/AlarmScheduler.kt` agenda disparos diários, soneca, testes e preview para 21:00. Solicita `setExactAndAllowWhileIdle` quando permitido e usa fallback inexato quando necessário.
- `worker/AlarmReceiver.kt` recebe os PendingIntents. No alarme matinal, exibe notificação `CATEGORY_ALARM` com full-screen intent e inicia a Activity. Para preview, encaminha um pedido com `IS_NIGHT_PREVIEW` ao Worker.
- `FatecSkipApp.kt` observa preferências salvas e agenda/cancela alarme e preview na inicialização do processo.
- `AndroidManifest.xml` declara permissões e componentes; a Activity de alarme é excluída dos Recentes, isolada em tarefa e configurada para aparecer sobre a tela bloqueada.

### Tela do alarme

`ui/screens/alarm/AlarmRingingActivity.kt` toca o ringtone padrão em loop quando suportado, vibra e consulta o veredito de hoje. Oferece soneca de 10 minutos, dispensar e registro das faltas das aulas de hoje. O encerramento para som/vibração, cancela a notificação e chama `finishAndRemoveTask()`.

A implementação acende a tela e a mantém ativa com flags de janela / APIs de lock screen; ela **não** mantém um `PowerManager.WakeLock` explícito. Mesmo com full-screen intent e permissão declarada, a exibição depende das políticas da versão Android, da permissão de notificações e das configurações do dispositivo.

## 6. Inicialização e composição manual

`FatecSkipApp.kt` inicializa Room, DataStore, Retrofit e `AcademicRepository`, além de agendar tarefas no início do processo. O `AppNavHost` cria ViewModels por fábricas e fornece o repositório e as preferências.

Não há framework de injeção de dependência dedicado. Para a escala atual, a composição é manual; `FatecSkipApp.instance` é o acesso global usado por algumas superfícies, inclusive pela Activity/Worker do alarme.

## 7. Pontos de validação e limites conhecidos

- Os testes em `app/src/test` e `app/src/androidTest` são exemplos padrão; regras de frequência, sincronização e alarme ainda precisam de testes específicos.
- O WorkManager periódico pode executar fora do minuto planejado; o preview às 21:00 usa AlarmManager como gatilho e Worker para o processamento.
- A precisão do AlarmManager e a abertura da tela sobre o bloqueio devem ser demonstradas em dispositivo e com permissões concedidas.
- O fallback runtime usa Kotlin (`DefaultFatecData`), não os JSONs do projeto.
- Regras acadêmicas e prazos citados pelo produto devem ser confirmados com fontes oficiais da instituição.
