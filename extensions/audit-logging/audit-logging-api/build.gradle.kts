val rsApi: String by project
val restAssured: String by project

plugins {
    `java-library`
    id("io.swagger.core.v3.swagger-gradle-plugin")
}

dependencies {
    implementation(project(":extensions:common:api:api-core"))

    implementation(project(":extensions:audit-logging:audit-logging-manager"))
    implementation(project(":extensions:audit-logging:audit-logging-api-configuration"))
   // implementation(project(":extensions:audit-logging:audit-logging-api"))

  //  implementation(project(":extensions:api:auth-spi"))
  //  implementation(project(":extensions:api:data-management:api-configuration"))

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")

  //  testImplementation(project(":extensions:http"))
  //  testImplementation(project(":extensions:iam:iam-mock"))
  //  testImplementation(testFixtures(project(":common:util")))
  //  testImplementation(testFixtures(project(":launchers:junit")))
  //  testImplementation("io.rest-assured:rest-assured:${restAssured}")
}

publishing {
    publications {
        create<MavenPublication>("audit-logging-api") {
            artifactId = "audit-logging-api"
            from(components["java"])
        }
    }
}