package com.example.myapplication2

import androidx.lifecycle.MutableLiveData
import com.example.myapplication2.database.DaoStudySet
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.repository.FirebaseRepository
import com.example.myapplication2.repository.StudySetRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StudySetRepositoryTest {

  private lateinit var dao: DaoStudySet
  private lateinit var firebaseRepository: FirebaseRepository
  private lateinit var studySetRepository: StudySetRepository

  @Before
  fun setUp() {
    dao = mockk { every { getAllStudySets() } returns MutableLiveData() }
    firebaseRepository = mockk()
    studySetRepository = StudySetRepository(dao, firebaseRepository)
  }

  @Test
  fun `test insert study set`() = runTest {
    val studySet = StudySet(
      id = 0,
      name = "Test Set",
      words = "word1,word2",
      marked_words = "",
      language_to = "en",
      language_from = "es",
      amount_of_words = 2,
      isFinished = false
    )
    coEvery { dao.insertStudySet(studySet) } returns 1L
    coEvery { firebaseRepository.addStudySet(studySet, any(), any()) } just runs

    val result = studySetRepository.insert(studySet)

    assertEquals(1L, result)
    coVerify {
      dao.insertStudySet(studySet)
      firebaseRepository.addStudySet(studySet, any(), any())
    }
  }

  @Test
  fun `test update study set`() = runTest {
    val studySet = StudySet(
      id = 1,
      name = "Updated Set",
      words = "word1,word2",
      marked_words = "",
      language_to = "en",
      language_from = "es",
      amount_of_words = 2,
      isFinished = false
    )

    coEvery {
      dao.updateStudySet(studySet)
      firebaseRepository.updateStudySet(studySet)
    } just runs

    studySetRepository.update(studySet)

    coVerify {
      dao.updateStudySet(studySet)
      firebaseRepository.updateStudySet(studySet)
    }
  }

  @Test
  fun `test delete study set`() = runTest {
    val studySet = StudySet(
      id = 1,
      name = "Test Set",
      words = "word1,word2",
      marked_words = "",
      language_to = "en",
      language_from = "es",
      amount_of_words = 2,
      isFinished = false
    )

    coEvery {
      dao.deleteStudySet(studySet)
      firebaseRepository.deleteStudySet(studySet)
    } just runs

    studySetRepository.delete(studySet)

    coVerify {
      dao.deleteStudySet(studySet)
      firebaseRepository.deleteStudySet(studySet)
    }
  }

  @Test
  fun `test delete all study sets`() = runTest {
    coEvery { dao.deleteAll() } just runs

    studySetRepository.deleteAll()

    coVerify { dao.deleteAll() }
  }

  @Test
  fun `test get study set by id`() = runTest {
    val studySet = StudySet(
      id = 1,
      name = "Test Set",
      words = "word1,word2",
      marked_words = "",
      language_to = "en",
      language_from = "es",
      amount_of_words = 2,
      isFinished = false
    )
    coEvery { dao.getNoLiveDataSpecificStudySet(1) } returns studySet

    val result = studySetRepository.getStudySetById(1)

    assertEquals(studySet, result)
    coVerify { dao.getNoLiveDataSpecificStudySet(1) }
  }

  @Test
  fun `test update set finished status`() = runTest {
    val studySet = StudySet(
      id = 1,
      name = "Test Set",
      words = "word1,word2",
      marked_words = "",
      language_to = "en",
      language_from = "es",
      amount_of_words = 2,
      isFinished = false
    )
    coEvery {
      dao.updateStudySet(studySet)
      firebaseRepository.updateSetFinished(1)
    } just runs
    coEvery { dao.getNoLiveDataSpecificStudySet(1) } returns studySet

    studySetRepository.updateSetFinishedStatus(1)

    assertTrue(studySet.isFinished)
    coVerify {
      dao.getNoLiveDataSpecificStudySet(1)
      dao.updateStudySet(studySet)
      firebaseRepository.updateSetFinished(1)
    }
  }

  @Test
  fun `test all study sets live data`() {
    val liveData = MutableLiveData<List<StudySet>>()
    every { dao.getAllStudySets() } returns liveData

    val result = studySetRepository.allStudySets

    assertEquals(liveData.value?.size, result.value?.size)
    verify { dao.getAllStudySets() }
  }
}