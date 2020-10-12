package com.xeross.anniveraire.listener

import androidx.appcompat.widget.SearchView

interface ToolBarListener {
    fun onSearch(searchView: SearchView)
    fun onAdd()
}