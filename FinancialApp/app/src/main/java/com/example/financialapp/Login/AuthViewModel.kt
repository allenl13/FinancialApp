package com.example.financialapp.Login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
            _userProfile.value = null
        } else {
            _authState.value = AuthState.Authenticated
            loadUserProfile()
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    loadUserProfile()
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    val userDoc = hashMapOf(
                        "email" to email,
                        "displayName" to "",
                        "createdAt" to Timestamp.now()
                    )

                    db.collection("users").document(uid)
                        .set(userDoc)
                        .addOnSuccessListener {
                            _authState.value = AuthState.Authenticated
                            _userProfile.value = UserProfile(
                                email = email,
                                displayName = "",
                                createdAt = System.currentTimeMillis()
                            )
                        }
                        .addOnFailureListener { e ->
                            _authState.value =
                                AuthState.Error("Account created but failed saving profile: ${e.message}")
                        }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _userProfile.value = null
        _authState.value = AuthState.Unauthenticated
    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { snap ->
                if (snap.exists()) {
                    _userProfile.value = UserProfile(
                        email = snap.getString("email") ?: "",
                        displayName = snap.getString("displayName") ?: "",
                        createdAt = snap.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                    )
                } else {
                    _userProfile.value = null
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error("Failed to load user data: ${e.message}")
            }
    }

    private val _resetState = MutableLiveData<ResetState>(ResetState.Idle)
    val resetState: LiveData<ResetState> = _resetState

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _resetState.value = ResetState.Error("Please enter your email.")
            return
        }
        _resetState.value = ResetState.Sending

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _resetState.value = if (task.isSuccessful) {
                    ResetState.Sent
                } else {
                    ResetState.Error(task.exception?.localizedMessage ?: "Failed to send reset email.")
                }
            }
    }

    fun clearResetState() {
        _resetState.value = ResetState.Idle
    }
}

// forgot password UI states
sealed class ResetState {
    data object Idle : ResetState()
    data object Sending : ResetState()
    data object Sent : ResetState()
    data class Error(val message: String) : ResetState()
}




// auth states
sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

// user profile model
data class UserProfile(
    val email: String = "",
    val displayName: String = "",
    val createdAt: Long = 0L
)