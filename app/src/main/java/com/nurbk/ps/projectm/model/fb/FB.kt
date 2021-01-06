package com.nurbk.ps.projectm.model.fb


import com.google.gson.annotations.SerializedName

data class FB(
    @SerializedName("birthday")
    var birthday: String,
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("last_name")
    var lastName: String,
    @SerializedName("link")
    var link: String,
    @SerializedName("locale")
    var locale: String,
    @SerializedName("location")
    var location: Location,
    @SerializedName("name")
    var name: String,
    @SerializedName("timezone")
    var timezone: Int,
    @SerializedName("updated_time")
    var updatedTime: String,
    @SerializedName("verified")
    var verified: Boolean
)