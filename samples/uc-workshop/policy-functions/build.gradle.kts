plugins {
    `java-library`
    id("application")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":spi"))
}
