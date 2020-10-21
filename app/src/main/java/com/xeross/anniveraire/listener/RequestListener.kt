package com.xeross.anniveraire.listener

interface RequestListener<T> {
    fun join(dObject: T)
    fun deny(dObject: T)
}