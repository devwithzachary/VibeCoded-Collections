package com.devwithzachary.collections.data

data class CollectionsExport(
    val collections: List<CollectionWithItemsExport>
)

data class CollectionWithItemsExport(
    val name: String,
    val items: List<CollectionItemExport>
)

data class CollectionItemExport(
    val name: String,
    val description: String,
    val has: Boolean,
    val wants: Boolean,
    val imageUri: String?
)
