import com.android.builder.files.classpathToRelativeFileSet
import org.gradle.kotlin.dsl.internal.sharedruntime.support.classFilePathCandidatesFor

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
dependencies {
    classFilePathCandidatesFor("com.squareup.picasso:picasso:2.8")
    classFilePathCandidatesFor("com.squareup.retrofit2:retrofit:2.9.0")
    classFilePathCandidatesFor("com.squareup.retrofit2:converter-gson:2.9.0")
    classFilePathCandidatesFor("com.google.android.exoplayer:exoplayer:2.19.1")
}