package com.nurbk.ps.projectm.others


const val NAME_FILE_PREF = "PreferencesManagerProject"

const val IS_SIGN_IN = "signIn"
const val COLLECTION_USERS = "User"

const val USER_DATA_PROFILE = "userProfile"

const val USER_DATA = "userData"
const val TYPE_CALL = "typeCall"

const val CALL_VIDEO = "video"
const val CALL_AUDIO = "audio"

const val REMOTE_MSG_AUTHORIZATION = "Authorization"
const val AUTH_VALUE =
    "AAAAZblCXUk:APA91bH4OaKITLI4Rd9OwXzvYzWdbOtNlImItjRziNm9-VYLbU-atWBR3cNIsG5PuwqAdGYchLtB_94TDoO8Z1-loZRKIwakjDOUTLDebsJ5Ug5RBCzNEvl7E8s6CYyQUamDq1qQYoAN"

const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
const val VALUE_TYPE = "application/json"


const val REMOTE_MSG_TYPE = "type"
const val REMOTE_MSG_INVITATION = "invitation"
const val REMOTE_MSG_MEETING_TYPE = "meetingType"
const val REMOTE_MSG_INVITER_TOKEN="inviterToken"
const val REMOTE_MSG_DATA="data"
const val REMOTE_MSG_REGISTRATION_IDS="registration_ids"

fun mapRemoteHeaders() = HashMap<String, String>().apply {
    put(REMOTE_MSG_AUTHORIZATION, AUTH_VALUE)
    put(REMOTE_MSG_CONTENT_TYPE, VALUE_TYPE)
}