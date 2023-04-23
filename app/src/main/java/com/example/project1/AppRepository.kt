package com.example.project1

import androidx.lifecycle.MutableLiveData
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
        if (mFullName != null) {
            mAppDao.insert(UserTable(mFullName!!, mCityCountry!!, mActivityLevel!!, mSex!!, mPicturePath!!, mWeight!!, mHeight!!, mAge!!))
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
}