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

-dontwarn org.junit.**
-keep class org.junit.** {
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

-dontwarn com.squareup.**
-keep class com.squareup.** {
    *;
}

-dontwarn org.hamcrest.**
-keep class org.hamcrest.** {
    *;
}

-dontwarn org.sqlite.**
-keep class org.sqlite.** {
    *;
}

-dontwarn sun.misc.**
-keep class sun.misc.** {
    *;
}

-dontwarn java.nio.**
-keep class java.nio.** {
    *;
}

-dontwarn java.lang.**
-keep class java.lang.** {
    *;
}

-dontwarn junit.framework.**
-keep class junit.framework.** {
    *;
}

-dontwarn junit.runner.**
-keep class junit.runner.** {
    *;
}

-dontwarn org.codehaus.**
-keep class org.codehaus.** {
    *;
}

-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** {
    *;
}

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#-keep class android.support.v7.widget.** { *; }