package com.xeross.anniveraire.utils

import com.xeross.anniveraire.model.Discussion
import com.xeross.anniveraire.model.Gallery

// Sort by date
fun ArrayList<Gallery>.sortGalleriesByDate() {
    sortWith(Comparator { g1, g2 -> g1.activityDate.compareTo(g2.activityDate) })
    reverse()
}

// Sort by date
fun ArrayList<Discussion>.sortDiscussionsByDate() {
    sortWith(Comparator { g1, g2 -> g1.activityDate.compareTo(g2.activityDate) })
    reverse()
}