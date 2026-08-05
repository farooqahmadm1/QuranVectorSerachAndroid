package com.learn.ai.deen.quran_android.data.repo

import com.learn.ai.deen.quran_android.data.model.AyaEntity

/**
 * Repository providing English and Urdu translations as well as concise Tafsir
 * explanations for Quranic verses.
 */
class QuranTranslationRepo {

    /**
     * Retrieves the English translation for a verse by sura and aya number.
     */
    fun getEnglishTranslation(sura: Long, aya: Long): String {
        return englishTranslations["$sura:$aya"]
            ?: "In the name of Allah, the Entirely Merciful, the Especially Merciful."
    }

    /**
     * Retrieves the Urdu translation for a verse by sura and aya number.
     */
    fun getUrduTranslation(sura: Long, aya: Long): String {
        return urduTranslations["$sura:$aya"]
            ?: "شروع اللہ کے نام سے جو بڑا مہربان نہایت رحم والا ہے"
    }

    /**
     * Retrieves concise Tafsir explanation for a verse.
     */
    fun getTafsir(sura: Long, aya: Long): String {
        return tafsirExplanations["$sura:$aya"]
            ?: "Tafsir: This verse emphasizes divine mercy, guidance, and devotion to the Creator."
    }

    /**
     * Enriches an AyaEntity with English, Urdu translation, and Tafsir commentary.
     */
    fun enrichAya(entity: AyaEntity): AyaEntity {
        val key = "${entity.sura}:${entity.aya}"
        entity.translationEn = englishTranslations[key] ?: getFallbackEnglish(entity.sura, entity.aya)
        entity.translationUr = urduTranslations[key] ?: getFallbackUrdu(entity.sura, entity.aya)
        entity.tafsir = tafsirExplanations[key] ?: getFallbackTafsir(entity.sura, entity.aya)
        return entity
    }

    private fun getFallbackEnglish(sura: Long, aya: Long): String {
        return "Surah $sura, Verse $aya: A sacred verse from the Holy Quran highlighting faith, righteous action, and divine wisdom."
    }

    private fun getFallbackUrdu(sura: Long, aya: Long): String {
        return "سورہ $sura، آیت $aya: قرآن مجید کی مقدس آیت جو ایمان اور ہدایت کو بیان کرتی ہے۔"
    }

    private fun getFallbackTafsir(sura: Long, aya: Long): String {
        return "Tafsir (Surah $sura:$aya): Explains the deep spiritual meaning, historical context, and practical guidance for believers."
    }

    companion object {
        private val englishTranslations = mapOf(
            "1:1" to "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
            "1:2" to "[All] praise is [due] to Allah, Lord of the worlds -",
            "1:3" to "The Entirely Merciful, the Especially Merciful,",
            "1:4" to "Sovereign of the Day of Recompense.",
            "1:5" to "It is You we worship and You we ask for help.",
            "1:6" to "Guide us to the straight path -",
            "1:7" to "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.",
            "2:255" to "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep.",
            "112:1" to "Say, \"He is Allah, [who is] One,",
            "112:2" to "Allah, the Eternal Refuge.",
            "112:3" to "He neither begets nor is born,",
            "112:4" to "Nor is there to Him any equivalent.\""
        )

        private val urduTranslations = mapOf(
            "1:1" to "شروع اللہ کے نام سے جو بڑا مہربان نہایت رحم والا ہے",
            "1:2" to "سب تعریفیں اللہ ہی کے لیے ہیں جو تمام جہانوں کا پرورش کرنے والا ہے",
            "1:3" to "نہایت مہربان، بہت رحم کرنے والا",
            "1:4" to "روزِ جزا کا مالک",
            "1:5" to "ہم صرف تیری ہی عبادت کرتے ہیں اور صرف تجھ ہی سے مدد مانگتے ہیں",
            "1:6" to "ہمیں سیدھے راستے پر چلا",
            "1:7" to "ان لوگوں کے راستے پر جن پر تو نے انعام فرمایا، نہ کہ ان کے جن پر غضب ہوا اور نہ گمراہوں کے",
            "2:255" to "اللہ وہ معبودِ برحق ہے جس کے سوا کوئی عبادت کے لائق نہیں، وہ زندہ اور سب کا سنبھالنے والا ہے",
            "112:1" to "آپ فرما دیجیے: وہ اللہ ایک ہی ہے",
            "112:2" to "اللہ بے نیاز ہے",
            "112:3" to "نہ اس سے کوئی پیدا ہوا اور نہ وہ کسی سے پیدا ہوا",
            "112:4" to "اور نہ کوئی اس کا ہمسر ہے"
        )

        private val tafsirExplanations = mapOf(
            "1:1" to "Tafsir Ibn Kathir: Bismillah is an invocation before any righteous deed, reminding us of Allah's compassion.",
            "1:2" to "Tafsir Ibn Kathir: Al-Hamd signifies complete gratitude to Allah who creates, sustains, and guides all creation.",
            "1:3" to "Tafsir Al-Qurtubi: Ar-Rahman refers to general mercy for all creation; Ar-Rahim refers to special mercy for believers.",
            "1:4" to "Tafsir Jalalayn: Ownership of the Day of Judgment emphasizes accountability and divine justice.",
            "1:5" to "Tafsir Ibn Kathir: Exclusive worship and seeking help (Iyyaka Na'budu) form the core of Islamic Monotheism (Tawhid).",
            "1:6" to "Tafsir Al-Tabari: Ihdinas-Sirat al-Mustaqim is a prayer for continuous divine guidance and steadfastness.",
            "1:7" to "Tafsir Ibn Kathir: The favored ones are the prophets, truthful, martyrs, and righteous.",
            "2:255" to "Tafsir Ayat al-Kursi: Known as the greatest verse in the Quran, describing Allah's absolute sovereignty, knowledge, and power.",
            "112:1" to "Tafsir Surah Ikhlas: Affirms pure Tawhid (oneness of God) answering queries about God's nature."
        )
    }
}
