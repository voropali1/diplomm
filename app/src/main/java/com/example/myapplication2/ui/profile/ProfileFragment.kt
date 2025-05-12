package com.example.myapplication2.ui.profile

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication2.R
import com.example.myapplication2.databinding.FragmentProfileBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profileViewModel.totalSets.observe(viewLifecycleOwner) { total ->
                binding.usernameTextView.text = "Hello, ${profile.username}!"

                if (total == 0) {
                    binding.completedSetsTextView.text = "No statistics yet"
                    binding.progressPieChart.visibility = View.GONE
                } else {
                    binding.completedSetsTextView.text = "Completed sets: ${profile.completedSets}/$total"
                    binding.progressPieChart.visibility = View.VISIBLE
                    updateChart(profile.completedSets, total)
                }
            }
        }

        binding.signOutButton.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }


    private fun updateChart(completed: Int, total: Int) {
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
        pieChart.invalidate() // обновить
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





