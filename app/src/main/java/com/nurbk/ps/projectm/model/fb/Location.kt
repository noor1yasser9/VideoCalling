package com.nurbk.ps.projectm.model.fb


import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("id")
    var id: String,
    @SerializedName("name")
    var name: String
)