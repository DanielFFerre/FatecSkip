package com.example.fatecskip.data.local

import com.example.fatecskip.data.local.entities.EventEntity
import com.example.fatecskip.data.local.entities.ExcuseEntity
import com.example.fatecskip.data.local.entities.SubjectEntity

/**
 * Grade Oficial Fatec - Horário de Aulas - 1º Semestre de 2026 - DSM (Matutino)
 * Extraído diretamente do PDF oficial da faculdade com os nomes exatos das matérias e professores.
 */
object DefaultFatecData {

    fun getDefaultSubjects(): List<SubjectEntity> {
        val list = mutableListOf<SubjectEntity>()

        // ==========================================
        // 1º SEMESTRE - MATUTINO DSM
        // ==========================================
        list.add(SubjectEntity(code = "DWI01", name = "Desenv. Web I (Hélio Cesar)", semester = 1, dayOfWeek = 1, daysOfWeek = "1,2", classesPerDay = 2, maxAbsences = 20))
        list.add(SubjectEntity(code = "ALG01", name = "Algoritmos (Lúcio)", semester = 1, dayOfWeek = 1, daysOfWeek = "1,3", classesPerDay = 2, maxAbsences = 20))
        list.add(SubjectEntity(code = "MBD01", name = "Model. de Banco (Sueli Ap)", semester = 1, dayOfWeek = 2, daysOfWeek = "2", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "SOP01", name = "Sistema Op (Silas / Ismael)", semester = 1, dayOfWeek = 3, daysOfWeek = "3", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "ES101", name = "Eng. Software I (Rháleff)", semester = 1, dayOfWeek = 4, daysOfWeek = "4", classesPerDay = 4, maxAbsences = 20, isExtension = true))
        list.add(SubjectEntity(code = "DDG01", name = "Design Digital (Patricia)", semester = 1, dayOfWeek = 5, daysOfWeek = "5", classesPerDay = 4, maxAbsences = 20))

        // ==========================================
        // 2º SEMESTRE - MATUTINO DSM
        // ==========================================
        list.add(SubjectEntity(code = "ES201", name = "Eng Software II (Luiz Antonio / Marco A)", semester = 2, dayOfWeek = 1, daysOfWeek = "1", classesPerDay = 4, maxAbsences = 20, isExtension = true))
        list.add(SubjectEntity(code = "EDD01", name = "Estrutura Dados (Lucio)", semester = 2, dayOfWeek = 1, daysOfWeek = "1,3", classesPerDay = 2, maxAbsences = 20))
        list.add(SubjectEntity(code = "DW201", name = "Desenv. Web II (Bruno)", semester = 2, dayOfWeek = 2, daysOfWeek = "2", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "MPC01", name = "Mat para Comp (Rosângela)", semester = 2, dayOfWeek = 3, daysOfWeek = "3", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "TPI01", name = "Téc Prog I (Marcos Vasconcelos)", semester = 2, dayOfWeek = 4, daysOfWeek = "4", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "BDR01", name = "Banco Dados Rel. (Rafael Miranda / Renan)", semester = 2, dayOfWeek = 5, daysOfWeek = "5", classesPerDay = 4, maxAbsences = 20))

        // ==========================================
        // 3º SEMESTRE - MATUTINO DSM
        // ==========================================
        list.add(SubjectEntity(code = "IHC01", name = "Int. H. Computador (Lucio)", semester = 3, dayOfWeek = 1, daysOfWeek = "1", classesPerDay = 2, maxAbsences = 10))
        list.add(SubjectEntity(code = "GAG01", name = "Gestão Ágil (Bruno)", semester = 3, dayOfWeek = 1, daysOfWeek = "1", classesPerDay = 4, maxAbsences = 20, isExtension = true))
        list.add(SubjectEntity(code = "BDN01", name = "Ban. Dados N Rel. (Marcos Vasconcelos)", semester = 3, dayOfWeek = 2, daysOfWeek = "2", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "DW301", name = "Desenv. Web III (Vinicius Heltai)", semester = 3, dayOfWeek = 3, daysOfWeek = "3", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "ALG03", name = "Álgebra Linear (Rosângela)", semester = 3, dayOfWeek = 4, daysOfWeek = "4", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "ING01", name = "Inglês I (Luciano / Valteir)", semester = 3, dayOfWeek = 5, daysOfWeek = "5", classesPerDay = 2, maxAbsences = 10))
        list.add(SubjectEntity(code = "TP201", name = "Téc Prog II (Vinicius)", semester = 3, dayOfWeek = 5, daysOfWeek = "5", classesPerDay = 4, maxAbsences = 20))

        // ==========================================
        // 4º SEMESTRE - MATUTINO DSM
        // ==========================================
        list.add(SubjectEntity(code = "IOT01", name = "Internet Coisas (Rháleff / Ismael)", semester = 4, dayOfWeek = 1, daysOfWeek = "1", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "LDW01", name = "Lab. Des. Web (Edson)", semester = 4, dayOfWeek = 2, daysOfWeek = "2", classesPerDay = 4, maxAbsences = 20, isExtension = true))
        list.add(SubjectEntity(code = "PDM01", name = "Prog. Disp. Móveis I (Rafael Lopes)", semester = 4, dayOfWeek = 2, daysOfWeek = "2,4", classesPerDay = 2, maxAbsences = 20))
        list.add(SubjectEntity(code = "IEC01", name = "Int. Entrega Contínua (Rafael Lopes)", semester = 4, dayOfWeek = 3, daysOfWeek = "3", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "EST01", name = "Estat. Aplicada (Diogo Soares)", semester = 4, dayOfWeek = 4, daysOfWeek = "4", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "ING02", name = "Inglês II (Luciano / Valteir)", semester = 4, dayOfWeek = 5, daysOfWeek = "5", classesPerDay = 2, maxAbsences = 10))
        list.add(SubjectEntity(code = "UX001", name = "Experiência Usuário (Carlo Ronny)", semester = 4, dayOfWeek = 5, daysOfWeek = "5", classesPerDay = 2, maxAbsences = 10))

        // ==========================================
        // 5º SEMESTRE - MATUTINO DSM
        // ==========================================
        list.add(SubjectEntity(code = "FRT01", name = "F. Redação técnica (Thierri)", semester = 5, dayOfWeek = 1, daysOfWeek = "1", classesPerDay = 2, maxAbsences = 10))
        list.add(SubjectEntity(code = "ING03", name = "Inglês III (Amaury)", semester = 5, dayOfWeek = 1, daysOfWeek = "1", classesPerDay = 2, maxAbsences = 10))
        list.add(SubjectEntity(code = "CPN01", name = "Comp. Nuvem I (Odair / Renan)", semester = 5, dayOfWeek = 2, daysOfWeek = "2", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "LDM01", name = "Lab. de Des. p/ Disp. Móveis (Carlo Ronny)", semester = 5, dayOfWeek = 3, daysOfWeek = "3", classesPerDay = 4, maxAbsences = 20, isExtension = true))
        list.add(SubjectEntity(code = "PDM03", name = "Prog. Disp. Móv. II (Vinicius)", semester = 5, dayOfWeek = 3, daysOfWeek = "3,5", classesPerDay = 2, maxAbsences = 20))
        list.add(SubjectEntity(code = "SDA01", name = "Seg. D. Aplicações (Evandro / Ismael)", semester = 5, dayOfWeek = 4, daysOfWeek = "4", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "APM01", name = "Aprendiz. Máquina (Rháleff / Marcelo)", semester = 5, dayOfWeek = 5, daysOfWeek = "5", classesPerDay = 4, maxAbsences = 20))

        // ==========================================
        // 6º SEMESTRE - MATUTINO DSM
        // ==========================================
        list.add(SubjectEntity(code = "LDM02", name = "Lab. Desenv. Multip. (Rafael Guem / Renan)", semester = 6, dayOfWeek = 1, daysOfWeek = "1", classesPerDay = 4, maxAbsences = 20, isExtension = true))
        list.add(SubjectEntity(code = "ETC01", name = "Ética (Janaina)", semester = 6, dayOfWeek = 2, daysOfWeek = "2", classesPerDay = 2, maxAbsences = 10))
        list.add(SubjectEntity(code = "MND01", name = "Mineração de Dados (Edson)", semester = 6, dayOfWeek = 2, daysOfWeek = "2,3", classesPerDay = 2, maxAbsences = 20))
        list.add(SubjectEntity(code = "QTS01", name = "Qual. e Testes SW (Edson)", semester = 6, dayOfWeek = 3, daysOfWeek = "3", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "CN201", name = "Comp. Nuvem II (Renan)", semester = 6, dayOfWeek = 4, daysOfWeek = "4", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "PLN01", name = "PLN (Andrea)", semester = 6, dayOfWeek = 5, daysOfWeek = "5", classesPerDay = 4, maxAbsences = 20))
        list.add(SubjectEntity(code = "ING04", name = "Inglês IV (Luciano / Valteir)", semester = 6, dayOfWeek = 5, daysOfWeek = "5", classesPerDay = 2, maxAbsences = 10))

        return list
    }

