package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class LetterSpeaker(context: Context) : TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
  private var ready = false

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val arLocale = Locale.forLanguageTag("ar")
      val res = tts?.setLanguage(arLocale)
      ready = res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED
      if (!ready) {
        tts?.setLanguage(Locale.getDefault())
        ready = true
      }
      tts?.setSpeechRate(0.9f)
    }
  }

  fun speakLetter(text: String) {
    if (!ready) return
    try {
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "pixel_harf_letter")
    } catch (_: Throwable) {
      // Safe fallback if TTS service is unavailable
    }
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
    } catch (_: Throwable) {
    }
    tts = null
    ready = false
  }
}
