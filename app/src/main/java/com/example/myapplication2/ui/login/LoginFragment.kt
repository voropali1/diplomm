package com.example.myapplication2.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapplication2.MainActivity
import com.example.myapplication2.databinding.FragmentLoginPageBinding
import com.example.myapplication2.repository.UserProfileRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    private val viewModel: LoginViewModel by viewModels()

    private val clientId =
        "241593447822-mceq1e9bbvq32d1lq6g7a91ns2ea2knl.apps.googleusercontent.com"

    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loader.observe(viewLifecycleOwner) { handleLoading(it) }
        viewModel.startMainActivity.observe(viewLifecycleOwner) { handleStartMainActivity(it) }
        viewModel.showError.observe(viewLifecycleOwner) { handleShowError(it) }

        binding.googleLoginButton.setOnClickListener {
            googleSignIn()
        }
    }

    private fun googleSignIn() {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .build()

        googleAuth(googleIdOption)
    }

    private fun googleSignUp() {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .build()

        googleAuth(googleIdOption)
    }

    private fun googleAuth(googleIdOption: GetGoogleIdOption) {
        val credentialManager = CredentialManager.create(requireContext())
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireContext(),
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                handleFailure(e)
                googleSignUp()
            }
        }
    }

    private fun handleFailure(e: Exception) {
        Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {

            is PublicKeyCredential -> {
                val id = GoogleIdTokenCredential.createFrom(credential.data).id
                Log.d(TAG, "PublicKeyCredential: $id")
                openMainActivity(id)
            }

            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
                openMainActivity(username)
                Log.d(TAG, "PasswordCredential: $username")
            }

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val email = googleIdTokenCredential.id
                        userProfileRepository.setLoginEmail(email)
                        openMainActivity(email)
                        Log.d(TAG, "Google ID Token: $email")
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                googleSignUp()
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun openMainActivity(email: String) {
        viewModel.login(email)
    }

    private fun showLoader() {
        binding.flLoader.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.flLoader.visibility = View.GONE
    }

    private fun handleShowError(error: String?) {
        if (error == null) {
            return
        }

        viewModel.showError.value = null
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun handleStartMainActivity(event: Unit?) {
        if (event == null) {
            return
        }

        viewModel.startMainActivity.value = null
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun handleLoading(show: Boolean?) {
        if (show == true) {
            showLoader()
        } else {
            hideLoader()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}

