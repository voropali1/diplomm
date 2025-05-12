package com.example.myapplication2.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.myapplication2.R
import com.example.myapplication2.databinding.ActivityStudySetBinding
import com.example.myapplication2.model.StudySet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudySetDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudySetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStudySetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val studySet = intent.extras?.getSerializable(KEY_STUDY_SET) as? StudySet

        val navController = findNavController(R.id.nav_host)
        val navGraph = navController.navInflater.inflate(R.navigation.study_sets_navigation)
        // Set the start destination
        navGraph.setStartDestination(R.id.studySetDetailsFragment)

        // Pass arguments
        val args = Bundle().apply { putSerializable("studySet", studySet) }
        navController.setGraph(navGraph, args)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object {
        private const val KEY_STUDY_SET = "studySet"

        fun newIntent(context: Context, studySet: StudySet): Intent {
            return Intent(context, StudySetDetailsActivity::class.java).apply {
                putExtra(KEY_STUDY_SET, studySet)
            }
        }
    }
}