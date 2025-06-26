package com.moare.android.core.util

class TrieNode(
    var isEndOfWord: Boolean = false,
    val children: MutableMap<Char, TrieNode> = mutableMapOf(),
    val originalWords: MutableMap<String, Int> = mutableMapOf()
)

class Trie {
    private val root = TrieNode()

    // 단어 추가
    fun insert(word: String, originalWord: String? = null, weight: Int = 0) {
        val displayWord = originalWord ?: word

        var node = root

        for (char in word.lowercase()) {
            if (!node.children.containsKey(char)) {
                node.children[char] = TrieNode()
            }
            node = node.children[char]!!
        }

        node.isEndOfWord = true
        node.originalWords[displayWord] = weight
    }

    // 검색
    fun search(prefix: String): List<String> {
//        var node = root
//
//        for (char in prefix) {
//            node = node.children[char] ?: return emptyList()
//        }
//
//        return collectWords(node, prefix)

        fun findNode(prefix: String): TrieNode? {
            var node = root
            for (char in prefix) {
                val next = node.children[char] ?: return null
                node = next
            }
            return node
        }

        val loweredPrefix = prefix.lowercase()
        val exactNode = findNode(loweredPrefix)

        val exactMatches = mutableMapOf<String, Int>()
        val fuzzyMatches = mutableMapOf<String, Int>()

        if (exactNode != null) {
            collectWordsSeparated(
                node = exactNode,
                exactMatches = exactMatches,
                fuzzyMatches = fuzzyMatches,
                prefix = loweredPrefix
            )
        }

        if (isKorean(prefix)) {
            val chosung = getChosung(loweredPrefix)
            val fuzzyNode = findNode(chosung)
            if (fuzzyNode != null) {
                collectWordsSeparated(
                    node = fuzzyNode,
                    exactMatches = exactMatches,
                    fuzzyMatches = fuzzyMatches,
                    prefix = chosung
                )
            }
        }

        val sortedExact = exactMatches.entries.sortedWith(
            compareByDescending<Map.Entry<String, Int>> { it.value }
                .thenBy { it.key }
        )
        val sortedFuzzy = fuzzyMatches.entries.sortedWith(
            compareByDescending<Map.Entry<String, Int>> { it.value }
                .thenBy { it.key }
        )

        val combinedResults = if (exactMatches.isNotEmpty()) sortedExact else sortedFuzzy

        // 중복 제거 + 상위 10개
        val seenWords = mutableSetOf<String>()
        val finalResults = mutableListOf<String>()

        for ((word, _) in combinedResults) {
            if (seenWords.add(word)) {
                finalResults.add(word)
                if (finalResults.size >= 10) break
            }
        }

        return finalResults
    }

    private fun collectWordsSeparated(
        node: TrieNode,
        exactMatches: MutableMap<String, Int>,
        fuzzyMatches: MutableMap<String, Int>,
        prefix: String
    ) {
        if (node.isEndOfWord) {
            for ((word, weight) in node.originalWords) {
                if (word.startsWith(prefix)) {
                    exactMatches[word] = weight
                } else {
                    fuzzyMatches[word] = weight
                }
            }
        }

        for ((_, childNode) in node.children) {
            collectWordsSeparated(childNode, exactMatches, fuzzyMatches, prefix)
        }
    }

    // 모든 단어 수집
    private fun collectWords(node: TrieNode, prefix: String): List<String> {
//        val result = mutableSetOf<String>()
        val result = mutableMapOf<String, Int>()

//        if (node.isEndOfWord) result.addAll(node.originalWords)
        if (node.isEndOfWord) {
            for ((word, weight) in node.originalWords) {
                result[word] = weight
            }
        }

//        for ((char, child) in node.children) {
//            result.addAll(collectWords(child, prefix + char))
//        }
        for ((char, childNode) in node.children) {
            val newPrefix = prefix + char
            val childWords = collectWords(childNode, newPrefix)
            for (word in childWords) {
                val weight = childNode.originalWords[word]
                if (weight != null) {
                    result[word] = weight
                }
            }
        }

//        return result.toList()
        return result.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> {it.value}.thenBy { it.key })
            .take(10)
            .map { it.key }
    }

    private fun isKorean(string: String): Boolean {
        for (char in string) {
            val value = char.code
            if ((value in 0xAC00..0xD7A3) || (value in 0x3131..0x318E) || (value in 0x1100..0x11FF)) {
                return true
            }
        }
        return false
    }
}