package com.moare.android.features.sign.display.signin.viewmodel

import android.util.Patterns
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.sign.models.AuthMethod
import com.moare.android.features.sign.models.AuthResponseType
import com.moare.android.features.sign.models.AuthSessionResponse
import com.moare.android.features.sign.models.AuthTokenData
import com.moare.android.features.sign.models.ConfirmAuthRequest
import com.moare.android.features.sign.models.SignUpCompleteRequest
import com.moare.android.features.sign.models.SignUpInitiateRequest
import com.moare.android.features.sign.models.SignUpVerificationRequest
import com.moare.android.features.sign.models.StartAuthRequest
import com.moare.android.features.sign.models.UserProfileCreateRequest
import com.moare.android.features.sign.networking.SignClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignIntent {
    data class SelectIdType(val index: Int) : SignIntent()
    data class UpdateText(val text: String) : SignIntent()
    data object Submit : SignIntent()
    data class UpdateSignFlow(val signFlow: SignFlow) : SignIntent()
    data object CheckNickname : SignIntent()
}

enum class SignFlow {
    LOGIN_ID, LOGIN_OTP, LOGIN_OTP_RETRY, LOGIN_OTP_EXPIRED, LOGIN_OTP_LIMIT_EXCEEDED, LOGIN_SUCCESS,
    SIGN_UP_ID, SIGN_UP_OTP, SIGN_UP_OTP_RETRY, SIGN_UP_OTP_EXPIRED, SIGN_UP_NICKNAME, SIGN_UP_SPORTS_INTERESTS, SIGN_UP_SUCCESS
}

