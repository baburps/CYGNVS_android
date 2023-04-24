package com.software.cygnvs_code

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.software.cygnvs_code.databinding.FragmentFirstBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class LocalizationFragment : Fragment() {

    companion object {
        private const val TAG = "LocalizationFragment"
        private const val FILE_NAME = "localisation.json"
    }


    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater , container , false)
        return binding.root

    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val viewModel: LocalizationViewModel = ViewModelProvider(this)[LocalizationViewModel::class.java]
        _binding?.viewModel = viewModel
        viewModel.initializePO()

        val path = requireContext().filesDir.toString() + FILE_NAME

        val file = File(path)
        if (file.exists()) {
            Log.d(TAG , "file already exists")
            //Skip downloading file if it already exist
            //Read JSON file & update in localization helper
            viewModel.readJSOnFile(path)
        } else {
            //Download Localization file
            viewModel.downloadLocalizationFile(path , FILE_NAME)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}