/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
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
    api(project(":spi:core-spi"))
    implementation(project(":core"))

    api(project(":extensions:audit-logging:audit-logging-spi"))

    implementation("io.codenotary:immudb4j:0.9.10.2")

    testImplementation(project(":extensions:http"))
    testImplementation(project(":core:defaults"))
    testImplementation(project(":extensions:transaction:transaction-local"))

    testImplementation("org.testng:testng:6.8.8")

    implementation("javax.annotation:javax.annotation-api:1.2-b01")

    testImplementation(testFixtures(project(":common:util")))
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testRuntimeOnly("org.glassfish.jersey.ext:jersey-bean-validation:${jerseyVersion}") //for validation
}


publishing {
    publications {
        create<MavenPublication>("audit-logging-manager") {
            artifactId = "audit-logging-manager"
            from(components["java"])
        }
    }
}
