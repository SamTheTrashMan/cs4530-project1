package com.example.project1.ui.gallery

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project1.databinding.FragmentGalleryBinding
import com.example.project1.databinding.FragmentHomeBinding
import com.example.project1.ui.home.HomeViewModel
import kotlin.collections.ArrayList

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

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
        binding.spinnerWeight!!.adapter = weightAdapter

        binding.editTextCityCountry.setText(cityCountry)
        binding.editTextFullName.setText(name)
        binding.spinnerAge.setSelection(age!!)
        binding.spinnerHeight.setSelection(height!!)
        binding.spinnerWeight.setSelection(weight!!)

        binding.spinnerActivity.setSelection(calculateActivityIndex(activityLevel!!))
        binding.spinnerSex.setSelection(calculateSexIndex(sex!!))



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