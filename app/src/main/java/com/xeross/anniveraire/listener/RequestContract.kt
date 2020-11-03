package com.xeross.anniveraire.listener

interface RequestContract {
    interface View<V> {
        fun setList()
        fun getObjectsFromUser(tObjects: ArrayList<V>)
        fun getRequests()
    }

    interface Presenter<P> {
        fun getObjectsFromUser(userId: String)
        fun removeObjectRequest(tObject: P, userId: String)
        fun joinObject(tObject: P, userId: String)
    }
}