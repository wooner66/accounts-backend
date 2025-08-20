package com.autoever.accounts.common.extension

fun String.getTopLevelByAddress(): String {
    return this.trim().split(Regex("\\s+")).firstOrNull() ?: ""
}
