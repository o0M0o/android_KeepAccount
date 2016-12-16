# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\123\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-dontwarn com.j256.ormlite.**
-keep class com.j256.ormlite.** {
    *;
}

-dontwarn org.junit.experimental.**
-keep class org.junit.experimental.** {
    *;
}

-dontwarn andriod.net.http.**
-keep class andriod.net.http.** {
    *;
}

-dontwarn de.mindpipe.android.**
-keep class de.mindpipe.android.** {
    *;
}

-dontwarn org.apache.http.**
-keep class org.apache.http.** {
    *;
}