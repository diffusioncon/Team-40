package com.tlabscloud.duni.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class User(
    var accessToken: String = "",
    @PrimaryKey var refreshToken: String = "",
    var name: String = "",
    var givenName: String = "",
    var familyName: String = "",
    var email: String = "",
    var sub: String = "",
    var lastLogin: Date = Date()
)
