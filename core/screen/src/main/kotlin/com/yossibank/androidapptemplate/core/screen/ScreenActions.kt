package com.yossibank.androidapptemplate.core.screen

data class ScreenActions(
    val reload: () -> Unit = {},
    val refresh: () -> Unit = {},
    val loadMore: () -> Unit = {},
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
)
