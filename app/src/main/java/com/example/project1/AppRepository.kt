package com.example.project1

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.annotation.WorkerThread
import androidx.core.content.ContentProviderCompat.requireContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.jvm.Synchronized

class AppRepository private constructor(appDao: AppDao) {
    private var mFullName: String? = null
    private var mCityCountry: String? = null
    private var mActivityLevel: String? = null
    private var mSex: String? = null
    private var mPicturePath: String ?= null
    private var mWeight: String? = null
    private var mHeight: String? = null
    private var mAge: String? = null

    private var mAppDao: AppDao = appDao


    fun getUserData(): List<UserTable>{
        return mAppDao.getUser()
    }

     fun setUserData(fullName: String, cityCountry: String, activityLevel: String, sex : String, picturePath: String,
                    weight: String, height: String, age: String)
    {
        mFullName = fullName
        mCityCountry = cityCountry
        mActivityLevel = activityLevel
        mSex = sex
        mPicturePath = picturePath
        mWeight = weight
        mHeight = height
        mAge = age
        mScope.launch (Dispatchers.IO){
            insert()
        }
    }


    @WorkerThread
    suspend fun insert() {
        mAppDao.deactivateAll()
        if (mFullName != null) {
            mAppDao.insert(UserTable(mFullName!!, mCityCountry!!, mActivityLevel!!, mSex!!, mPicturePath!!, mWeight!!, mHeight!!, mAge!!, true))
        }
    }

    @WorkerThread
    suspend fun fetchAndParseWeatherData(location: String) {
        // TODO: move weather logic from HomeFragment to here
    }

    // Make the repository singleton. Could in theory
    // make this an object class, but the companion object approach
    // is nicer (imo)
    companion object {
        private var mInstance: AppRepository? = null
        private lateinit var mScope: CoroutineScope
        @Synchronized
        fun getInstance(appDao: AppDao,
                        scope: CoroutineScope
        ): AppRepository {
            mScope = scope
            return mInstance?: synchronized(this){
                val instance = AppRepository(appDao)
                mInstance = instance
                instance
            }
        }
    }


    fun getWeatherData(): String {
        mScope.launch(Dispatchers.IO){
            getWeather()
        }
    }

    @WorkerThread
     suspend fun getWeather(): String {
        var url: URL? = null
        try {
            url =
                URL(
                    "https://api.openweathermap.org/data/2.5/weather?q=${
                        cityCountry.replace(
                            " ",
                            "%20"
                        )
                    }&appid=99ea8382701bd7481e5ea568772f739a"
                )
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
            } catch (e: Exception) {
                return "Not Found"
            } finally {
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

}