package com.xeross.anniveraire.model

data class User(val id: String = "", val email: String? = "",
                val userName: String? = "", val urlImage: String? = "",
                val discussionsId: ArrayList<String>? = null,
                val discussionsRequestId: ArrayList<String>? = null,
                val galleriesId: ArrayList<String>? = null,
                val galleriesRequestId: ArrayList<String>? = null)