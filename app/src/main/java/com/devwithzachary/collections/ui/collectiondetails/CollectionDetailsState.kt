package com.devwithzachary.collections.ui.collectiondetails

data class CollectionDetailsState(
    val searchQuery: String = "",
    val sortType: SortType = SortType.ALPHABETICAL,
    val filterType: FilterType = FilterType.NONE
)

enum class SortType {
    ALPHABETICAL,
    ALPHABETICAL_REVERSE
}

enum class FilterType {
    NONE,
    HAVE,
    WANT
}
