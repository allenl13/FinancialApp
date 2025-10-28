package com.example.financialapp.Login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.MultiFactorResolver
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorGenerator
import com.google.firebase.auth.PhoneMultiFactorInfo
import com.google.firebase.firestore.firestore
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    //MFA
    private var mfaResolver: MultiFactorResolver? = null
    private var verificationId: String? = null

    private val _mfaState = MutableLiveData<String>()
    val mfaState: LiveData<String> = _mfaState

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
                    val ex = task.exception
                    if (ex is FirebaseAuthMultiFactorException) {
                        // save resolver and move to mfa screen
                        mfaResolver = ex.resolver
                        _mfaState.value = "MFA_REQUIRED"
                        _authState.value = AuthState.Unauthenticated   // reenable the login ui
                    } else {
                        _authState.value = AuthState.Error(ex?.message ?: "Something went wrong")
                    }

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

    fun needsMfaEnrollment(): Boolean =
        FirebaseAuth.getInstance().currentUser?.multiFactor?.enrolledFactors?.isEmpty() == true

    // Start SMS 2FA ENROLLMENT (sends code to the phone entered on AddMFA page)
    fun startMfaEnrollment(phoneE164: String, activity: Activity) {
        val mfa = FirebaseAuth.getInstance().currentUser?.multiFactor
            ?: run { _mfaState.postValue("MFA_ERROR: Not signed in"); return }

        _mfaState.postValue("ENROLL_SENDING")

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneE164)               // e.g. +15555550100 (test number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(cred: PhoneAuthCredential) {
                    // Instant/auto verify path
                    val assertion = PhoneMultiFactorGenerator.getAssertion(cred)
                    mfa.enroll(assertion, "My phone")
                        .addOnSuccessListener { _mfaState.postValue("ENROLL_SUCCESS") }
                        .addOnFailureListener { e -> _mfaState.postValue("MFA_ERROR: ${e.message}") }
                }
                override fun onVerificationFailed(e: FirebaseException) {
                    _mfaState.postValue("MFA_ERROR: ${e.message}")
                }
                override fun onCodeSent(vid: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId = vid
                    _mfaState.postValue("ENROLL_CODE_SENT")
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Finish ENROLLMENT after the user types the code on AddMFA page
    fun finalizeMfaEnrollment(code: String) {
        val mfa = FirebaseAuth.getInstance().currentUser?.multiFactor
            ?: run { _mfaState.postValue("MFA_ERROR: Not signed in"); return }
        val vid = verificationId
            ?: run { _mfaState.postValue("MFA_ERROR: No verificationId"); return }

        val cred = PhoneAuthProvider.getCredential(vid, code)
        val assertion = PhoneMultiFactorGenerator.getAssertion(cred)
        _mfaState.postValue("ENROLLING")
        mfa.enroll(assertion, "My phone")
            .addOnSuccessListener { _mfaState.postValue("ENROLL_SUCCESS") }
            .addOnFailureListener { e -> _mfaState.postValue("MFA_ERROR: ${e.message}") }
    }

    fun startMfaChallenge(activity: Activity) {
        val resolver = mfaResolver ?: run {
            _mfaState.value = "MFA_ERROR: No resolver"
            return
        }

        val phoneInfo = resolver.hints
            .filterIsInstance<PhoneMultiFactorInfo>()
            .firstOrNull() ?: run {
            _mfaState.value = "MFA_ERROR: No phone factor"
            return
        }

        _mfaState.value = "MFA_SENDING_CODE"

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(cred: PhoneAuthCredential) {
                finishMfaSignIn(cred)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                _mfaState.value = "MFA_ERROR: ${e.message}"
            }
            override fun onCodeSent(vid: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = vid
                _mfaState.value = "MFA_CODE_SENT"
            }
        }

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setActivity(activity)
            .setMultiFactorSession(resolver.session)
            .setMultiFactorHint(phoneInfo)   //for mfa
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // verify mfa code
    fun verifyMfaCode(code: String) {
        val vid = verificationId ?: run {
            _mfaState.value = "MFA_ERROR: No verification in progress"
            return
        }
        val cred = PhoneAuthProvider.getCredential(vid, code)
        finishMfaSignIn(cred)
    }

    //finishes MFA sign in
    private fun finishMfaSignIn(cred: PhoneAuthCredential) {
        val resolver = mfaResolver ?: run {
            _mfaState.value = "MFA_ERROR: No resolver"
            return
        }
        val assertion = PhoneMultiFactorGenerator.getAssertion(cred)
        _mfaState.value = "MFA_VERIFYING"

        resolver.resolveSignIn(assertion)
            .addOnSuccessListener {
                mfaResolver = null
                verificationId = null
                _mfaState.value = "MFA_SUCCESS"
                _authState.value = AuthState.Authenticated
                loadUserProfile()
            }
            .addOnFailureListener { e ->
                _mfaState.value = "MFA_ERROR: ${e.message}"
            }
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

