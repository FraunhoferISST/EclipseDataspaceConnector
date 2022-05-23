plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val jupiterVersion: String by project
val rsApi: String by project

dependencies {
    api(project(":spi"))
    implementation(project(":core"))

    implementation(project(":extensions:api:observability"))

    implementation(project(":extensions:filesystem:configuration-fs"))
    implementation(project(":extensions:iam:iam-mock"))

    implementation(project(":extensions:api:auth-tokenbased"))
    implementation(project(":extensions:api:data-management"))

    implementation(project(":data-protocols:ids"))

    implementation(project(":samples:uc-workshop:policy-functions"))
}

application {
    mainClass.set("org.eclipse.dataspaceconnector.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("uc-workshop.jar")
}
