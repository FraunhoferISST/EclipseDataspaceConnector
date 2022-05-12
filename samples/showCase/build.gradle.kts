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
 *       Fraunhofer Institute for Software and Systems Engineering - added dependencies
 *
 */

plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val jupiterVersion: String by project

dependencies {
    api(project(":core"))

    implementation(project(":data-protocols:ids"))

    implementation(project(":extensions:filesystem:configuration-fs"))
    implementation(project(":extensions:filesystem:vault-fs"))

    implementation(project(":extensions:iam:oauth2:oauth2-core"))
    implementation(project(":extensions:iam:daps"))

    implementation(project(":extensions:api:data-management"))

    implementation(project(":extensions:sql:contract-negotiation-store-sql"))
    implementation(project(":extensions:sql:asset-index-sql"))
    implementation(project(":extensions:sql:contract-definition-store-sql"))
    implementation(project(":extensions:sql:policy-store-sql"))
   // implementation(project(":extensions:sql:transfer-process-store-sql"))


    implementation(project(":extensions:transaction:transaction-local"))
    implementation("org.postgresql:postgresql:42.3.3")
    implementation(project(":extensions:transaction:transaction-spi"))
    implementation(project(":extensions:transaction:transaction-datasource-spi"))
    implementation(project(":extensions:sql:common-sql"))
    implementation(project(":extensions:sql:lease-sql"))
    implementation(project(":extensions:sql:pool:apache-commons-pool-sql"))


    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
}

application {
    mainClass.set("org.eclipse.dataspaceconnector.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("connector-health.jar")
}
