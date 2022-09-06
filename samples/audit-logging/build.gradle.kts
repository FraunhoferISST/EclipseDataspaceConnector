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
 *       ZF Friedrichshafen AG - add dependency
 *
 */

plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val jupiterVersion: String by project
val rsApi: String by project

dependencies {
    implementation(project(":core:control-plane:control-plane-core"))

    implementation(project(":extensions:common:api:observability"))

    implementation(project(":extensions:common:configuration:filesystem-configuration"))
    implementation(project(":extensions:common:iam:iam-mock"))

    implementation(project(":extensions:common:auth:auth-tokenbased"))
    implementation(project(":extensions:control-plane:api:data-management"))

    implementation(project(":extensions:audit-logging:audit-logging-manager"))
    implementation(project(":extensions:audit-logging:audit-logging-api"))

    implementation(project(":data-protocols:ids"))

    //Postgres SQL stores
    implementation(project(":extensions:control-plane:store:sql:contract-negotiation-store-sql"))
    implementation(project(":extensions:control-plane:store:sql:transfer-process-store-sql"))
    implementation(project(":extensions:control-plane:store:sql:contract-definition-store-sql"))
    implementation(project(":extensions:control-plane:store:sql:asset-index-sql"))
    implementation(project(":extensions:control-plane:store:sql:policy-store-sql"))


    //Postgres SQL needed helper modules
    implementation(project(":extensions:common:transaction:transaction-local"))
    implementation(project(":spi:common:transaction-spi"))
    implementation(project(":spi:common:transaction-datasource-spi"))
    implementation(project(":extensions:common:sql:common-sql"))
    implementation(project(":extensions:common:sql:lease-sql"))
    implementation(project(":extensions:common:sql:pool:apache-commons-pool-sql"))
    implementation("org.postgresql:postgresql:42.3.3")

    implementation(project(":extensions:control-plane:data-plane-transfer:data-plane-transfer-client"))
    implementation(project(":extensions:data-plane-selector:selector-client"))
    implementation(project(":core:data-plane-selector:data-plane-selector-core"))
    implementation(project(":core:data-plane:data-plane-framework"))
    implementation(project(":extensions:data-plane:data-plane-http"))


    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
}

application {
    mainClass.set("org.eclipse.dataspaceconnector.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("audit-logging.jar")
}
