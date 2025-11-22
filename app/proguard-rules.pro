# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep DreamService classes
-keep class com.matrixscreensaver.MatrixDreamService { *; }
-keep class com.matrixscreensaver.MainActivity { *; }

# Keep all Kotlin metadata
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }

