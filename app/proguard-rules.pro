# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# AndroidX
#-keep class * extends androidx.lifecycle.ViewModel { *; }

#-keep class androidx.lifecycle.** {  *; }
#-keep class androidx.activity.** { *; }
#-keep class androidx.datastore.** { *; }

#-keepclassmembers class * {
#    @androidx.annotation.Keep *;
#}

# Dagger & Hilt
#-keep class dagger.** { *; }
#-keep class dagger.hilt.** { *; }
-keep class * { @dagger.hilt.android.lifecycle.HiltViewModel *; }

# Retrofit
#-keep class retrofit2.** { *; }
#-keep class okhttp3.** { *; }

# kotlinx.serialization
#-keep class kotlinx.serialization.** { *; }

# AWS SDK
#-keep class com.amazonaws.** { *; }

# Coil
#-keep class coil3.** { *; }
-dontwarn coil3.PlatformContext

#-keepattributes *Annotation*


#-keep class javax.inject.** { *; }
#-keepattributes RuntimeVisibleAnnotations
#-keep class * { @kotlinx.serialization.Serializable *; }
# AWS SDK 기본 유지
#-keep class com.amazonaws.** { *; }
#-keep class software.amazon.awssdk.** { *; }
#
## Cognito Identity Provider 예외 처리
#-keep class com.amazonaws.mobileconnectors.cognitoidentityprovider.** { *; }
#
## AWS S3 예외 처리
#-keep class com.amazonaws.services.s3.** { *; }
#
## AWS Translate 예외 처리
#-keep class com.amazonaws.services.translate.** { *; }
#
#-keep class androidx.compose.** { *; }
#-keep class androidx.constraintlayout.** { *; }
#-keep class * { @androidx.compose.runtime.Composable *; }