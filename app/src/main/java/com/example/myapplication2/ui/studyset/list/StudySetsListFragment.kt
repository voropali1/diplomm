package com.example.myapplication2.ui.studyset.list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication2.adapters.StudySetsAdapter
import com.example.myapplication2.databinding.FragmentStudySetsBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.ui.detail.StudySetDetailsActivity
import com.example.myapplication2.ui.profile.ProfileViewModel
import com.example.myapplication2.utils.getTabletLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudySetsListFragment : Fragment() {

    private var _binding: FragmentStudySetsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudySetsListViewModel by viewModels()
    private lateinit var adapter: StudySetsAdapter
    private var fullList: List<StudySet> = emptyList()
    private val profileViewModel: ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudySetsBinding.inflate(inflater, container, false)

        adapter = StudySetsAdapter(
            itemsList = emptyList(),
            callback = object : StudySetsAdapter.Callback {
                override fun onDeleteClicked(item: StudySet, position: Int) {
                    viewModel.deleteStudySet(item)
                }

                override fun onItemClicked(item: StudySet) {
                    openStudySetDetails(item)
                }
            }
        )

        binding.recyclerView.apply {
            layoutManager = requireContext().getTabletLayoutManager()
            adapter = this@StudySetsListFragment.adapter
        }

        viewModel.allStudySets.observe(viewLifecycleOwner) { studySets ->
            fullList = studySets ?: emptyList()
            updateFilteredList("")
        }

        binding.searchDictationET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateFilteredList(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return binding.root
    }

    private fun openStudySetDetails(studySet: StudySet) {
        val intent = StudySetDetailsActivity.newIntent(requireContext(), studySet)
        startActivity(intent)
    }

    private fun updateFilteredList(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.name?.contains(query, ignoreCase = true) == true }
        }

        if (filteredList.isEmpty()) {
            binding.textPlaceholder.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.textPlaceholder.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            adapter.updateList(filteredList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}