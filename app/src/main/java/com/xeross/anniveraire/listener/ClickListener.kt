package com.xeross.anniveraire.listener

interface ClickListener<M> {
    fun onClick(o: M)
    fun onLongClick(o: M)
}