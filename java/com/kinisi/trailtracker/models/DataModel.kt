package com.kinisi.trailtracker.models

import com.google.gson.annotations.SerializedName

data class DataModel(val id: String, val title: String, val type: String, val dist: String)

data class SearchModel(val id: String, val title: String, val type: String, val xCoord: String, val yCoord: String)

data class ResultsModel(val res: String)


