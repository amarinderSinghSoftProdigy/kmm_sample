# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

#-ignorewarnings

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes *Annotation*,SourceFile,LineNumberTable,InnerClasses

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Application classes that will be kept
-keep class com.zealsoftsol.medico.data.** { *; }

# JodaTime
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

# OkHttp
-keepattributes Signature
-keepattributes Annotation
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep public class com.google.firebase.iid.FirebaseInstanceId {
    public *;
}
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# KotlinX
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class app.chudo.**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class app.chudo.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class app.chudo.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}

# Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-keep class kotlinx.** { *; }