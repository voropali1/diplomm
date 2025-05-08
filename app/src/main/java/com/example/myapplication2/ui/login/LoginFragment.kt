package com.example.myapplication2.ui.login

import com.example.myapplication2.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton



class LoginFragment : Fragment(R.layout.fragment_login_page) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация кнопки для входа
        val loginButton: MaterialButton = view.findViewById(R.id.loginButton)

        // Скрыть Toolbar (если используется) и нижнее меню
        //activity?.findViewById<Toolbar>(R.id.)?.visibility = View.GONE
        //activity?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE

        // Устанавливаем обработчик нажатия на кнопку
        loginButton.setOnClickListener {
            // Переход к экрану с набором студийных сетов
            findNavController().navigate(R.id.action_loginFragment_to_studySetsFragment)
        }


    }
}

