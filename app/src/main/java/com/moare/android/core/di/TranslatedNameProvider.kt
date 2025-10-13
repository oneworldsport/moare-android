package com.moare.android.core.di

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslatedNameProvider @Inject constructor() {
    private val dictionaryMap: MutableMap<String, Map<String, String>> = mutableMapOf()

    fun setDictionary(category: String, nameMap: Map<String, String>) {
        dictionaryMap[category.lowercase()] = nameMap
    }

    fun getDictionary(category: String): Map<String, String> {
        return dictionaryMap[category.lowercase()] ?: emptyMap()
    }

    fun getName(category: String, name: String): String {
        val map = dictionaryMap[category.lowercase()] ?: emptyMap()
        return map[name.lowercase()] ?: name
    }
}