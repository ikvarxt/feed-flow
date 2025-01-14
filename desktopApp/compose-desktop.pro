# Okhttp
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Okio

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# Jsoup
#Jsoup

-keeppackagenames org.jsoup.nodes

-ignorewarnings

-keep class org.sqlite.** { *; }
-keep class org.sqlite.database.** { *; }

# Coroutines
# When editing this file, update the following files as well:
# - META-INF/com.android.tools/proguard/coroutines.pro
# - META-INF/com.android.tools/r8/coroutines.pro

# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Same story for the standard library's SafeContinuation that also uses AtomicReferenceFieldUpdater
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# These classes are only required by kotlinx.coroutines.debug.AgentPremain, which is only loaded when
# kotlinx-coroutines-core is used as a Java agent, so these are not needed in contexts where ProGuard is used.
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
-dontwarn java.lang.instrument.Instrumentation
-dontwarn sun.misc.Signal

# Only used in `kotlinx.coroutines.internal.ExceptionsConstructor`.
# The case when it is not available is hidden in a `try`-`catch`, as well as a check for Android.
-dontwarn java.lang.ClassValue

# An annotation used for build tooling, won't be directly accessed.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-keep class kotlinx.coroutines.swing.SwingDispatcherFactory {*;}
-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler {*;}

# Theme detector
-dontwarn java.awt.*
-keep class com.sun.jna.* { *; }
-keepclassmembers class * extends com.sun.jna.* { public *; }

# Preserve SAX classes and interfaces
-keep class javax.xml.parsers.SAXParserFactory { *; }
-keep class org.xml.sax.** { *; }
-keep interface org.xml.sax.** { *; }

# Preserve any additional classes or interfaces you use for XML parsing
-keep class com.prof18.rssparser.internal.SaxFeedHandler { *; }
-keepclassmembers class com.prof18.rssparser.internal.SaxFeedHandler { *; }

# If your application uses any other custom XML parsing classes or packages, add additional rules here
#-keep class * implements org.xml.sax.EntityResolver
-keep class com.prof18.feedflow.domain.opml.SaxFeedHandler { *; }
-keepclassmembers class com.prof18.feedflow.domain.opml.SaxFeedHandler { *; }

-keep class javax.xml.stream.XMLOutputFactory { *; }
-keepclassmembers class javax.xml.stream.XMLOutputFactory { *; }

-keepnames class javax.xml.stream.** { *; }
