package com.w10group.hertzdictionary.bean

import com.google.gson.annotations.SerializedName

data class AlternativeTranslations(@SerializedName("alternative") val words: List<Alternative>? = null)