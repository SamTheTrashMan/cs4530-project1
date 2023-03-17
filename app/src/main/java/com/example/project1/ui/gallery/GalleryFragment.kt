package com.example.project1.ui.gallery

import android.R
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project1.databinding.FragmentGalleryBinding
import com.example.project1.databinding.FragmentHomeBinding
import com.example.project1.ui.home.HomeViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GalleryFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentGalleryBinding? = null
    private var picturePath: String? = ""
    private var picture: Bitmap? = null

    private var cameraButton: Button? = null
    private var buttonSignUp: Button? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val intent = requireActivity().intent

        val sex = intent.getStringExtra("sex")
        val activityLevel = intent.getStringExtra("activityLevel")
        val age = intent.getStringExtra("age")!!.toIntOrNull()
        val height = intent.getStringExtra("height")!!.toIntOrNull()
        val weight = intent.getStringExtra("weight")!!.toIntOrNull()
        val name = intent.getStringExtra("fullName")
        val cityCountry = intent.getStringExtra("cityCountry")

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        buttonSignUp = binding.buttonSignUp
        buttonSignUp!!.setOnClickListener(this)

        cameraButton = binding.buttonPicture
        cameraButton!!.setOnClickListener(this)
        val entries = ArrayList<String>()
        entries.add("Select Age")
        for (i in 1..65) {
            entries.add(i.toString())
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_spinner_item, entries
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAge!!.adapter = adapter

        // Height
        val heightEntries = ArrayList<String>()
        heightEntries.add("Select Height")
        for (i in 1..113) {
            heightEntries.add(i.toString())
        }
        val heightAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_spinner_item,
            heightEntries
        )
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHeight!!.adapter = heightAdapter

        // Weight
        val weightEntries = ArrayList<String>()
        weightEntries.add("Select Weight")
        for (i in 1..500) {
            weightEntries.add(i.toString())
        }
        val weightAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_spinner_item,
            weightEntries
        )
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        if(weightAdapter != null) {
            binding.spinnerWeight!!.adapter = weightAdapter
        }
        if(cityCountry != null) {
            binding.editTextCityCountry.setText(cityCountry)
        }
            binding.editTextFullName.setText(name)

        if(age != null) {
            binding.spinnerAge.setSelection(age!!)
        }
        if(height != null) {
            binding.spinnerHeight.setSelection(height!!)
        }
        if(weight != null) {
            binding.spinnerWeight.setSelection(weight!!)
        }
        if(activityLevel != null) {
            binding.spinnerActivity.setSelection(calculateActivityIndex(activityLevel!!))
        }
        if(sex != null) {
            binding.spinnerSex.setSelection(calculateSexIndex(sex!!))
        }


        return root
    }

    private fun calculateSexIndex(sex: String): Int {
        if(sex == "Select Sex"){
            return 0
        }
        if(sex == "Male"){
            return 1
        }
        return 2
    }

    override fun onClick(view: View) {
        when (view.id) {
            com.example.project1.R.id.buttonPicture -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    cameraActivity.launch(cameraIntent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), ex.toString(), Toast.LENGTH_LONG).show()
                }
            }
            com.example.project1.R.id.buttonSignUp -> {
                val fullName = binding.editTextFullName!!.text.toString()
                val cityCountry = binding.editTextCityCountry!!.text.toString()
                val activityLevel = binding.spinnerActivity!!.selectedItem.toString()
                val sex = binding.spinnerSex!!.selectedItem.toString()
                val weight = binding.spinnerWeight!!.selectedItem.toString()
                val height = binding.spinnerHeight!!.selectedItem.toString()
                val age = binding.spinnerAge!!.selectedItem.toString()
                if (fullName.isNullOrBlank()) {
                    Toast.makeText(requireContext(),"Please fill fill in the name field",Toast.LENGTH_SHORT).show()
                } else {
                    requireActivity().intent.putExtra("picturePath", picturePath)
                    requireActivity().intent.putExtra("fullName", fullName)
                    requireActivity().intent.putExtra("cityCountry", cityCountry)
                    requireActivity().intent.putExtra("activityLevel", activityLevel)
                    requireActivity().intent.putExtra("sex", sex)
                    requireActivity().intent.putExtra("weight", weight)
                    requireActivity().intent.putExtra("height", height)
                    requireActivity().intent.putExtra("age", age)
                    Toast.makeText(requireContext(),"In the else",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val myDir = File("${requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/saved_images")
        myDir.mkdirs()
        val file = File(myDir, "Thumbnail_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg")
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(requireContext(), "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == AppCompatActivity.RESULT_OK) {
            val extras = result.data!!.extras
            picture = extras!!["data"] as Bitmap?
            if (isExternalStorageWritable) {
                picturePath = saveImage(picture)
                requireActivity().intent.putExtra("picturePath", picturePath)
            } else {
                Toast.makeText(requireContext(), "External storage not writable.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun calculateActivityIndex(activityLevel:String): Int {
        if(activityLevel == "Select Activity Level"){
            return 0
        }
        if(activityLevel == "Sedentary"){
            return 1
        }
        if(activityLevel == "Light Exercise"){
            return 2
        }
        if(activityLevel == "Moderate Exercise"){
            return 3
        }
        if(activityLevel == "Heavy Exercise"){
            return 4
        }
       return 5
    }
}