package com.example.myapplication2

import androidx.lifecycle.Observer
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.repository.FirebaseRepository
import com.example.myapplication2.repository.StudySetRepository
import com.example.myapplication2.ui.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoginViewModelTest {

    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var studySetRepository: StudySetRepository
    private lateinit var loginViewModel: LoginViewModel

    private val loaderObserver: Observer<Boolean> = mockk(relaxed = true)
    private val startMainActivityObserver: Observer<Unit?> = mockk(relaxed = true)
    private val showErrorObserver: Observer<String?> = mockk(relaxed = true)

    @Before
    fun setUp() {
        firebaseRepository = mockk()
        studySetRepository = mockk()
        loginViewModel = LoginViewModel(firebaseRepository, studySetRepository)

        loginViewModel.loader.observeForever(loaderObserver)
        loginViewModel.startMainActivity.observeForever(startMainActivityObserver)
        loginViewModel.showError.observeForever(showErrorObserver)
    }

    @Test
    fun `test login success`() = runTest {
        val email = "test@example.com"
        val studySets = listOf(
            StudySet(1, "Set 1", "word1,word2", "", "en", "es", 2, false)
        )

        every { firebaseRepository.createUser(email, any(), any()) } answers {
            secondArg<() -> Unit>().invoke()
        }
        every { firebaseRepository.getAllStudySets(any(), any()) } answers {
            firstArg<(List<StudySet>) -> Unit>().invoke(studySets)
        }
        coEvery { studySetRepository.upsertManyLocalOnly(studySets) } just runs

        loginViewModel.login(email)

        coVerify {
            loaderObserver.onChanged(true)
            firebaseRepository.createUser(email, any(), any())
            firebaseRepository.getAllStudySets(any(), any())
            studySetRepository.upsertManyLocalOnly(studySets)
            loaderObserver.onChanged(false)
            startMainActivityObserver.onChanged(Unit)
        }
    }

    @Test
    fun `test login failure`() = runTest {
        val email = "test@example.com"

        every { firebaseRepository.createUser(email, any(), any()) } answers {
            thirdArg<() -> Unit>().invoke()
        }

        loginViewModel.login(email)

        verify {
            loaderObserver.onChanged(true)
            firebaseRepository.createUser(email, any(), any())
            loaderObserver.onChanged(false)
            showErrorObserver.onChanged("Failed to create user")
        }
    }

    @Test
    fun `test fetch study sets failure`() = runTest {
        val email = "test@example.com"

        every { firebaseRepository.createUser(email, any(), any()) } answers {
            secondArg<() -> Unit>().invoke()
        }
        every { firebaseRepository.getAllStudySets(any(), any()) } answers {
            secondArg<() -> Unit>().invoke()
        }

        loginViewModel.login(email)

        verify {
            loaderObserver.onChanged(true)
            firebaseRepository.createUser(email, any(), any())
            firebaseRepository.getAllStudySets(any(), any())
            loaderObserver.onChanged(false)
            showErrorObserver.onChanged("Failed to fetch study sets")
            startMainActivityObserver.onChanged(Unit)
        }
    }
}