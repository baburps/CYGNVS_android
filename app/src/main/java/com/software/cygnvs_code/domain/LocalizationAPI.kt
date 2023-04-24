package com.software.cygnvs_code.domain

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface LocalizationAPI {

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileName:String): Response<ResponseBody>
}