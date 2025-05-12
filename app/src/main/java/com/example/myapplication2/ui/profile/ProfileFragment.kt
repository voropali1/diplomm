package com.example.myapplication2.ui.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapplication2.R
import com.example.myapplication2.databinding.FragmentProfileBinding
import com.example.myapplication2.model.UserProfile
import com.example.myapplication2.ui.login.LoginActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.userProfile.observe(viewLifecycleOwner) { handleUser(it) }
        profileViewModel.statistics.observe(viewLifecycleOwner) { handleStatistics(it) }
        profileViewModel.showLoader.observe(viewLifecycleOwner) { handleLoader(it) }

        binding.signOutButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                CredentialManager.create(requireContext())
                    .clearCredentialState(ClearCredentialStateRequest())

                profileViewModel.signOut()
                withContext(Dispatchers.Main) {
                    startActivity(
                        Intent(requireContext(), LoginActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                }
            }
        }

        binding.btnSync.setOnClickListener {
            profileViewModel.sync()
        }
    }

    private fun handleUser(profile: UserProfile) {
        binding.usernameTextView.text = "Hello, ${profile.username}!"
    }

    private fun handleStatistics(statistics: Statistics) {
        val total = statistics.totalSets
        val completedSets = statistics.completedSets

        if (total == 0) {
            binding.completedSetsTextView.text = "No statistics yet"
            binding.progressPieChart.visibility = View.GONE
        } else {
            binding.completedSetsTextView.text = "Completed sets: $completedSets/$total"
            binding.progressPieChart.visibility = View.VISIBLE
            updateChart(completedSets, total)
        }
    }

    private fun handleLoader(show: Boolean?) {
        if (show == null) {
            return
        }

        profileViewModel.showLoader.value = null

        if (show) {
            binding.flLoader.visibility = View.VISIBLE
        } else {
            binding.flLoader.visibility = View.GONE
        }
    }

    private fun updateChart(completed: Int, total: Int) {
        if (total == 0) {
            return
        }

        val pieChart = binding.progressPieChart

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(completed.toFloat(), ""))
        entries.add(PieEntry((total - completed).toFloat(), ""))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.blue_300)
        )
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f
        dataSet.setDrawValues(false)

        val data = PieData(dataSet)

        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.isRotationEnabled = false
        pieChart.centerText = "${(completed * 100 / total)}%"
        pieChart.setCenterTextSize(18f)
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setUsePercentValues(false)
        pieChart.legend.isEnabled = false
        pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}