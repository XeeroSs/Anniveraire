package com.xeross.anniveraire.model

data class User(val id: String = "", val email: String? = "",
                val userName: String? = "", val urlImage: String? = "",
                val discussionId: ArrayList<Int> = ArrayList())