import kotlin.math.min
import java.io.File

class BigWords(Words: ArrayList<String>) {
    private val wordCount = HashMap<String, Int>()
    private val maxLen = 10000;
    private val sortedArr = IntArray(maxLen)
    private var BigWordArr = listOf("")
    private var count = 0
    private var sortWords = Array<String?>(1){it -> ""}
    private var countBig = 0
    private var countMin = 0
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

    fun nextMinWord() : String{
        if (countMin >= count)
            return BigWordArr[countMin++ - count]
        return sortWords[countMin++]!!
    }

    fun size() : Int{
        return count + countBig
    }

    fun canNextMin() : Boolean{
        return (count + countBig) - countMin > 0
    }

    fun count_in_text(s : String) : Int{
        return wordCount[s]!!
    }

    fun startMin(){
        countMin = 0
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
    val stemmer = Stem.SStem(lang)
    val result = ArrayList<Pair<String, Int>>()
    val resultStem = ArrayList<String>()
    while (result.size != n && big.size() != 0) {
        val buf = big.nextWord()
        val bufStem = if (buf.length > 4)  stemmer.stem(buf) else buf
        var flag = true
        val del = ArrayList<Int>()
        for (i in 0 until resultStem.size)
            if (rootCompare(bufStem, resultStem[i])) {
                if (!flag && bufStem.length < 6){
                    del.add(i)
                }
                flag = false
            }
        del.reverse()
        for (i in del){
            result.remove(result[i])
            resultStem.remove(resultStem[i])
        }
        if (flag) {
            result.add(Pair(buf, big.count_in_text(buf)))
            resultStem.add(bufStem)
        }
        var len = 0
        var cou = 0
        while (result.size == n && big.canNextMin() && len < 6 && cou < 1000){
            val min = stemmer.stem(big.nextMinWord())
            flag = true
            for (i in 0 until resultStem.size)
                if (rootCompare(min, resultStem[i])) {
                    if (!flag && min.length in 4..6){
                        del.add(i)
                    }
                    flag = false
                }
            del.reverse()
            for (i in del){
                result.remove(result[i])
                resultStem.remove(resultStem[i])
            }
            cou++
            len = min.length
        }
        big.startMin()
    }
    return result
}

fun main(args: Array<String>) {
    val Arr = ArrayList<String>()
    File("/Users/nikvoen/Projects/untitled3/input.txt").forEachLine { line ->
        val lineWords = line.split("[\\p{Punct}\\s]+".toRegex())
        lineWords.forEach { word ->
            Arr.add(word.lowercase())
        }
    }

    for (i in LongNonSingleRoot(Arr, lang = "russian", n = 10))
        println(i.first + " " + i.first.length + " " + i.second)
}