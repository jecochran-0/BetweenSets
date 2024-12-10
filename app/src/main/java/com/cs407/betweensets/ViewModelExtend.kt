package com.cs407.betweensets
import android.app.Application
class ViewModelExtend : Application() {
    val userViewModel: UserViewModel by lazy {
        UserViewModel()
    }
}