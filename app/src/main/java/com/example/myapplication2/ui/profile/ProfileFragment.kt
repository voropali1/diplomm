package com.example.myapplication2.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication2.R
import com.example.myapplication2.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Пример получения данных из ViewModel
        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            binding.usernameTextView.text = profile.username
            binding.termsCountTextView.text = "Изучено терминов: ${profile.termsCount}"
            binding.completedSetsTextView.text = "Пройдено сетов: ${profile.completedSets}"
            binding.progressPercentageTextView.text = "Прогресс: ${profile.progress}%"
            binding.progressBar.progress = profile.progress
        }

        // Обработка нажатия кнопки "Sign Out"
        binding.signOutButton.setOnClickListener {
            // Логика выхода из аккаунта
            signOut()
        }
    }

    private fun signOut() {
        // Пример выхода из аккаунта. Очистка SharedPreferences или другого хранилища данных
        val preferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        preferences.edit().clear().apply()

        // Навигация обратно на экран входа
        findNavController().navigate(R.id.profileFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
