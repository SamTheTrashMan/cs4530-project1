package com.example.project1

import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow

class AppViewModel(repository: AppRepository) : ViewModel() {
    //The singleton repository. If our app maps to one process, the recommended
    // pattern is to make repo and db singletons. That said, it's sometimes useful
    // to have more than one repo so it doesn't become a kitchen sink class, but each
    // of those repos could be singleton.
    private var mAppRepository: AppRepository = repository


    fun setUserData(fullName: String, cityCountry: String, activityLevel: String, sex : String, picturePath: String,
    weight: String, height: String, age: String)
    {
        // Simply pass the location to the repository
        mAppRepository.setUserData(fullName, cityCountry, activityLevel, sex, picturePath, weight, height, age)
    }

    fun getUser(): List<UserTable> {
        return mAppRepository.userData;
    }
}

// This factory class allows us to define custom constructors for the view model
class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}