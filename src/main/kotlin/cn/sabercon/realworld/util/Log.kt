package cn.sabercon.realworld.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

/**
 * Returns a logger named corresponding to the caller class.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun logger(): Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
