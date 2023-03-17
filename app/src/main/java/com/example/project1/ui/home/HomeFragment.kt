package com.example.project1.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project1.R
import com.example.project1.databinding.FragmentHomeBinding
import org.w3c.dom.Text


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var picturePath: String? = null

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
        val cityCountry = intent.getStringExtra("cityCountry")



        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        picturePath = intent.getStringExtra("picturePath")
        if (picturePath != null) {
            val thumbnail = BitmapFactory.decodeFile(picturePath)
            (binding.imageViewPicture).setImageBitmap(thumbnail)
        }

        var BMRVal = 0.0
        var calTarget = 0.0
        if (age == null || height == null || weight == null || sex == "Select Sex") {
            BMRVal = 0.0
        } else {
            BMRVal = calculateBMR(sex!!, weight, height, age)
            if (activityLevel == "Sedentary")
            {
                calTarget = BMRVal!! * 1.2
            }
            else if(activityLevel == "Light Exercise")
            {
                calTarget = BMRVal!! * 1.375
            }
            else if (activityLevel == "Moderate Exercise")
            {
                calTarget = BMRVal!! * 1.55
            }
            else if (activityLevel == "Heavy Exercise")
            {
                calTarget = BMRVal!! * 1.725
            }
            else if (activityLevel == "Athlete")
            {
                calTarget = BMRVal!! * 1.9
            }
            else
            {
                calTarget = 0.0
            }
        }

        binding.textViewBMR.text = BMRVal.toString()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun calculateBMR(sex: String, weight: Int, height: Int, age: Int): Double {
        if(sex == "Male") {
            return 66.47 + ( 6.24 * weight!!)+ ( 12.7 * height!!) - ( 6.755 * age!!)
        }
        return 655.1 + ( 4.35 * weight!!) + ( 4.7 * height!!) - ( 4.7 * age!!)
    }


}