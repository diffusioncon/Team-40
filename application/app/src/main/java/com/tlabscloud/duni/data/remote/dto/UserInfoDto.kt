package com.tlabscloud.duni.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserInfoDto(
    val email: String,
    @SerializedName("family_name")
    val familyName: String,
    @SerializedName("given_name")
    val givenName: String,
    val name: String,
    val sub: String

)
