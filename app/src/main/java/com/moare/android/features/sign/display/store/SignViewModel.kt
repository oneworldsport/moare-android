package com.moare.android.features.sign.display.store

import android.util.Patterns
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moare.android.core.networking.ApiHttpError
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.sign.models.AuthErrorCode
import com.moare.android.features.sign.models.AuthMethod
import com.moare.android.features.sign.models.AuthTokenResponse
import com.moare.android.features.sign.models.ConfirmAuthRequest
import com.moare.android.features.sign.models.SignUpCompleteRequest
import com.moare.android.features.sign.models.SignUpInitiateRequest
import com.moare.android.features.sign.models.SignUpVerificationRequest
import com.moare.android.features.sign.models.StartAuthRequest
import com.moare.android.features.sign.models.UserHandleReserveRequest
import com.moare.android.features.sign.models.UserProfileCreateRequest
import com.moare.android.features.sign.networking.SignClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SignAction {
    data class SetFullWidth(val fullWidth: Dp) : SignAction
    data class UpdateSignFlow(val signFlow: SignFlow) : SignAction
    data class SelectIdType(val index: Int) : SignAction
    data class UpdateText(val text: String) : SignAction
    data class AddSport(val sport: String) : SignAction
    data object Submit : SignAction
    data object CheckUserHandle : SignAction
}

enum class SignFlow {
    LOGIN_ID, LOGIN_OTP, SIGN_UP_ID, SIGN_UP_OTP, SIGN_UP_USER_HANDLE, SIGN_UP_SPORTS_INTERESTS, SIGN_UP_SUCCESS
}

// SignView에 '제출 버튼'과 '하단 프로그레스바'의 활성화된 상태
enum class SignActivatedState {
    ALL_ACTIVATED, ONLY_BUTTON_ACTIVATED, ALL_DEACTIVATED, ONLY_BAR_ACTIVATED
}

