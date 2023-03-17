package com.example.project1.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project1.R
import com.example.project1.databinding.FragmentHomeBinding
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.Executors


class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var picturePath: String? = null

    private var weatherData: String? = null

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

        binding.mapsButton.setOnClickListener(this)
        binding.buttonWeather.setOnClickListener(this)

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

    override fun onClick(view: View) {
        val cityCountry = requireActivity().intent.getStringExtra("cityCountry")
        when (view.id) {
            R.id.mapsButton -> {
                if (cityCountry.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "No Location to Search", Toast.LENGTH_SHORT).show()
                } else {
                    //We have to grab the search term and construct a URI object from it.
                    //We'll hardcode WEB's location here
                    val searchUri = Uri.parse("geo:0,0?q=$cityCountry Hikes")

                    //Create the implicit intent
                    val mapIntent = Intent(Intent.ACTION_VIEW, searchUri)

                    //If there's an activity associated with this intent, launch it
                    try{
                        startActivity(mapIntent)
                    }catch(ex: ActivityNotFoundException){
                        //handle errors here
                    }
                }
            }
            R.id.buttonWeather -> {
                if (cityCountry.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "No Location to Search", Toast.LENGTH_SHORT).show()
                } else {
                    var executorService = Executors.newSingleThreadExecutor()
                    var mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper())
                    executorService.execute {
                        val weather = getWeather(cityCountry)

                        mainThreadHandler.post {
                            weatherData = weather
                            val weatherJSON = JSONObject(weatherData)
                            // Converting Kelvin to Fahrenheit
                            val temp = (weatherJSON.getJSONObject("main").getDouble("temp") - 273.150) * 1.8 + 32
                            val feelsLike = (weatherJSON.getJSONObject("main").getDouble("feels_like")- 273.150) * 1.8 + 32
                            val tempMin = (weatherJSON.getJSONObject("main").getDouble("temp_min")- 273.150) * 1.8 + 32
                            val tempMax = (weatherJSON.getJSONObject("main").getDouble("temp_max")- 273.150) * 1.8 + 32
                            //MPS to MPH
                            val windSpeed = weatherJSON.getJSONObject("wind").getDouble("speed") * 2.23694
                        }
                    }
                }
            }
        }
    }

    private fun getWeather(cityCountry: String): String {
        val url = URL("https://api.openweathermap.org/data/2.5/weather?q=$cityCountry&appid=99ea8382701bd7481e5ea568772f739a")
        val connection = url.openConnection() as HttpURLConnection
        val weather = try {
            val inputStream = connection.inputStream

            //The scanner trick: search for the next "beginning" of the input stream
            //No need to user BufferedReader
            val scanner = Scanner(inputStream)
            scanner.useDelimiter("\\A")
            val hasInput = scanner.hasNext()
            if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            connection.disconnect()
        }

        if (weather != null) {
            return weather
        }
        return "Not Found"
    }
}