## 소개  |  [모아레](https://play.google.com/store/apps/details?id=com.moare.android&hl=ko)
구글 플레이에 출시되어 운영 중인 스포츠 정보 검색 및 데이터 탐색 Android 앱입니다.
사용자는 축구, NBA, KBO, MLB, 테니스 등 다양한 스포츠 리그의 선수·팀 정보, 경기 일정, 순위, 실시간 경기 기록, 토너먼트 대진표 등을 키워드 기반으로 검색하고 탐색할 수 있습니다.

기존 스포츠 정보 제공 서비스의 카테고리 중심 구조로 인해 원하는 정보를 찾기까지 여러 단계를 거쳐야 하는 불편함을 줄이고, 검색 중심의 간결한 탐색 경험을 제공하고자 만들었습니다.  

## 주요 기능
- [선수/팀/리그 + 스포츠 키워드 + 시즌] 검색어를 통한 통합 검색
- Trie 기반 자동완성 검색
- 인기 검색어 및 리그별 키워드를 통한 검색
- 축구, NBA, KBO, MLB, 테니스 등 다양한 리그 데이터 지원
- 선수·팀 정보 / 선수·팀 스탯 / 선수·팀 순위 화면 제공
- 리그 일정 / 선수·팀 일정 / 경기 상세 기록 / 토너먼트 대진표 화면 제공
- 새로고침을 통한 실시간 경기 데이터 제공

## 스크린샷
<img width="19%" alt="app_store_v1 0 9_1" src="https://github.com/user-attachments/assets/c97a36d5-2f2b-4f2f-8b97-3bfcabf7810a" />
<img width="19%" alt="app_store_v1 0 9_2" src="https://github.com/user-attachments/assets/19e12b80-07d1-479a-ad2e-546d6db0c10a" />
<img width="19%" alt="app_store_v1 0 9_3" src="https://github.com/user-attachments/assets/d4a06b4c-c349-48da-ad02-9aee27edf6fc" />
<img width="19%" alt="app_store_v1 0 9_4" src="https://github.com/user-attachments/assets/cee6d916-9fa2-494b-9f13-8916b4058d2a" />
<img width="19%" alt="app_store_v1 0 9_5" src="https://github.com/user-attachments/assets/bb5bb6b9-73f6-432d-8d24-ba0b65b8c793" />

## 기술 스택
- Kotlin
- Kotlin Coroutines, Flow, StateFlow
- Jetpack Compose
- MVI
- Hilt
- DataStore
- Retrofit, Kotlinx Serialization
- Firebase Analytics, AWS SDK
- JUnit4, MockK, Kotlinx Coroutines Test

## 구조
Jetpack Compose와 MVI 패턴을 기반으로 프로젝트를 구성하였으며,  
화면 UI, 상태 관리, 데이터 모델, 네트워크 통신 로직, 공통 화면 로직을 역할별로 분리했습니다.

- AppViewModel - 앱 전역 상태와 검색 화면 진입 흐름을 관리하며, 검색 결과에 따른 종목별 상세 화면 라우팅 처리

- SearchStore - 검색어 입력, 자동완성, 인기 검색어, 리그별 키워드, 검색 결과의 상태 및 액션 관리

- Feature Stores - 축구, NBA, KBO, MLB, 테니스 등 종목별 화면의 상태와 액션을 관리

- Base Stores - 정보, 일정, 스탯, 순위 등 유사한 화면에서 반복되는 상태 관리 로직을 공통화하여 재사용

- View - Jetpack Compose 기반 화면 구성 및 사용자 인터랙션 처리

- ViewContainer - 유사한 화면 구조를 공통화하여 종목별 UI 구현 중복을 줄이고 재사용성 관리

- DataModel - 다양한 형태의 스포츠 API 응답을 공통 데이터 모델로 수신하고, 종목과 데이터 타입에 따라 화면별 모델로 변환

- Repository - 검색, 키워드, 자동완성, 인기 검색어 등 데이터 요청 및 가공 로직을 추상화

- Network - Retrofit 기반으로 검색, 일정, 키워드, ID 기반 조회 등 스포츠 데이터 API 요청 처리

- Utilities - 자동완성 Trie, 날짜·시즌 계산, 공통 포맷팅 등 앱 전반에서 사용하는 유틸리티 관리

- Tests - JUnit4, MockK, Kotlinx Coroutines Test를 활용해 Store의 Action 처리, State 변경, 비동기 로직 흐름을 검증

## Unit Test
JUnit4, MockK, Kotlinx Coroutines Test를 활용해 주요 Store의 상태 변화와 비동기 로직 흐름을 검증

- Store의 Action 실행에 따른 State 변경 검증
- MockK를 사용해 테스트 환경에서 외부 의존성을 대체하고 네트워크 요청 없이 로직 검증
- Kotlinx Coroutines Test를 활용해 Coroutine 기반 비동기 처리 흐름 검증
- Mock 데이터를 활용해 API 응답 모델 변환 및 화면 표시 모델 생성 흐름 검증
- 검색, 일정, 상세 화면 등 주요 사용자 흐름에서 필요한 상태 처리 테스트

## 아키텍처 개선
Unit Test 적용 과정에서 테스트 가능한 구조를 만들기 위해 Repository 레이어를 도입하고, Data / Domain 계층 분리를 기반으로 Clean Architecture 방향의 구조 개선을 점진적으로 적용중
































