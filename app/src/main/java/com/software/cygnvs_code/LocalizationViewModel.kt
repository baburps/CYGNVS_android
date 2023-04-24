package com.software.cygnvs_code

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.software.cygnvs_code.app.LocalizationApplication
import com.software.cygnvs_code.domain.LocalizationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import javax.inject.Inject


@HiltViewModel
class LocalizationViewModel @Inject constructor(
    private val localizationHelper: LocalizationHelper ,
    private val repo: LocalizationRepo
) : AndroidViewModel(LocalizationApplication()) {

    val po = ObservableField<LocalizationPO>()

    companion object {
        private const val TAG = "LocalizationViewModel"
    }

    fun initializePO() {
        po.set(LocalizationPO(inputText = "GREETINGS" , title = ""))
    }

    fun downloadLocalizationFile(path: String , fileName: String) {
        Log.d(TAG , "downloadLocalizationFile")
        viewModelScope.launch {
            repo.downloadFile(fileName).collect {
                saveFile(it.body() , path)
            }
        }
    }

    private fun saveFile(body: ResponseBody? , path: String) {

        if (body == null) {
            Log.d(TAG , "body == null")
            return
        }
        var input: InputStream? = null
        try {
            input = body.byteStream()
            val fos = FileOutputStream(path)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer , 0 , read)
                }
                output.flush()
            }
            Log.d(TAG , "saveFile--Success")

            //Read JSON file & update in localization helper
            readJSOnFile(path)
            return
        } catch (e: Exception) {
            Log.e(TAG , "saveFile" , e)
        } finally {
            input?.close()
        }
        return
    }

    fun readJSOnFile(path: String) {
        try {
            val file = File(path)
            if (file.exists()) {
                val stream = FileInputStream(file)
                var jsonStr: String? = null
                try {
                    val fc: FileChannel = stream.channel
                    val bb = fc.map(FileChannel.MapMode.READ_ONLY , 0 , fc.size())
                    jsonStr = Charset.defaultCharset().decode(bb).toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    stream.close()
                }

                jsonStr?.let {
                    val jsonObject = JSONObject(jsonStr)
                    localizationHelper.setLocalizationJSON(jsonObject)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG , "readJSOnFile" , e)
        }
    }

    fun onSubmitClick() {
        Log.d(TAG , "onSubmitClick")
        po.get()?.let { value ->
            Log.d(TAG , "onSubmitClick--${value.inputText}")

            val localizationValue = localizationHelper.getString(value.inputText)
            Log.d(TAG , "onSubmitClick--$localizationValue")
            po.set(LocalizationPO(value.inputText , localizationValue))
        }
    }

}