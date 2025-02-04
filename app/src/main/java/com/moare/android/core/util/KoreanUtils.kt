package com.moare.android.core.util

private val CHO = arrayOf(
    'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
)

fun getChosung(text: String): String {
    val result = StringBuilder()
    for (char in text) {
        if (char in '가'..'힣') {
            val unicode = char.code - 0xAC00
            val cho = unicode / (21 * 28)
            result.append(CHO[cho])
        } else {
            result.append(char) // 한글이 아니면 그대로 추가
        }
    }
    return result.toString()
}