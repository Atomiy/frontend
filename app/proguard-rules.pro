
# Keep all data model classes used by Gson for serialization/deserialization
-keep class com.dresscode.app.data.model.** { *; }
-keepattributes Signature

# Keep GSON specific annotations
-keepattributes *Annotation*

# Keep classes for Retrofit
-keep class retrofit2.** { *; }

# OkHttp
-keepattributes Signature
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# For coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.flow.** { *; }
-keepclassmembers class kotlinx.coroutines.flow.internal.FlowCoroutine {
    <methods>;
}
-keepclassmembers class **$*COROUTINE$* {
    <fields>;
    <methods>;
}
-keepclassmembers class **$*जब$* {
    <fields>;
    <methods>;
}
-keepclassmembers class **$*र$* {
    <fields>;
    <methods>;
}
