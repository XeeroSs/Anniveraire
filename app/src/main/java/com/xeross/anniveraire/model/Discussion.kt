package com.xeross.anniveraire.model

import java.util.*
import kotlin.collections.ArrayList

data class Discussion(val id: String = UUID.randomUUID().toString(), val ownerId: String = "", val name: String = "", val usersId: ArrayList<String> = ArrayList())