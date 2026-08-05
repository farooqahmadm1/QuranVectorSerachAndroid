package com.learn.ai.deen.quran_android.data.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.VectorDistanceType
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class AyaEntity(
    @Id var id: Long = 0,
    var sura: Long = 0,
    var aya: Long = 0,
    var text: String? = null,
    var md5: String? = null,
    var translationEn: String? = null,
    var translationUr: String? = null,
    var tafsir: String? = null,
    @HnswIndex(dimensions = 256, distanceType = VectorDistanceType.DEFAULT)
    var embedding: FloatArray? = null
) {
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sura.hashCode()
        result = 31 * result + aya.hashCode()
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (md5?.hashCode() ?: 0)
        result = 31 * result + (translationEn?.hashCode() ?: 0)
        result = 31 * result + (translationUr?.hashCode() ?: 0)
        result = 31 * result + (tafsir?.hashCode() ?: 0)
        result = 31 * result + (embedding?.contentHashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AyaEntity

        if (id != other.id) return false
        if (sura != other.sura) return false
        if (aya != other.aya) return false
        if (text != other.text) return false
        if (md5 != other.md5) return false
        if (translationEn != other.translationEn) return false
        if (translationUr != other.translationUr) return false
        if (tafsir != other.tafsir) return false
        if (embedding != null) {
            if (other.embedding == null) return false
            if (!embedding.contentEquals(other.embedding)) return false
        } else if (other.embedding != null) return false

        return true
    }

    override fun toString(): String {
        return "AyaEntity(id=$id, sura=$sura, aya=$aya, text='$text', translationEn=$translationEn, translationUr=$translationUr, tafsir=$tafsir)"
    }
}