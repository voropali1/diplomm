package com.example.myapplication2.ui.spash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication2.MainActivity
import com.example.myapplication2.repository.UserProfileRepository
import com.example.myapplication2.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MySplashActivity : AppCompatActivity() {

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (userProfileRepository.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}