package com.software.cygnvs_code

import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject

class LocalizationHelper {

    private var localizationMap: Map<String , String> = HashMap()
    private val regex = Regex("\\$\\{\\s*([a-zA-Z0-9_]+)\\s*\\}")

    fun setLocalizationJSON(localizationJSONObject: JSONObject) {
        localizationMap = Gson().fromJson(localizationJSONObject.toString() , localizationMap.javaClass)
    }

    /**
     * To get value from JSON. If value is not present then return the key as value
     */
    fun getString(key: String): String {
        Log.d("LocalizationHelper" , key)

        //Get Value for key
        var value = getLocalizationValue(key)
        do {
            //Check if the returned value contains interpolation expression
            val listOfKeys = regex.findAll(value).map { it.groupValues[1] }.toList()
            Log.d("interpolation" , listOfKeys.toString())
            if (listOfKeys.isNotEmpty()) {
                //Replace interpolation expression keys with value form localization json
                value = regex.replace(value) { matchResult ->
                    val expressionKey = matchResult.groupValues[1]
                    localizationMap[expressionKey]?.toString() ?: matchResult.value
                }

                Log.d("output" , value)
            }
        } while (checkForInterpolationExpressions(value))

        Log.d("value" , value)
        return value
    }

    /**
     * To get value for keys. Return key as value if value is not available for that key
     */
    private fun getLocalizationValue(key: String): String {
        val value: String? = localizationMap[key]
        value?.let {
            return value
        }
        return key
    }

    /**
     * To check if a string contains interpolation expression keys
     */
    private fun checkForInterpolationExpressions(value: String): Boolean {
        //Get list of interpolation expression keys
        val listOfKeys = regex.findAll(value).map { it.groupValues[1] }.toList()
        Log.d("interpolation" , listOfKeys.toString())
        if (listOfKeys.isNotEmpty()) {
            return true
        }
        return false
    }
}