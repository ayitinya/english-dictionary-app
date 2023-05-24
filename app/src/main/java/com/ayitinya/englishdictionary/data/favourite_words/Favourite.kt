package com.ayitinya.englishdictionary.data.favourite_words

import com.ayitinya.englishdictionary.data.favourite_words.source.local.LocalFavourite

data class Favourite(
    val word: String,
)

fun Favourite.toLocal(): LocalFavourite {
    return LocalFavourite(id = null, word = word)
}

fun LocalFavourite.toExternal(): Favourite {
    return Favourite(word)
}

fun List<Favourite>.toLocal(): List<LocalFavourite> {
    return map { it.toLocal() }
}

fun List<LocalFavourite>.toExternal(): List<Favourite> {
    return map { it.toExternal() }
}