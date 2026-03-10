package medlife.com.br.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import medlife.com.br.model.Usuario
import medlife.com.br.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _signInResult = MutableLiveData<Result<Boolean>>()
    val signInResult: LiveData<Result<Boolean>> = _signInResult

    private val _registerResult = MutableLiveData<Result<Boolean>>()
    val registerResult: LiveData<Result<Boolean>> = _registerResult

    fun getCurrentUser() = authRepository.getCurrentUser()

    fun signIn(email: String, senha: String) {
        viewModelScope.launch {
            _signInResult.value = authRepository.signInWithEmailAndPassword(email, senha)
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _signInResult.value = authRepository.signInWithGoogle(idToken)
        }
    }

    fun register(email: String, senha: String, usuario: Usuario) {
        viewModelScope.launch {
            _registerResult.value = authRepository.registerUser(email, senha, usuario)
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
