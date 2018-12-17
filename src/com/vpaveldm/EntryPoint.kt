package com.vpaveldm

import java.io.File
import java.io.SequenceInputStream
import java.util.*
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

fun main(args: Array<String>) {

    val vowels = listOf("а", "и", "о", "у", "ы", "э", "е", "ё", "ю", "я")
    val soundMap = mapOf(
            " " to "src/sounds/wav30.wav",
            "а" to "src/sounds/а.wav",
            "б" to "src/sounds/б.wav",
            "б'" to "src/sounds/бь.wav",
            "в" to "src/sounds/в.wav",
            "в'" to "src/sounds/вь.wav",
            "г" to "src/sounds/г.wav",
            "г'" to "src/sounds/гь.wav",
            "д" to "src/sounds/д.wav",
            "д'" to "src/sounds/дь.wav",
            "ж" to "src/sounds/ж.wav",
            "з" to "src/sounds/з.wav",
            "з'" to "src/sounds/зь.wav",
            "и" to "src/sounds/и.wav",
            "й" to "src/sounds/й.wav",
            "к" to "src/sounds/к.wav",
            "к'" to "src/sounds/кь.wav",
            "л" to "src/sounds/л.wav",
            "л'" to "src/sounds/ль.wav",
            "м" to "src/sounds/м.wav",
            "м'" to "src/sounds/мь.wav",
            "н" to "src/sounds/н.wav",
            "н'" to "src/sounds/нь.wav",
            "о" to "src/sounds/о.wav",
            "п" to "src/sounds/п.wav",
            "п'" to "src/sounds/пь.wav",
            "р" to "src/sounds/р.wav",
            "р'" to "src/sounds/рь.wav",
            "с" to "src/sounds/с.wav",
            "с'" to "src/sounds/сь.wav",
            "т" to "src/sounds/т.wav",
            "т'" to "src/sounds/ть.wav",
            "у" to "src/sounds/у.wav",
            "ф" to "src/sounds/ф.wav",
            "ф'" to "src/sounds/фь.wav",
            "х" to "src/sounds/х.wav",
            "х'" to "src/sounds/хь.wav",
            "ц" to "src/sounds/ц.wav",
            "ч" to "src/sounds/ч.wav",
            "ш" to "src/sounds/ш.wav",
            "щ" to "src/sounds/щ.wav",
            "ы" to "src/sounds/ы.wav",
            "э" to "src/sounds/э.wav"
    )
    val softConsonants = listOf("б", "в", "г", "д", "з", "к", "л", "м", "н", "п", "р", "с", "т", "ф", "х", "ч", "щ")
    val doubleSound = hashMapOf("е" to "йэ", "ё" to "йо", "ю" to "йу", "я" to "йа")

    val scanner = Scanner(System.`in`)
    val input = scanner.nextLine()

    val letters = input.map { it }
    println(letters)

    var phonetics = input.map { it.toString() }.toMutableList()

    //Rule 1

    doubleSound[phonetics.first()]?.let {
        phonetics.removeAt(0)
        phonetics.add(0, it)
    }

    phonetics = phonetics
            .joinToString(separator = "")
            //rule 2
            .replace("жи", "жы")
            .replace("ши", "шы")
            //rule 3
            .replace("сс", "с")
            .replace("сш", "ш")
            //rule 4
            .replace("тс", "ц")
            .replace("тьс", "ц")
            .replace("ь ", " ")
            .map { it.toString() }
            .toMutableList()

    var i = 0
    while (i < phonetics.size) {
        val letter = phonetics[i]
        if (doubleSound.containsKey(letter) && (phonetics[i - 1] == "ь" || phonetics[i - 1] == "ъ")) {
            phonetics[i - 1] = "й"
            phonetics[i] = doubleSound[letter]!![1].toString()
        } else if (doubleSound.containsKey(letter) && vowels.contains(phonetics[i - 1])) {
            phonetics[i] = doubleSound[letter]!![0].toString()
            phonetics.add(i + 1, doubleSound[letter]!![1].toString())
            i += 1
        } else if (doubleSound.containsKey(letter)) {
            phonetics.add(i, doubleSound[letter]!![1].toString())
            phonetics.removeAt(i + 1)
            if (softConsonants.contains(phonetics[i - 1])) {
                phonetics[i - 1] += "'"
            }
        } else if (i > 0 && phonetics[i - 1] == "ь" && softConsonants.contains(letter)) {
            phonetics.removeAt(i - 1)
            i -= 1
            phonetics[i] += "'"
        }
        i += 1
    }

    println(phonetics)

    val sounds = phonetics.map {
        soundMap[it]!!
    }

    var audioFormat: AudioFormat? = null
    val audioInputStreams = arrayListOf<AudioInputStream>()
    var frameLength = 0L
    sounds.forEach {
        val audioStream = AudioSystem.getAudioInputStream(File(it))
        audioFormat = audioStream?.format
        audioInputStreams.add(audioStream)
        frameLength += audioStream.frameLength
    }

    val duration = (frameLength + 0.0) / (audioFormat?.frameRate ?: 1f)
    print("duration: $duration")
    val appendedFiles = AudioInputStream(
            SequenceInputStream(
                    Collections.enumeration<AudioInputStream>(audioInputStreams)), audioFormat, frameLength)
    val clip = AudioSystem.getClip()
    clip.open(appendedFiles)
    clip.start()
    scanner.nextLine()
}