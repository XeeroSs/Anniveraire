package com.xeross.anniveraire.listener

import androidx.appcompat.widget.SearchView

interface ToolBarListener {
    fun onRequest()
    fun onSearch(searchView: SearchView)
    fun onAdd()
}