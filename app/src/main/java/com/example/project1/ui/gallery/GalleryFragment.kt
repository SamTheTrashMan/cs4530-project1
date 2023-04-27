package com.example.project1.ui.gallery

import android.R
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.project1.AppApplication
import com.example.project1.AppViewModel
import com.example.project1.AppViewModelFactory
import com.example.project1.UserTable
import com.example.project1.databinding.FragmentGalleryBinding
import com.example.project1.databinding.FragmentHomeBinding
import com.example.project1.ui.home.HomeViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import androidx.lifecycle.Observer




class GalleryFragment : Fragment(), View.OnClickListener {
    private var fullName: String? = null
    private var cityCountry: String? = ""
    private var weight: Int ? = 0
    private var height: Int?= 0
    private var age: Int?= 0
    private var activityLevel: String? = ""
    private var sex: String? = ""
    private var fullNameEt: EditText? = null
    private var ageEt: Spinner? = null
    private var cityCountryEt: EditText? = null
    private var weightEt: Spinner? = null
    private var heightEt: Spinner? = null
    private var activitySpinner: Spinner? = null
    private var sexSpinner: Spinner? = null




    private var _binding: FragmentGalleryBinding? = null
    private var picturePath: String? = ""
    private var picture: Bitmap? = null

    private var cameraButton: Button? = null
    private var buttonSignUp: Button? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory((requireActivity().application as AppApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val intent = requireActivity().intent
//        val activityLevel = intent.getStringExtra("activityLevel")
//        val age = intent.getStringExtra("age")!!.toIntOrNull()
//        val height = intent.getStringExtra("height")!!.toIntOrNull()
//        val weight = intent.getStringExtra("weight")!!.toIntOrNull()
//        val name = intent.getStringExtra("fullName")
//        val cityCountry = intent.getStringExtra("cityCountry")

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        appViewModel.userData.observe(viewLifecycleOwner, userDataObserver)
        appViewModel.getUser()

        buttonSignUp = binding.buttonSignUp
        buttonSignUp!!.setOnClickListener(this)

        cameraButton = binding.buttonPicture
        cameraButton!!.setOnClickListener(this)

        fullNameEt = binding.editTextFullName
        ageEt = binding.spinnerAge
        cityCountryEt = binding.editTextCityCountry
        weightEt = binding.spinnerWeight
        heightEt = binding.spinnerHeight
        activitySpinner = binding.spinnerActivity
        sexSpinner = binding.spinnerSex

        return root
    }

    private val userDataObserver: Observer<UserTable> =
        Observer { user ->
             fullName = user.fullName
             sex = user.sex
             activityLevel = user.activityLevel
             cityCountry = user.cityCountry
             age = user.age!!.toIntOrNull()
             height = user.height!!.toIntOrNull()
             weight = user.weight!!.toIntOrNull()
             picturePath = user.picturePath


            //set profile picture image
            if (picturePath != null) {
                val thumbnail = BitmapFactory.decodeFile(picturePath)
                (binding.imageViewPicture2).setImageBitmap(thumbnail)
            }
            // update bmr text box
            var bmrVal: Double
            var calTarget = 0.0
            if (age == null || height == null || weight == null || sex == "Select Sex") {
                bmrVal = 0.0
            } else {
                bmrVal = calculateBMR(sex!!, weight!!, height!!, age!!)
                if (activityLevel == "Sedentary") {
                    calTarget = bmrVal!! * 1.2
                } else if (activityLevel == "Light Exercise") {
                    calTarget = bmrVal!! * 1.375
                } else if (activityLevel == "Moderate Exercise") {
                    calTarget = bmrVal!! * 1.55
                } else if (activityLevel == "Heavy Exercise") {
                    calTarget = bmrVal!! * 1.725
                } else if (activityLevel == "Athlete") {
                    calTarget = bmrVal!! * 1.9
                } else {
                    calTarget = 0.0
                }
            }

            Log.d("Cal Target", calTarget.toString())
            binding.textViewBMR2.text = "Cal Target: ${calTarget.roundToInt().toString()}"

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
            binding.editTextFullName.setText(fullName)

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
                    val fullName = fullNameEt!!.text.toString()
                    val cityCountry = cityCountryEt!!.text.toString()
                    val activityLevel = activitySpinner!!.selectedItem.toString()
                    val sex = sexSpinner!!.selectedItem.toString()
                    val weight = weightEt!!.selectedItem.toString()
                    val height = heightEt!!.selectedItem.toString()
                    val age = ageEt!!.selectedItem.toString()
                    if (fullName.isNullOrBlank())  {
                        Toast.makeText(requireContext(), "Please fill fill in the name field", Toast.LENGTH_SHORT).show()
                    } else {
                        appViewModel.setUserData(fullName!!, cityCountry!!, activityLevel!!, sex!!,
                            picturePath!!, weight!!, height!!, age!!)
                    Toast.makeText(requireContext(),"Updated successfully!",Toast.LENGTH_SHORT).show()
                        var bmrVal: Double
                        var calTarget = 0.0
                        if (age == null || height == null || weight == null || sex == "Select Sex") {
                            bmrVal = 0.0
                        } else {
                            bmrVal = calculateBMR(sex!!, weight.toInt(), height.toInt(), age.toInt())
                            if (activityLevel == "Sedentary") {
                                calTarget = bmrVal!! * 1.2
                            } else if (activityLevel == "Light Exercise") {
                                calTarget = bmrVal!! * 1.375
                            } else if (activityLevel == "Moderate Exercise") {
                                calTarget = bmrVal!! * 1.55
                            } else if (activityLevel == "Heavy Exercise") {
                                calTarget = bmrVal!! * 1.725
                            } else if (activityLevel == "Athlete") {
                                calTarget = bmrVal!! * 1.9
                            } else {
                                calTarget = 0.0
                            }
                        }

                        Log.d("Cal Target", calTarget.toString())
                        binding.textViewBMR2.text = "Cal Target: ${calTarget.roundToInt().toString()}"
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

    private fun calculateBMR(sex: String, weight: Int, height: Int, age: Int): Double {
        if (sex == "Male") {
            return 66.47 + (6.24 * weight!!) + (12.7 * height!!) - (6.755 * age!!)
        }
        return 655.1 + (4.35 * weight!!) + (4.7 * height!!) - (4.7 * age!!)
    }
}