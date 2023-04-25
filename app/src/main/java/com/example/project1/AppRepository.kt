package com.example.project1

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    val data = MutableLiveData<String>()

    val userData = MutableLiveData<UserTable>()

    fun getUserData() {
        mScope.launch(Dispatchers.IO) {
            getUser()
        }
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
    fun getUser() {
        val data = mAppDao.getUser()
        data.forEach {
            userData.postValue(
                UserTable(fullName = it.fullName,
                    cityCountry = it.cityCountry,
                    activityLevel = it.activityLevel,
                    sex = it.sex,
                    picturePath = it.picturePath,
                    weight = it.weight,
                    height = it.height,
                    age = it.age,
                    active = it.active
                )
            )
        }
    }

    @WorkerThread
    suspend fun insert() {
        mAppDao.deactivateAll()
        if (mFullName != null) {
            mAppDao.insert(UserTable(mFullName!!, mCityCountry!!, mActivityLevel!!, mSex!!, mPicturePath!!, mWeight!!, mHeight!!, mAge!!, true))
            mScope.launch (Dispatchers.IO){
                getWeather(mCityCountry!!)
            }
        }
    }

    @WorkerThread
    private fun getWeather(cityCountry: String) {
        if (cityCountry == "") {
            return
        }

        Log.d("city", cityCountry)

        var url: URL?
        try {
            url =
                URL("https://api.openweathermap.org/data/2.5/weather?q=${cityCountry.replace(" ", "%20")}&appid=99ea8382701bd7481e5ea568772f739a")
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
                data.postValue("Not Found")
            }
            finally {
                connection.disconnect()
            }

            if (weather != null && weather is String) {
                data.postValue(weather!!)
            } else {
                data.postValue("Not Found")
            }
        } catch (e: MalformedURLException) {
            data.postValue("Not Found")
        }
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
}