package org.tartarus.snowball
import java.io.File
import kotlin.math.min

class BigWords(Words: ArrayList<String>) {
    private val wordCount = HashMap<String, Int>()
    private val maxLen = 10000;
    private val sortedArr = IntArray(maxLen)
    var BigWordArr = listOf("")
    private var count = 0
    private var sortWords = Array<String?>(1){it -> ""}
    private var countBig = 0;
    init {
        val bigWordArr = ArrayList<String>()
        for (word in Words) {
            if (wordCount.containsKey(word)) {
                wordCount[word] = wordCount[word]!! + 1
            } else {
                wordCount[word] = 1
            }
        }
        for (word in wordCount) {
            if (word.key.length >= maxLen) {
                bigWordArr.add(word.key)
                countBig++
            }
            else {
                sortedArr[word.key.length]++
                count++
            }
        }
        val startsArr = IntArray(maxLen)
        startsArr[0] = 0
        for (i in 1 until maxLen)
            startsArr[i] = sortedArr[i - 1] + startsArr[i - 1]
        val sortedWords = arrayOfNulls<String>(count)
        for (word in wordCount)
            if (word.key.length < maxLen)
                sortedWords[startsArr[word.key.length]++] = word.key
        sortWords = sortedWords
        BigWordArr = bigWordArr.sortedBy { it.length }
    }

    fun nextWord() : String{
        if (countBig != 0)
            return BigWordArr[--countBig]
        return sortWords[--count]!!
    }

    fun size() : Int{
        return count + countBig
    }

    fun count_in_text(s : String) : Int{
        return wordCount[s]!!
    }
}

fun rootCompare(str1: String, str2: String): Boolean {
    val d : Int
    val lenStr1 : Int = str1.length
    val lenStr2 : Int = str2.length
    val minLen : Int = min(lenStr1, lenStr2)

    when (minLen) {
        1 -> d = 1
        in 2..3 -> d = 2
        in 4..5 -> d = 3
        in 6..7 -> d = 4
        in 8..12 -> d = 5
        else -> d = 6
    }

    for (i in 0..(str1.length-d)) {
        val subStr = str1.substring(i, i+d)
        if (str2.contains(subStr)) {
            return true
        }
    }
    return false
}

fun LongNonSingleRoot(Words : ArrayList<String>,lang : String = "english", n : Int = 10) : ArrayList<Pair<String, Int>>{
    val big = BigWords(Words)
    val s = SpecifiedStemmer(lang)
    val result = ArrayList<Pair<String, Int>>()
    val resultStem = ArrayList<String>()
    while (result.size != n && big.size() != 0){
        val buf = big.nextWord()
        val bufStem = s.getStem(buf)
        var flag = true
        for (stem in resultStem)
            if (rootCompare(bufStem, stem)) {
                flag = false
                break
            }
        if (flag) {
            result.add(Pair(buf,big.count_in_text(buf)))
            resultStem.add(bufStem)
        }
    }
    return result
}

fun main(args: Array<String>) {
    val words = mutableSetOf<String>()
    File("java/org/tartarus/snowball/input.txt").forEachLine { line ->
        val lineWords = line.split("[\\p{Punct}\\s]+".toRegex())
        lineWords.forEach { word ->
            words.add(word.lowercase())
        }
    }

    val Arr = ArrayList<String>()
    for(i in words) Arr.add(i)

    for (i in LongNonSingleRoot(Arr, lang = "russian", n = 10))
        println(i.first + " " + i.first.length + " " + i.second)
}
