package com.manta.advice

object APIKeyStore {

    init {
        System.loadLibrary("native-lib")
    }

    external fun getNaverClientID(): String
    external fun getNaverSecret(): String


}