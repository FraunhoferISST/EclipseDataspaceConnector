/*
 *  Copyright (c) 2022 Fraunhofer Institute for Software and Systems Engineering
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering - initial API and implementation
 *
 */


val infoModelVersion: String by project
val jerseyVersion: String by project
val okHttpVersion: String by project
val restAssured: String by project
val rsApi: String by project

plugins {
    `java-library`
}

dependencies {
    api(project(":spi:common:core-spi"))
    api(project(":spi:control-plane:transfer-spi"))
    implementation(project(":core"))

    api(project(":extensions:audit-logging:audit-logging-spi"))

    //implementation("io.codenotary:immudb4j:0.9.10.2")

    implementation("co.elastic.clients:elasticsearch-java:8.4.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")

    testImplementation(project(":extensions:common:http"))
    //testImplementation(project(":core:defaults"))
    //testImplementation(project(":extensions:transaction:transaction-local"))

    //estImplementation("org.testng:testng:6.8.8")

    implementation("javax.annotation:javax.annotation-api:1.2-b01")

    //testImplementation(testFixtures(project(":common:util")))
    //testImplementation("io.rest-assured:rest-assured:${restAssured}")
    //testRuntimeOnly("org.glassfish.jersey.ext:jersey-bean-validation:${jerseyVersion}") //for validation
}


publishing {
    publications {
        create<MavenPublication>("audit-logging-manager") {
            artifactId = "audit-logging-manager"
            from(components["java"])
        }
    }
}