@HiltViewModel
class SignViewModel @Inject constructor(
    private val signClient: SignClient,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _currentFlow = MutableStateFlow(SignFlow.LOGIN_ID)
    val currentFlow: StateFlow<SignFlow> = _currentFlow

    private val _idType = MutableStateFlow(AuthMethod.EMAIL)
    val idType: StateFlow<AuthMethod> = _idType

    private val _idTypeSelectedIndex = MutableStateFlow(0)
    val idTypeSelectedIndex: StateFlow<Int> = _idTypeSelectedIndex

    private val _title = MutableStateFlow("로그인")
    val title: StateFlow<String> = _title

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private val _placeholder = MutableStateFlow(" 이메일 입력")
    val placeholder: StateFlow<String> = _placeholder

    private val _submitBtnLabel = MutableStateFlow("코드 전송")
    val submitBtnLabel: StateFlow<String> = _submitBtnLabel

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isTextFieldEnabled = MutableStateFlow(true)
    val isTextFieldEnabled: StateFlow<Boolean> = _isTextFieldEnabled

    private val _activatedState = MutableStateFlow(SignActivatedState.ALL_DEACTIVATED)
    val activatedState: StateFlow<SignActivatedState> = _activatedState

    private val _barAlignment = MutableStateFlow(Alignment.Start)
    val barAlignment: StateFlow<Alignment.Horizontal> = _barAlignment

    private val _barWidth = MutableStateFlow(20.dp)
    val barWidth: StateFlow<Dp> = _barWidth

    private val _barDuration = MutableStateFlow(500)
    val barDuration: StateFlow<Int> = _barDuration

    private val _apiFetchState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val apiFetchState: StateFlow<ApiFetchState> = _apiFetchState

    private var fullWidth = 0.dp
    private var isFirstRequest = true // barAlignment 설정을 바꿀때 사용

    private var id = ""
    private var session: String? = null
    private var otp = ""
    private var userHandle: String? = null
    private val _sportsInterests = MutableStateFlow<List<String>?>(null)
    val sportsInterests: StateFlow<List<String>?> = _sportsInterests

    private var checkJob: Job? = null

    fun send(action: SignAction) {
        when (action) {
            is SignAction.SetFullWidth -> fullWidth = action.fullWidth
            is SignAction.UpdateSignFlow -> updateSignFlow(action.signFlow)
            is SignAction.SelectIdType -> selectIdType(action.index)
            is SignAction.UpdateText -> updateText(action.text)
            is SignAction.AddSport -> addSport(action.sport)
            is SignAction.Submit -> submit()
            is SignAction.CheckUserHandle -> checkUserHandle()
        }
    }

    private fun updateSignFlow(signFlow: SignFlow) {
        _currentFlow.value = signFlow
        _activatedState.value = SignActivatedState.ALL_DEACTIVATED
        _apiFetchState.value = ApiFetchState.Success
        isFirstRequest = true

        when (signFlow) {
            SignFlow.LOGIN_ID -> {
                _title.value = "로그인"
                _submitBtnLabel.value = "코드 전송"

                selectIdType(0)
                return
            }
            SignFlow.LOGIN_OTP -> {
                _title.value = "코드 인증"
                _placeholder.value = " 인증 코드"
                _submitBtnLabel.value = "확인"
            }
            SignFlow.SIGN_UP_ID -> {
                _title.value = "회원가입"
                _submitBtnLabel.value = "코드 전송"

                id = ""
                session = null
                otp = ""

                selectIdType(0)
                return
            }
            SignFlow.SIGN_UP_OTP -> {
                _title.value = "코드 인증"
                _placeholder.value = " 인증 코드"
                _submitBtnLabel.value = "확인"
            }
            SignFlow.SIGN_UP_USER_HANDLE -> {
                _title.value = "사용자 이름"
                _placeholder.value = " 사용자 이름 입력"
                _submitBtnLabel.value = "다음"
            }
            SignFlow.SIGN_UP_SPORTS_INTERESTS -> {
                _title.value = "스포츠 선택"
                _submitBtnLabel.value = "선택 완료"
            }
            SignFlow.SIGN_UP_SUCCESS -> {
            }
        }

        updateText("")
    }

    private fun selectIdType(index: Int) {
        _idTypeSelectedIndex.value = index

        if (index == 0) {
            _idType.value = AuthMethod.EMAIL
            _placeholder.value = " 이메일 입력"
        } else {
            _idType.value = AuthMethod.PHONE_NUMBER
            _placeholder.value = " 전화번호 입력"
        }

        updateText("")
    }

    private fun updateText(text: String) {
        // '코드 재전송'의 경우에는 아래 로직을 타면 안됨. 개선 필요할듯..?
        if (!isTextFieldEnabled.value) {
            return
        }

        _text.value = text
        _errorMessage.value = ""

        when (currentFlow.value) {
            SignFlow.LOGIN_ID, SignFlow.SIGN_UP_ID -> {
                if (idType.value == AuthMethod.EMAIL) {
                    if (Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                        _activatedState.value = SignActivatedState.ALL_ACTIVATED
                    } else {
                        _activatedState.value = SignActivatedState.ALL_DEACTIVATED
                    }
                } else {
                    if (Patterns.PHONE.matcher(text).matches()) {
                        _activatedState.value = SignActivatedState.ALL_ACTIVATED
                    } else {
                        _activatedState.value = SignActivatedState.ALL_DEACTIVATED
                    }
                }
            }

            SignFlow.LOGIN_OTP, SignFlow.SIGN_UP_OTP -> {
                if (text.length == 6) {
                    _activatedState.value = SignActivatedState.ALL_ACTIVATED
                } else {
                    _activatedState.value = SignActivatedState.ALL_DEACTIVATED
                }
            }

            SignFlow.SIGN_UP_USER_HANDLE -> {
                _activatedState.value = SignActivatedState.ALL_DEACTIVATED

                checkJob?.cancel()
                // TODO: 유효성 검사 필요
                if (text.isBlank()) {
                    updateBarState()
                    return
                }

                checkJob = viewModelScope.launch {
                    updateBarState()
                    // text가 바뀌면 2초 후 닉네임 중복 검사 api(checkUserHandle()) 호출.
                    // 2초 이내에 또 text가 바뀌면 이전 실행 취소하고 새로 실행.
                    delay(2000)
                    checkUserHandle()
                }
            }
            else -> {}
        }

        updateBarState()
    }

    private fun addSport(sport: String) {
        if (sportsInterests.value.isNullOrEmpty()) {
            _sportsInterests.value = mutableListOf(sport)
            _activatedState.value = SignActivatedState.ALL_ACTIVATED

            updateBarState()
            return
        } else {
            if (sportsInterests.value?.contains(sport) == true) {
                _sportsInterests.update {
                    it?.let {
                        it - sport
                    }
                }

                if (sportsInterests.value?.isEmpty() == true) {
                    _activatedState.value = SignActivatedState.ALL_DEACTIVATED

                    updateBarState()
                    return
                }
            } else {
                _sportsInterests.update {
                    it?.let {
                        it + sport
                    }
                }
            }
        }
    }

    private fun submit() {
        _errorMessage.value = ""

        when (currentFlow.value) {
            SignFlow.LOGIN_ID -> {
                sendLoginOtp()
            }
            SignFlow.LOGIN_OTP -> {
                if (isTextFieldEnabled.value) {
                    confirmLoginOtp()
                } else {
                    sendLoginOtp()
                }
            }
            SignFlow.SIGN_UP_ID -> {
                sendSignUpOtp()
            }
            SignFlow.SIGN_UP_OTP -> {
                if (isTextFieldEnabled.value) {
                    confirmSignUpOtp()
                } else {
                    sendSignUpOtp()
                }
            }
            SignFlow.SIGN_UP_USER_HANDLE -> {
                reserveUserHandle()
            }
            SignFlow.SIGN_UP_SPORTS_INTERESTS -> {
                completeSignUp()
            }
            SignFlow.SIGN_UP_SUCCESS -> {
            }
        }
    }

    private fun sendLoginOtp() {
        if (currentFlow.value == SignFlow.LOGIN_ID) {
            id = text.value
        }

        _apiFetchState.value = ApiFetchState.Fetching
        _activatedState.value = SignActivatedState.ALL_DEACTIVATED

        updateBarState()

        viewModelScope.launch {
            delay(3000)

            try {
                val body = StartAuthRequest(loginId = id, method = idType.value)
                val result = signClient.startLoginAuth(body)

                session = result.session
                _isTextFieldEnabled.value = true

                updateSignFlow(SignFlow.LOGIN_OTP)
            } catch (e: Exception) {
                if (e is ApiHttpError) {
                    responseFailure(e)
                }
            }
        }
    }

    private fun confirmLoginOtp() {
        // TODO: session null일때 오류 처리 필요
        val session = session ?: return

        otp = text.value

        _apiFetchState.value = ApiFetchState.Fetching
        _activatedState.value = SignActivatedState.ALL_DEACTIVATED

        updateBarState()

        viewModelScope.launch {
            delay(3000)

            try {
                val body = ConfirmAuthRequest(id, otp, session)
                val result = signClient.confirmLoginAuth(body)

                // 로그인 성공 후 MoatView를 보여준다
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("idToken")] = result.idToken
                    preferences[stringPreferencesKey("accessToken")] = result.accessToken
                    preferences[stringPreferencesKey("refreshToken")] = result.refreshToken
                }
            } catch (e: Exception) {
                if (e is ApiHttpError) {
                    responseFailure(e)
                }
            }
        }
    }

    private fun sendSignUpOtp() {
        if (currentFlow.value == SignFlow.SIGN_UP_ID) {
            id = text.value
        }

        _apiFetchState.value = ApiFetchState.Fetching
        _activatedState.value = SignActivatedState.ALL_DEACTIVATED

        updateBarState()

        viewModelScope.launch {
            delay(3000)

            try {
                val body = SignUpInitiateRequest(id, idType.value)
                val result = signClient.initiateSignUp(body)

                _isTextFieldEnabled.value = true

                updateSignFlow(SignFlow.SIGN_UP_OTP)
            } catch (e: Exception) {
                if (e is ApiHttpError) {
                    responseFailure(e)
                }
            }
        }
    }

    private fun confirmSignUpOtp() {
        otp = text.value

        _apiFetchState.value = ApiFetchState.Fetching
        _activatedState.value = SignActivatedState.ALL_DEACTIVATED

        updateBarState()

        viewModelScope.launch {
            delay(3000)

            try {
                val body = SignUpVerificationRequest(id, otp)
                val result = signClient.verifySignUpOtp(body)

                updateSignFlow(SignFlow.SIGN_UP_USER_HANDLE)
            } catch (e: Exception) {
                if (e is ApiHttpError) {
                    responseFailure(e)
                }
            }
        }
    }

    private fun checkUserHandle() {
        userHandle = text.value

        _apiFetchState.value = ApiFetchState.Fetching
        _activatedState.value = SignActivatedState.ONLY_BAR_ACTIVATED
        _isTextFieldEnabled.value = false // 사용 가능한 userHandle인지 체크하는 동안에는 TextField를 비활성화

        if (userHandle.isNullOrBlank()) {
            // TODO: 오류 처리 필요
            return
        }

        updateBarState()

        viewModelScope.launch {
            delay(3000)

            try {
                val result = signClient.checkUserHandle(userHandle!!)

                _isTextFieldEnabled.value = true

                if (result.success) {
                    _apiFetchState.value = ApiFetchState.Success
                    _activatedState.value = SignActivatedState.ALL_ACTIVATED
                } else {
                    _apiFetchState.value = ApiFetchState.Error("")
                    _activatedState.value = SignActivatedState.ALL_DEACTIVATED
                    _errorMessage.value = "이미 사용중인 사용자 이름입니다."
                }

                updateBarState()
            } catch (e: Exception) {
                if (e is ApiHttpError) {
                    responseFailure(e)
                }
            }
        }
    }

    private fun reserveUserHandle() {
        if (userHandle.isNullOrBlank()) {
            // TODO: 오류 처리 필요
            return
        }

        _apiFetchState.value = ApiFetchState.Fetching
        _activatedState.value = SignActivatedState.ALL_DEACTIVATED

        updateBarState()

        viewModelScope.launch {
            delay(3000)

            try {
                val body = UserHandleReserveRequest(userHandle!!)
                val result = signClient.reserveUserHandle(body)

                updateSignFlow(SignFlow.SIGN_UP_SPORTS_INTERESTS)
            } catch (e: Exception) {
                if (e is ApiHttpError) {
                    responseFailure(e)
                }
            }
        }
    }

    private fun completeSignUp() {
        if (userHandle.isNullOrBlank() || sportsInterests.value.isNullOrEmpty()) {
            // TODO: 오류 처리 필요
            return
        }

        _apiFetchState.value = ApiFetchState.Fetching
        _activatedState.value = SignActivatedState.ALL_DEACTIVATED

        updateBarState()

        viewModelScope.launch {
            delay(3000)

            try {
                val body = SignUpCompleteRequest(
                    id = id,
                    method = idType.value,
                    profile = UserProfileCreateRequest(
                        userHandle = userHandle!!,
                        sportsInterests = sportsInterests.value!!
                    )
                )
                val result = signClient.completeSignUp(body)

                // 회원가입 성공 후 자동 로그인 (MoatView를 보여준다)
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("idToken")] = result.idToken
                    preferences[stringPreferencesKey("accessToken")] = result.accessToken
                    preferences[stringPreferencesKey("refreshToken")] = result.refreshToken
                }
            } catch (e: Exception) {
                if (e is ApiHttpError) {
                    responseFailure(e)
                }
            }
        }
    }

    private fun responseFailure(e: ApiHttpError) {
        _apiFetchState.value = ApiFetchState.Error("")

        var authErrorCode = AuthErrorCode.UNKNOWN
        e.apiCode?.let { apiCode ->
            e.message?.let { message ->
                authErrorCode = AuthErrorCode.fromCode(apiCode)
                _errorMessage.value = message
            }
        }

        when (currentFlow.value) {
            SignFlow.LOGIN_ID -> {
                if (authErrorCode == AuthErrorCode.USER_NOT_FOUND) {
                    _activatedState.value = SignActivatedState.ALL_ACTIVATED
                }
            }
            SignFlow.LOGIN_OTP -> {
                if (authErrorCode == AuthErrorCode.OTP_INVALID) {
                    e.details?.get("session")?.let {
                        session = it
                    }

                    _submitBtnLabel.value = "확인"
                    _activatedState.value = SignActivatedState.ALL_ACTIVATED
                } else if (authErrorCode == AuthErrorCode.OTP_EXPIRED || authErrorCode == AuthErrorCode.OTP_ATTEMPT_LIMIT_EXCEEDED) {
                    _submitBtnLabel.value = "코드 재전송"
                    _activatedState.value = SignActivatedState.ALL_ACTIVATED

                    _text.value = ""
                    _isTextFieldEnabled.value = false
                }
            }
            SignFlow.SIGN_UP_OTP -> {
                if (authErrorCode == AuthErrorCode.OTP_INVALID) {
                    _submitBtnLabel.value = "확인"
                    _activatedState.value = SignActivatedState.ALL_ACTIVATED
                } else if (authErrorCode == AuthErrorCode.OTP_EXPIRED || authErrorCode == AuthErrorCode.OTP_ATTEMPT_LIMIT_EXCEEDED) {
                    _submitBtnLabel.value = "코드 재전송"
                    _activatedState.value = SignActivatedState.ALL_ACTIVATED

                    _text.value = ""
                    _isTextFieldEnabled.value = false
                } else if (authErrorCode == AuthErrorCode.AUTH_SESSION_NOT_FOUND) {
                    _submitBtnLabel.value = "돌아가기"
                    _activatedState.value = SignActivatedState.ALL_DEACTIVATED

                    _text.value = ""
                }
            }
            SignFlow.SIGN_UP_USER_HANDLE -> {
                _isTextFieldEnabled.value = true

                if (authErrorCode == AuthErrorCode.USER_HANDLE_ALREADY_EXISTS) {
                    _activatedState.value = SignActivatedState.ALL_DEACTIVATED
                }
            }
            else -> {}
        }

        updateBarState()
    }

    private fun updateBarState() {
        if (apiFetchState.value == ApiFetchState.Fetching) {
            if (isFirstRequest && activatedState.value != SignActivatedState.ONLY_BAR_ACTIVATED) {
                isFirstRequest = false
                if (barAlignment.value == Alignment.Start) {
                    _barAlignment.value = Alignment.End
                } else {
                    _barAlignment.value = Alignment.Start
                }
            }

            _barDuration.value = 10000
        } else {
            _barDuration.value = 500
        }

        when (activatedState.value) {
            SignActivatedState.ALL_ACTIVATED, SignActivatedState.ONLY_BAR_ACTIVATED -> {
                // NOTE: 같은 크기로 _barWidth를 바꾸면 animation이 trigger가 안됨
                if (barWidth.value == fullWidth) {
                    _barWidth.value = fullWidth - 0.1.dp
                } else {
                    _barWidth.value = fullWidth
                }
            }
            SignActivatedState.ONLY_BUTTON_ACTIVATED, SignActivatedState.ALL_DEACTIVATED -> {
                if (barWidth.value == 20.dp) {
                    _barWidth.value = 20.1.dp
                } else {
                    _barWidth.value = 20.dp
                }
            }
        }
    }
}
























