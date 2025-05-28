package com.dev.lokabudaya.pages.Auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> = _userData

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val user = auth.currentUser
        if (user == null) {
            _authState.value = AuthState.Unauthenticated
            _userData.value = null
        } else {
            user.reload().addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    if (user.isEmailVerified) {
                        _authState.value = AuthState.Authenticated
                        fetchUserData()
                    } else {
                        _authState.value = AuthState.EmailNotVerified
                    }
                } else {
                    if (user.isEmailVerified) {
                        _authState.value = AuthState.Authenticated
                        fetchUserData()
                    } else {
                        _authState.value = AuthState.EmailNotVerified
                    }
                }
            }
        }
    }

    fun fetchUserData() {
        val user = auth.currentUser
        user?.let { firebaseUser ->
            firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userData = UserData(
                            uid = document.getString("uid") ?: "",
                            email = document.getString("email") ?: "",
                            username = document.getString("username") ?: "",
                            displayName = firebaseUser.displayName ?: document.getString("username") ?: "",
                            isEmailVerified = document.getBoolean("isEmailVerified") ?: false,
                            profile = document.get("profile") as? Map<String, Any> ?: emptyMap()
                        )
                        _userData.value = userData
                    }
                }
        }
    }

    fun loginWithUsername(username: String, password: String) {
        if(username.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Username or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading

        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDoc = documents.documents[0]
                    val email = userDoc.getString("email") ?: ""

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user?.isEmailVerified == true) {
                                    _authState.value = AuthState.Authenticated
                                } else {
                                    _authState.value = AuthState.EmailNotVerified
                                }
                            } else {
                                _authState.value = AuthState.Error(task.exception?.message ?: "Login failed")
                            }
                        }
                } else {
                    _authState.value = AuthState.Error("Username not found")
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error("Login failed: ${exception.message}")
            }
    }

    fun login(email: String, password: String) {
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String, username: String) {
        if(email.isEmpty() || password.isEmpty() || username.isEmpty()){
            _authState.value = AuthState.Error("Email, password, and username can't be empty")
            return
        }
        _authState.value = AuthState.Loading

        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    _authState.value = AuthState.Error("Username already exists")
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                user?.let { firebaseUser ->
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build()

                                    firebaseUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener { profileTask ->
                                            if (profileTask.isSuccessful) {
                                                val userDoc = hashMapOf<String, Any>(
                                                    "uid" to firebaseUser.uid,
                                                    "email" to email,
                                                    "username" to username,
                                                    "isEmailVerified" to false,
                                                    "createdAt" to FieldValue.serverTimestamp(),
                                                    "profile" to hashMapOf<String, Any>(
                                                        "displayname" to username,
                                                        "username" to username,
                                                        "phonenumber" to ""
                                                    )
                                                )

                                                firestore.collection("users")
                                                    .document(firebaseUser.uid)
                                                    .set(userDoc)
                                                    .addOnSuccessListener {
                                                        sendEmailVerification()
                                                    }
                                                    .addOnFailureListener { exception ->
                                                        _authState.value = AuthState.Error("Failed to save user data: ${exception.message}")
                                                    }
                                            } else {
                                                _authState.value = AuthState.Error("Failed to update profile")
                                            }
                                        }
                                }
                            } else {
                                _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error("Failed to check username: ${exception.message}")
            }
    }

    fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.EmailVerificationSent
                } else {
                    _authState.value = AuthState.Error("Failed to send verification email: ${task.exception?.message}")
                }
            }
    }

    fun checkEmailVerification() {
        val user = auth.currentUser
        user?.let { firebaseUser ->
            _authState.value = AuthState.Loading

            firebaseUser.getIdToken(true)
                .addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        firebaseUser.reload().addOnCompleteListener { reloadTask ->
                            if (reloadTask.isSuccessful) {
                                if (firebaseUser.isEmailVerified) {
                                    firestore.collection("users")
                                        .document(firebaseUser.uid)
                                        .update("isEmailVerified", true)
                                        .addOnSuccessListener {
                                            _authState.value = AuthState.Authenticated
                                        }
                                        .addOnFailureListener {
                                            _authState.value = AuthState.Authenticated
                                        }
                                } else {
                                    _authState.value = AuthState.EmailNotVerified
                                }
                            } else {
                                _authState.value = AuthState.Error("Failed to check verification status")
                            }
                        }
                    } else {
                        _authState.value = AuthState.Error("Failed to refresh token")
                    }
                }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let { firebaseUser ->
                        firestore.collection("users")
                            .document(firebaseUser.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (!document.exists()) {
                                    val displayName = firebaseUser.displayName ?: "User"
                                    val email = firebaseUser.email ?: ""

                                    val userDoc = hashMapOf(
                                        "uid" to firebaseUser.uid,
                                        "email" to email,
                                        "username" to displayName,
                                        "isEmailVerified" to firebaseUser.isEmailVerified,
                                        "createdAt" to FieldValue.serverTimestamp(),
                                        "profile" to hashMapOf(
                                            "displayname" to displayName,
                                            "username" to displayName,
                                            "phonenumber" to ""
                                        )
                                    )

                                    firestore.collection("users")
                                        .document(firebaseUser.uid)
                                        .set(userDoc)
                                        .addOnSuccessListener {
                                            _authState.value = AuthState.Authenticated
                                        }
                                        .addOnFailureListener {
                                            _authState.value = AuthState.Authenticated
                                        }
                                } else {
                                    _authState.value = AuthState.Authenticated
                                }
                            }
                            .addOnFailureListener {
                                _authState.value = AuthState.Authenticated
                            }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Google sign-in failed")
                }
            }
    }

    fun signout(context: Context) {
        auth.signOut()

        val credentialManager = CredentialManager.create(context)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (_: Exception) {
            }
        }

        _authState.value = AuthState.Unauthenticated
    }

    fun getUserData(callback: (UserData?) -> Unit) {
        val user = auth.currentUser
        user?.let { firebaseUser ->
            firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Ambil displayName dari profile atau document langsung
                        val profile = document.get("profile") as? Map<String, Any> ?: emptyMap()
                        val displayName = profile["displayname"] as? String
                            ?: document.getString("displayName")
                            ?: firebaseUser.displayName
                            ?: document.getString("username")
                            ?: ""

                        val userData = UserData(
                            uid = document.getString("uid") ?: "",
                            email = document.getString("email") ?: "",
                            username = document.getString("username") ?: "",
                            displayName = displayName,
                            isEmailVerified = document.getBoolean("isEmailVerified") ?: false,
                            profile = profile
                        )
                        callback(userData)
                    } else {
                        callback(null)
                    }
                }
                .addOnFailureListener {
                    callback(null)
                }
        }
    }
}

data class UserData(
    val uid: String,
    val email: String,
    val username: String,
    val displayName: String,
    val isEmailVerified: Boolean,
    val profile: Map<String, Any>
)

sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    object EmailVerificationSent : AuthState()
    object EmailNotVerified : AuthState()
    data class Error(val message : String) : AuthState()
}


//    fun signInWithFacebook(token: String) {
//        _authState.value = AuthState.Loading
//        val credential = FacebookAuthProvider.getCredential(token)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    _authState.value = AuthState.Authenticated
//                } else {
//                    _authState.value = AuthState.Error(task.exception?.message ?: "Facebook sign-in failed")
//                }
//            }
//    }