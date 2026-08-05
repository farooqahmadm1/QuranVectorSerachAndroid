package com.learn.ai.deen.quran_android.domain.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.learn.ai.deen.quran_android.data.model.AyaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class QuranAudioPlayer(context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _currentAya = MutableStateFlow<AyaEntity?>(null)
    val currentAya: StateFlow<AyaEntity?> = _currentAya.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var playlist: List<AyaEntity> = emptyList()
    private var currentIndex: Int = -1

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingState: Boolean) {
                _isPlaying.value = isPlayingState
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> _isLoading.value = true
                    Player.STATE_READY -> _isLoading.value = false
                    Player.STATE_ENDED -> {
                        _isLoading.value = false
                        playNext()
                    }
                    Player.STATE_IDLE -> _isLoading.value = false
                }
            }
        })
    }

    fun getAudioUrl(sura: Long, aya: Long): String {
        val suraFormatted = String.format(Locale.US, "%03d", sura)
        val ayaFormatted = String.format(Locale.US, "%03d", aya)
        return "https://everyayah.com/data/Alafasy_128kbps/$suraFormatted$ayaFormatted.mp3"
    }

    fun playAya(aya: AyaEntity, suraPlaylist: List<AyaEntity> = emptyList()) {
        playlist = suraPlaylist.ifEmpty { listOf(aya) }
        currentIndex = playlist.indexOfFirst { it.sura == aya.sura && it.aya == aya.aya }
        if (currentIndex == -1) currentIndex = 0

        playCurrentIndex()
    }

    private fun playCurrentIndex() {
        if (currentIndex < 0 || currentIndex >= playlist.size) {
            stop()
            return
        }

        val aya = playlist[currentIndex]
        _currentAya.value = aya
        val audioUrl = getAudioUrl(aya.sura, aya.aya)

        val mediaItem = MediaItem.fromUri(audioUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun playNext() {
        if (currentIndex >= 0 && currentIndex < playlist.size - 1) {
            currentIndex++
            playCurrentIndex()
        } else {
            stop()
        }
    }

    fun playPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            playCurrentIndex()
        }
    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        player.play()
    }

    fun stop() {
        player.stop()
        _isPlaying.value = false
        _currentAya.value = null
        currentIndex = -1
    }

    fun release() {
        player.release()
    }
}
