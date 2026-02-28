package com.saathi.focuscompanion.data.model

enum class CollectibleType { DESK_ITEM, BOOK, ROOM_DECOR, CHAI_VARIANT }

data class CollectibleItem(
    val id: String,
    val name: String,
    val description: String,
    val unlockedAt: Long? = null,
    val isUnlocked: Boolean = false,
    val type: CollectibleType
)
