package com.xeross.anniveraire.model

data class User(val id: String = "", val email: String? = "",
                val userName: String? = "", val urlImage: String? = "",
                val discussionsId: ArrayList<String> = ArrayList(),
                val discussionsRequestId: ArrayList<String> = ArrayList(),
                val galleriesId: ArrayList<String> = ArrayList(),
                val galleriesRequestId: ArrayList<String> = ArrayList())