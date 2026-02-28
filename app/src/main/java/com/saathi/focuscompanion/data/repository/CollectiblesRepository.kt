package com.saathi.focuscompanion.data.repository

import com.saathi.focuscompanion.data.model.CollectibleItem
import com.saathi.focuscompanion.data.model.CollectibleType
import com.saathi.focuscompanion.data.model.StudySession
import com.saathi.focuscompanion.data.model.UserProfile

class CollectiblesRepository {

    private val unlockedIds = mutableSetOf<String>()

    private val allCollectibles = listOf(
        CollectibleItem("pencil_box", "Pencil Box", "1 session complete karo", type = CollectibleType.DESK_ITEM),
        CollectibleItem("cactus_plant", "Cactus Plant", "5 sessions complete karo", type = CollectibleType.DESK_ITEM),
        CollectibleItem("sticky_note", "Sticky Notes", "10 sessions complete karo", type = CollectibleType.DESK_ITEM),
        CollectibleItem("new_lamp", "Study Lamp", "20 sessions complete karo", type = CollectibleType.DESK_ITEM),
        CollectibleItem("trophy", "Trophy", "50 sessions complete karo", type = CollectibleType.DESK_ITEM),
        CollectibleItem("masala_chai", "Masala Chai", "3 din ka streak banao", type = CollectibleType.CHAI_VARIANT),
        CollectibleItem("adrak_chai", "Adrak Chai", "7 din ka streak banao", type = CollectibleType.CHAI_VARIANT),
        CollectibleItem("special_chai", "Special Chai", "14 din ka streak banao", type = CollectibleType.CHAI_VARIANT),
        CollectibleItem("filter_coffee", "Filter Coffee", "14 din ka streak (South India)", type = CollectibleType.CHAI_VARIANT),
        CollectibleItem("topper_chai", "Topper Chai", "30 din ka streak banao", type = CollectibleType.CHAI_VARIANT),
        CollectibleItem("monsoon_room", "Monsoon Room", "25 sessions complete karo", type = CollectibleType.ROOM_DECOR),
        CollectibleItem("winter_room", "Winter Room", "50 sessions complete karo", type = CollectibleType.ROOM_DECOR),
        CollectibleItem("night_room", "Night Room", "100 sessions complete karo", type = CollectibleType.ROOM_DECOR),
    )

    fun getCollectibles(): List<CollectibleItem> {
        return allCollectibles.map { it.copy(isUnlocked = it.id in unlockedIds) }
    }

    fun checkAndUnlockCollectibles(profile: UserProfile, session: StudySession) {
        val sessions = profile.totalSessionsCompleted

        if (sessions >= 1) unlock("pencil_box")
        if (sessions >= 5) unlock("cactus_plant")
        if (sessions >= 10) unlock("sticky_note")
        if (sessions >= 20) unlock("new_lamp")
        if (sessions >= 50) unlock("trophy")
        if (sessions >= 25) unlock("monsoon_room")
        if (sessions >= 50) unlock("winter_room")
        if (sessions >= 100) unlock("night_room")

        when (profile.currentStreakDays) {
            in 3..6 -> unlock("masala_chai")
            in 7..13 -> unlock("adrak_chai")
            in 14..29 -> {
                if (isSouthIndianCity(profile.city)) unlock("filter_coffee")
                else unlock("special_chai")
            }
            in 30..Int.MAX_VALUE -> unlock("topper_chai")
        }
    }

    private fun unlock(id: String) {
        unlockedIds.add(id)
    }

    private fun isSouthIndianCity(city: String): Boolean {
        val southCities = setOf(
            "Chennai", "Bangalore", "Hyderabad", "Kochi",
            "Thiruvananthapuram", "Kozhikode", "Mangalore",
            "Coimbatore", "Madurai", "Vijayawada", "Visakhapatnam",
            "Puducherry"
        )
        return southCities.any { it.equals(city, ignoreCase = true) }
    }
}
