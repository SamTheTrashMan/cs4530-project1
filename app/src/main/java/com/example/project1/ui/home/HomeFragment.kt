package com.example.project1.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.project1.*
import com.example.project1.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.MalformedURLException
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

    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory((requireActivity().application as AppApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val intent = requireActivity().intent

        val user = appViewModel.getUser()[0]

        val sex = user.age
        val activityLevel = user.activityLevel
        val age = user.age.toIntOrNull()
        val height = user.height.toIntOrNull()
        val weight = user.weight.toIntOrNull()
        val cityCountry = user.cityCountry

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        picturePath = user.picturePath
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
            if (activityLevel == "Sedentary") {
                calTarget = BMRVal!! * 1.2
            } else if (activityLevel == "Light Exercise") {
                calTarget = BMRVal!! * 1.375
            } else if (activityLevel == "Moderate Exercise") {
                calTarget = BMRVal!! * 1.55
            } else if (activityLevel == "Heavy Exercise") {
                calTarget = BMRVal!! * 1.725
            } else if (activityLevel == "Athlete") {
                calTarget = BMRVal!! * 1.9
            } else {
                calTarget = 0.0
            }
        }

        binding.textViewBMR.text = "Cal Target: $calTarget"

        binding.mapsButton.setOnClickListener(this)
        binding.buttonWeather.setOnClickListener(this)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun calculateBMR(sex: String, weight: Int, height: Int, age: Int): Double {
        if (sex == "Male") {
            return 66.47 + (6.24 * weight!!) + (12.7 * height!!) - (6.755 * age!!)
        }
        return 655.1 + (4.35 * weight!!) + (4.7 * height!!) - (4.7 * age!!)
    }

    override fun onClick(view: View) {
        val cityCountry = requireActivity().intent.getStringExtra("cityCountry")
        when (view.id) {
            R.id.mapsButton -> {
                if (cityCountry.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "No Location to Search", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    //We have to grab the search term and construct a URI object from it.
                    //We'll hardcode WEB's location here
                    val searchUri = Uri.parse("geo:0,0?q=$cityCountry Hikes")

                    //Create the implicit intent
                    val mapIntent = Intent(Intent.ACTION_VIEW, searchUri)

                    //If there's an activity associated with this intent, launch it
                    try {
                        startActivity(mapIntent)
                    } catch (ex: ActivityNotFoundException) {
                        //handle errors here
                    }
                }
            }
            R.id.buttonWeather -> {
                if (cityCountry.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "No Location to Search", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    var executorService = Executors.newSingleThreadExecutor()
                    var mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper())
                    executorService.execute {

                        val weather = getWeather(cityCountry)

                        if(weather != "Not Found") {
                            mainThreadHandler.post {
                                weatherData = weather
                                val weatherJSON = JSONObject(weatherData)
                                println(weatherJSON)
                                val errorCheck = weatherJSON.getInt("cod")
                                if (errorCheck.toString() == "404") {
                                    Toast.makeText(
                                        requireContext(),
                                        "Invalid city and country",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    // Converting Kelvin to Fahrenheit
                                    var temp = (weatherJSON.getJSONObject("main")
                                        .getDouble("temp") - 273.150) * 1.8 + 32
                                    val feelsLike = (weatherJSON.getJSONObject("main")
                                        .getDouble("feels_like") - 273.150) * 1.8 + 32
                                    var tempMin = (weatherJSON.getJSONObject("main")
                                        .getDouble("temp_min") - 273.150) * 1.8 + 32
                                    var tempMax = (weatherJSON.getJSONObject("main")
                                        .getDouble("temp_max") - 273.150) * 1.8 + 32
                                    //MPS to MPH
                                    val windSpeed =
                                        weatherJSON.getJSONObject("wind")
                                            .getDouble("speed") * 2.23694
                                    val tempInt = temp.toInt()
                                    val tempMaxInt = tempMax.toInt()
                                    val tempMinInt = tempMin.toInt()

                                    binding.textViewCurrTemp.text = "Current Temp: $tempInt"
                                    binding.textViewHighTemp.text = "High Temp: $tempMaxInt"
                                    binding.textViewLowTemp.text = "Low Temp: $tempMinInt"
                                }
                            }
                        } else {
                            binding.textViewCurrTemp.text = "Invalid location"
                        }
                    }
                }
            }
        }
    }

    private fun getWeather(cityCountry: String): String {
        var url: URL? = null
        try {
            url =
                URL("https://api.openweathermap.org/data/2.5/weather?q=${cityCountry.replace(" ", "%20")}&appid=99ea8382701bd7481e5ea568772f739a")
            println(url)
            val connection = url!!.openConnection() as HttpURLConnection
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
            } catch (e: Exception) {return "Not Found"}
            finally {
                connection.disconnect()
            }

            if (weather != null) {
                return weather
            }
            return "Not Found"
        } catch (e: MalformedURLException) {
            Toast.makeText(requireContext(), "Invalid city and country", Toast.LENGTH_SHORT).show()
        }
        return "Not Found"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("currTemp", binding.textViewCurrTemp.text.toString())
        outState.putString("lowTemp", binding.textViewLowTemp.text.toString())
        outState.putString("highTemp", binding.textViewHighTemp.text.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            binding.textViewCurrTemp.text = savedInstanceState!!.getString("currTemp")
            binding.textViewLowTemp.text = savedInstanceState!!.getString("lowTemp")
            binding.textViewHighTemp.text = savedInstanceState!!.getString("highTemp")
        }
    }
}