    fun getDefaultHolidays(): List<EventEntity> {
        return listOf(
            EventEntity(title = "Independência do Brasil", date = "2026-09-07", type = "HOLIDAY", isCritical = false),
            EventEntity(title = "Nossa Sra. Aparecida (Padroeira)", date = "2026-10-12", type = "HOLIDAY", isCritical = false),
            EventEntity(title = "Dia do Professor (Folga Fatec!)", date = "2026-10-15", type = "HOLIDAY", isCritical = false),
            EventEntity(title = "Dia do Servidor Público", date = "2026-10-28", type = "HOLIDAY", isCritical = false),
            EventEntity(title = "Finados", date = "2026-11-02", type = "HOLIDAY", isCritical = false),
            EventEntity(title = "Proclamação da República", date = "2026-11-15", type = "HOLIDAY", isCritical = false),
            EventEntity(title = "Dia da Consciência Negra", date = "2026-11-20", type = "HOLIDAY", isCritical = false),
            EventEntity(title = "Recesso de Fim de Ano", date = "2026-12-24", type = "HOLIDAY", isCritical = false),
            EventEntity(title = "Natal", date = "2026-12-25", type = "HOLIDAY", isCritical = false)
        )
    }

    fun getDefaultExcuses(): List<ExcuseEntity> {
        return listOf(
            ExcuseEntity(text = "Meu cachorro comeu meu bilhete único."),
            ExcuseEntity(text = "Deu conflito de merge no meu despertador hoje."),
            ExcuseEntity(text = "Fiquei preso num loop infinito tentando sair da cama."),
            ExcuseEntity(text = "O ônibus passou direto e levou minha vontade de estudar junto."),
            ExcuseEntity(text = "Tive que ficar em casa pra atualizar o Windows aos 99%."),
            ExcuseEntity(text = "A linha do trem quebrou e tive que voltar pra dormir."),
            ExcuseEntity(text = "Minha bateria social descarregou e a Fatec não tem tomada bivolt."),
            ExcuseEntity(text = "Deu NullPointerException na minha motivação nesta manhã."),
            ExcuseEntity(text = "O Oráculo da Falta me avisou que as estrelas do SIGA não recomendam sair de casa hoje."),
            ExcuseEntity(text = "Estou realizando testes de estresse no meu colchão."),
            ExcuseEntity(text = "Fui compilar o café da manhã e deu Timeout."),
            ExcuseEntity(text = "Fui dar git push na coragem e deu merge conflict.")
        )
    }
}