@HiltViewModel
class SignViewModel @Inject constructor(
    private val signClient: SignClient,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
//    val userProfile =
    private val _currentFlow = MutableStateFlow(SignFlow.LOGIN_ID)
    val currentFlow: StateFlow<SignFlow> = _currentFlow

    private val _title = MutableStateFlow("로그인")
    val title: StateFlow<String> = _title

    private val _idType = MutableStateFlow(AuthMethod.EMAIL)
    val idType: StateFlow<AuthMethod> = _idType

    private val _idTypeSelectedIndex = MutableStateFlow(0)
    val idTypeSelectedIndex: StateFlow<Int> = _idTypeSelectedIndex

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private val _placeholder = MutableStateFlow(" 이메일 입력")
    val placeholder: StateFlow<String> = _placeholder

    private val _submitBtnLabel = MutableStateFlow("코드 전송")
    val submitBtnLabel: StateFlow<String> = _submitBtnLabel

    private val _isValid = MutableStateFlow(false)
    val isValid: StateFlow<Boolean> = _isValid

    private val _errorText = MutableStateFlow("")
    val errorText: StateFlow<String> = _errorText

    private val _apiFetchState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val apiFetchState: StateFlow<ApiFetchState> = _apiFetchState

    private val _isCheckingNickname = MutableStateFlow(false)
    val isCheckingNickname: StateFlow<Boolean> = _isCheckingNickname

    private var id = ""
    private var session: String? = null
    private var otp = ""
    private var nickname: String? = null
    private var sportsInterests: List<String>? = null

    init {
        // TODO: 테스트용 임시 코드
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey("idToken"))
                preferences.remove(stringPreferencesKey("accessToken"))
                preferences.remove(stringPreferencesKey("refreshToken"))
            }
        }
    }

    fun send(intent: SignIntent) {
        viewModelScope.launch {
            when (intent) {
                is SignIntent.SelectIdType -> selectIdType(intent.index)
                is SignIntent.UpdateText -> updateText(intent.text)
                is SignIntent.Submit -> submit()
                is SignIntent.UpdateSignFlow -> updateSignFlow(intent.signFlow)
                is SignIntent.CheckNickname -> checkNickname()
            }
        }
    }

    private suspend fun selectIdType(index: Int) {
        _idTypeSelectedIndex.value = index

        if (index == 0) {
            _idType.value = AuthMethod.EMAIL
            _placeholder.value = " 이메일 입력"
        } else {
            _idType.value = AuthMethod.PHONE_NUMBER
            _placeholder.value = " 전화번호 입력"
        }

        _text.value = ""
        checkValidation()
    }

    private suspend fun updateText(text: String) {
        _text.value = text
        checkValidation()
    }

    private suspend fun checkValidation() {
        when (currentFlow.value) {
            SignFlow.LOGIN_ID, SignFlow.SIGN_UP_ID -> {
                if (idType.value == AuthMethod.EMAIL) {
                    _isValid.value = Patterns.EMAIL_ADDRESS.matcher(text.value).matches()
                } else {
                    _isValid.value = Patterns.PHONE.matcher(text.value).matches()
                }
            }
            SignFlow.LOGIN_OTP, SignFlow.LOGIN_OTP_RETRY, SignFlow.SIGN_UP_OTP, SignFlow.SIGN_UP_OTP_RETRY -> {
                _isValid.value = text.value.length == 6
            }
            SignFlow.LOGIN_OTP_EXPIRED -> {
            }
            SignFlow.LOGIN_OTP_LIMIT_EXCEEDED -> {
            }
            SignFlow.LOGIN_SUCCESS -> {
            }
            SignFlow.SIGN_UP_OTP_EXPIRED -> {

            }
            SignFlow.SIGN_UP_NICKNAME -> {
                delay(2000)
                checkNickname()
            }
            SignFlow.SIGN_UP_SPORTS_INTERESTS -> {

            }
            SignFlow.SIGN_UP_SUCCESS -> {
            }
        }
    }

    private suspend fun submit() {
        when (currentFlow.value) {
            SignFlow.LOGIN_ID, SignFlow.LOGIN_OTP_EXPIRED, SignFlow.LOGIN_OTP_LIMIT_EXCEEDED -> {
                if (isValid.value) {
                    sendLoginOtp()
                }
            }
            SignFlow.LOGIN_OTP, SignFlow.LOGIN_OTP_RETRY -> {
                if (isValid.value) {
                    confirmLoginOtp()
                }
            }
            SignFlow.LOGIN_SUCCESS -> {
            }
            SignFlow.SIGN_UP_ID, SignFlow.SIGN_UP_OTP_EXPIRED -> {
                if (isValid.value) {
                    sendSignUpOtp()
                }
            }
            SignFlow.SIGN_UP_OTP, SignFlow.SIGN_UP_OTP_RETRY -> {
                if (isValid.value) {
                    confirmSignUpOtp()
                }
            }
            SignFlow.SIGN_UP_NICKNAME -> {
                if (isValid.value) {
                    reserveNickname()
                }
            }
            SignFlow.SIGN_UP_SPORTS_INTERESTS -> {
                if (isValid.value) {
                    completeSignUp()
                }
            }
            SignFlow.SIGN_UP_SUCCESS -> {
            }
        }
    }

    // login
    private suspend fun sendLoginOtp() {
        id = id.ifEmpty { text.value }

        val body = StartAuthRequest(
            id = id,
            method = idType.value
        )

        // start loading
        _apiFetchState.value = ApiFetchState.Fetching
        val result = signClient.startLoginAuth(body)

        session = result?.session

        updateSignFlow(SignFlow.LOGIN_OTP)
    }

    private suspend fun confirmLoginOtp() {
        otp = text.value

        session?.let {
            val body = ConfirmAuthRequest(
                id = id,
                otp = otp,
                session = it
            )

            // start loading
            _apiFetchState.value = ApiFetchState.Fetching
            val result = signClient.confirmLoginAuth(body)

            if (result is AuthTokenData) {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("idToken")] = result.idToken
                    preferences[stringPreferencesKey("accessToken")] = result.accessToken
                    preferences[stringPreferencesKey("refreshToken")] = result.refreshToken
                }

                updateSignFlow(SignFlow.LOGIN_SUCCESS)
            } else if (result is AuthSessionResponse) {
                session = result.session

                updateSignFlow(SignFlow.LOGIN_OTP_RETRY)
            } else if (result == AuthResponseType.EXPIRED) {
                updateSignFlow(SignFlow.LOGIN_OTP_EXPIRED)
            } else if (result == AuthResponseType.LIMIT_EXCEEDED) {
                updateSignFlow(SignFlow.LOGIN_OTP_LIMIT_EXCEEDED)
            }
        }
    }

    // signup
    private suspend fun sendSignUpOtp() {
        id = id.ifEmpty { text.value }

        val body = SignUpInitiateRequest(
            id = id,
            method = idType.value
        )

        // start loading
        _apiFetchState.value = ApiFetchState.Fetching
        val result = signClient.initiateSignUp(body)

        // TODO: 실패했을경우 에러 메시지
        if (result?.success == true) {
            updateSignFlow(SignFlow.SIGN_UP_OTP)
        }
    }

    private suspend fun confirmSignUpOtp() {
        otp = text.value

        val body = SignUpVerificationRequest(
            id = id,
            otp = otp
        )

        // start loading
        _apiFetchState.value = ApiFetchState.Fetching
        val result = signClient.verifySignUpOtp(body)

        result?.type?.let { type ->
            when (type) {
                AuthResponseType.SUCCESS -> updateSignFlow(SignFlow.SIGN_UP_NICKNAME)
                AuthResponseType.RETRY -> updateSignFlow(SignFlow.SIGN_UP_OTP_RETRY)
                AuthResponseType.EXPIRED -> updateSignFlow(SignFlow.SIGN_UP_OTP_EXPIRED)
                else -> {}
            }
        }
    }

    private suspend fun checkNickname() {
        nickname = text.value
        _isCheckingNickname.value = true
        _isValid.value = false

        if (!nickname.isNullOrBlank()) {
            // start loading
            _apiFetchState.value = ApiFetchState.Fetching
            val result = signClient.checkNickname(nickname!!)

            if (result?.success == true) {
                _apiFetchState.value = ApiFetchState.Success
                _isValid.value = true
            } else if (result?.success == false) {
                _apiFetchState.value = ApiFetchState.Error("")
                _isValid.value = false // NOTE: isValid가 false인 상태에서 또 false넣어 LaunchedEffect(isValid)가 실행이 안됨.
                _errorText.value = "이미 사용 중인 닉네임입니다."
            }
        }

        delay(100)
        // STUDY: _isCheckingNickname와 다른 StateFlow(_apiFetchState)가 거의 동시에 실행되고,
        // 이에 의해 Compose가 recomposition될때 한 프레임 내에서 함께 반영될 수 있다.
        // 이때문에 LaunchedEffect(apiFetchState)에서 isCheckingNickname의 상태가 코드 순서대로 반영이 안되어서, delay를 줌.
        // 다음 checkNickname()이 실행되기 까지 걸리는 최소 시간 까지는 delay를 줘도 문제가 안됨.
        _isCheckingNickname.value = false
    }

    private suspend fun reserveNickname() {
        if (!nickname.isNullOrBlank()) {
            // start loading
            _apiFetchState.value = ApiFetchState.Fetching
            val result = signClient.reserveNickname(nickname!!)

            if (result?.success == true) {
                updateSignFlow(SignFlow.SIGN_UP_SPORTS_INTERESTS)
            }
        }
    }

    private suspend fun completeSignUp() {
//        sportsInterests =

        val body = SignUpCompleteRequest(
            id = id,
            method = idType.value,
            profile = UserProfileCreateRequest(
                nickname = nickname ?: "test"
            )
        )

        // start loading
        _apiFetchState.value = ApiFetchState.Fetching
        val result = signClient.completeSignUp(body)

        if (result?.success == true) {
            updateSignFlow(SignFlow.SIGN_UP_SUCCESS)
        }
    }

    private suspend fun updateSignFlow(signFlow: SignFlow) {
        _currentFlow.value = signFlow
        _text.value = ""
        _isValid.value = false

        when (signFlow) {
            SignFlow.LOGIN_ID -> {
                _apiFetchState.value = ApiFetchState.Success
                _title.value = "로그인"
                _placeholder.value = " 이메일 입력"
                _submitBtnLabel.value = "코드 전송"
            }
            SignFlow.LOGIN_OTP -> {
                _apiFetchState.value = ApiFetchState.Success
                _title.value = "코드 인증"
                _placeholder.value = " 인증 코드"
                _submitBtnLabel.value = "확인"
            }
            SignFlow.LOGIN_OTP_RETRY -> {
                _apiFetchState.value = ApiFetchState.Error("")
                _submitBtnLabel.value = "확인"
                _errorText.value = "코드가 틀렸습니다. 다시 시도해 주세요."
            }
            SignFlow.LOGIN_OTP_EXPIRED -> {
                _apiFetchState.value = ApiFetchState.Error("")
                _placeholder.value = ""
                _submitBtnLabel.value = "코드 재전송"
                _isValid.value = true
                _errorText.value = "코드가 만료되었습니다. 코드를 재전송해 주세요."
            }
            SignFlow.LOGIN_OTP_LIMIT_EXCEEDED -> {
                _apiFetchState.value = ApiFetchState.Error("")
                _placeholder.value = ""
                _submitBtnLabel.value = "코드 재전송"
                _isValid.value = true
                _errorText.value = "코드 인증 시도 횟수를 초과하였습니다. 코드를 재전송해 주세요."
            }
            SignFlow.LOGIN_SUCCESS -> {
                _apiFetchState.value = ApiFetchState.Success
            }
            SignFlow.SIGN_UP_ID -> {
                _apiFetchState.value = ApiFetchState.Success
                selectIdType(0)

                id = ""
                session = null
                otp = ""

                _title.value = "회원가입"
                _submitBtnLabel.value = "코드 전송"
            }
            SignFlow.SIGN_UP_OTP -> {
                _apiFetchState.value = ApiFetchState.Success
                _title.value = "코드 인증"
                _placeholder.value = " 인증 코드"
                _submitBtnLabel.value = "확인"
            }
            SignFlow.SIGN_UP_OTP_RETRY -> {
                _apiFetchState.value = ApiFetchState.Error("")
                _submitBtnLabel.value = "확인"
                _errorText.value = "코드가 틀렸습니다. 다시 시도해 주세요."
            }
            SignFlow.SIGN_UP_OTP_EXPIRED -> {
                _apiFetchState.value = ApiFetchState.Error("")
                _placeholder.value = ""
                _submitBtnLabel.value = "코드 재전송"
                _isValid.value = true
                _errorText.value = "코드가 만료되었습니다. 코드를 재전송해 주세요."
            }
            SignFlow.SIGN_UP_NICKNAME -> {
                _apiFetchState.value = ApiFetchState.Success
                _title.value = "닉네임"
                _placeholder.value = " 닉네임 입력"
                _submitBtnLabel.value = "다음"
            }
            SignFlow.SIGN_UP_SPORTS_INTERESTS -> {
                _apiFetchState.value = ApiFetchState.Success
                _title.value = "스포츠 선택"
                _submitBtnLabel.value = "선택 완료"
                _isValid.value = true
            }
            SignFlow.SIGN_UP_SUCCESS -> {
                _apiFetchState.value = ApiFetchState.Success
                _title.value = "가입 완료!"
            }
        }
    }
}