package cn.sabercon.realworld.util

import com.github.slugify.Slugify
import java.util.*

val SLUGIFY: Slugify = Slugify.builder().build()

fun String.slugify(randomSuffix: Boolean = false): String {
    val slug = SLUGIFY.slugify(this)
    return if (randomSuffix) "$slug-${randomKey()}" else slug
}

private fun randomKey(): String = UUID.randomUUID().toString().substring(0, 8)
