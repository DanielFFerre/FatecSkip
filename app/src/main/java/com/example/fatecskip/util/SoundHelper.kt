package com.example.fatecskip.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object SoundHelper {
    fun playVerdictSound(isDanger: Boolean) {
        try {
            val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            if (isDanger) {
                // Alerta Vermelho: Beep agudo e curto
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 200)
            } else {
                // Liberado Verde/Azul: Beep duplo suave de confirmação
                toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 200)
            }
        } catch (e: Exception) {
            Log.e("SoundHelper", "Erro ao reproduzir som de veredito", e)
        }
    }
}
