package com.software.cygnvs_code.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import retrofit2.Response

class LocalizationRepo(private val api: LocalizationAPI , private val ioDispatcher: CoroutineDispatcher) {

    suspend fun downloadFile(fileName: String):
            Flow<Response<ResponseBody>> {
        return flow {
            val response = api.downloadFile(fileName)
            emit(response)
        }.flowOn(ioDispatcher)
    }

}