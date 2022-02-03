/*
 *  Copyright (c) 2020, 2021, 2022
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering
 *
 */

package org.eclipse.dataspaceconnector.iam.oauth2.core.impl;

import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class Oauth2ValidationRuleTest {

    public static final String TEST_AUDIENCE = "test-audience";
    private Oauth2ValidationRule rule;
    private Instant now;

    @Test
    void validationKoBecauseNotBeforeTimeNotRespected() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("test-audience")
                .notBeforeTime(Date.from(now.plusSeconds(20)))
                .expirationTime(Date.from(now.plusSeconds(600)))
                .build();

        var result = rule.checkRule(claims, TEST_AUDIENCE);

        assertThat(result.succeeded()).isFalse();
        assertThat(result.getFailureMessages()).hasSize(1)
                .contains("Current date/time before the not before (nbf) claim in token");
    }

    @Test
    void validationKoBecauseNotBeforeTimeNotProvided() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("test-audience")
                .expirationTime(Date.from(now.plusSeconds(600)))
                .build();

        var result = rule.checkRule(claims, TEST_AUDIENCE);

        assertThat(result.succeeded()).isFalse();
        assertThat(result.getFailureMessages()).hasSize(1)
                .contains("Required not before (nbf) claim is missing in token");
    }

    @Test
    void validationKoBecauseExpirationTimeNotRespected() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("test-audience")
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.minusSeconds(10)))
                .build();

        var result = rule.checkRule(claims, TEST_AUDIENCE);

        assertThat(result.succeeded()).isFalse();
        assertThat(result.getFailureMessages()).hasSize(1)
                .contains("Token has expired (exp)");
    }

    @Test
    void validationKoBecauseExpirationTimeNotProvided() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("test-audience")
                .notBeforeTime(Date.from(now))
                .build();

        var result = rule.checkRule(claims, TEST_AUDIENCE);

        assertThat(result.succeeded()).isFalse();
        assertThat(result.getFailureMessages()).hasSize(1)
                .contains("Required expiration time (exp) claim is missing in token");
    }

    @Test
    void validationKoBecauseAudienceNotRespected() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("fake-audience")
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(600)))
                .build();

        var result = rule.checkRule(claims, TEST_AUDIENCE);

        assertThat(result.succeeded()).isFalse();
        assertThat(result.getFailureMessages()).hasSize(1)
                .contains("Token audience (aud) claim did not contain connector audience: test-audience");
    }

    @Test
    void validationKoBecauseAudienceNotProvided() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(600)))
                .build();

        var result = rule.checkRule(claims, TEST_AUDIENCE);

        assertThat(result.succeeded()).isFalse();
        assertThat(result.getFailureMessages()).hasSize(1)
                .contains("Required audience (aud) claim is missing in token");
    }

    @Test
    void validationOk() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("test-audience")
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(600)))
                .build();

        var result = rule.checkRule(claims, TEST_AUDIENCE);

        assertThat(result.succeeded()).isTrue();
    }

    @Test
    void validationKoBecauseIssuedAtAfterExpires() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("test-audience")
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(60)))
                .issueTime(Date.from(now.plusSeconds(65)))
                .build();

        var result = rule.checkRule(claims, TEST_AUDIENCE);

        assertThat(result.succeeded()).isFalse();
        assertThat(result.getFailureMessages()).hasSize(1).contains("Issued at (iat) claim is after expiration time (exp) claim in token");
    }

    @Test
    void validationKoBecauseIssuedAtInFuture() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("test-audience")
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(60)))
                .issueTime(Date.from(now.plusSeconds(10)))
                .build();

        var result = rule.checkRule(claims, TEST_AUDIENCE);

        assertThat(result.succeeded()).isFalse();
        assertThat(result.getFailureMessages()).hasSize(1).contains("Current date/time before issued at (iat) claim in token");
    }

    @BeforeEach
    public void setUp() {
        rule = new Oauth2ValidationRule();
        now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
