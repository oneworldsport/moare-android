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
        var node = root

        for (char in word) {
            if (!node.children.containsKey(char)) {
                node.children[char] = TrieNode()
            }
            node = node.children[char]!!
            originalWord?.let {
                node.originalWords[it] = weight
            }
//            node = node.children.getOrPut(char) { TrieNode() }
//            if (originalWord != null) {
//                node.originalWords.add(originalWord)
//            }
        }

        node.isEndOfWord = true
        if (originalWord == null) {
            node.originalWords[word] = weight
//            node.originalWords.add(word)
        }
    }

    // 검색
    fun search(prefix: String): List<String> {
        var node = root

        for (char in prefix) {
            node = node.children[char] ?: return emptyList()
        }

        return collectWords(node, prefix)
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
}