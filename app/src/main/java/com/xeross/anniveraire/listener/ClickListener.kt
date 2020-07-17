package com.xeross.anniveraire.listener

interface ClickListener<T> {
    fun onClick(o: T)
    fun onLongClick(o: T)
}