package com.xeross.anniveraire.model

import java.util.*
import kotlin.collections.ArrayList

data class Gallery(val id: String = UUID.randomUUID().toString(), val ownerId: String = "", val name: String = "",
                   val usersId: ArrayList<String> = ArrayList(),
                   val imagesId: ArrayList<String> = ArrayList(), val activityDate: Date)