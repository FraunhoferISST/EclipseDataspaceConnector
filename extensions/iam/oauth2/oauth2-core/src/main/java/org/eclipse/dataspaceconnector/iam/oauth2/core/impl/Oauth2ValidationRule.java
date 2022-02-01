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
import org.eclipse.dataspaceconnector.iam.oauth2.spi.ValidationRule;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Oauth2ValidationRule implements ValidationRule {

    private static Instant convertToUtcTime(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")).toInstant();
    }

    /**
     * Validates the JWT by checking the audience, nbf, and expiration. Accessible for testing.
     */
    @Override
    public Result<JWTClaimsSet> checkRule(JWTClaimsSet toVerify, String audience) {
        Instant nowUtc = Instant.now();
        List<String> errors = new ArrayList<>();

        // check audiences
        List<String> audiences = toVerify.getAudience();
        if (audiences.isEmpty()) {
            errors.add("Required audience (aud) claim is missing in token");
        } else if (!audiences.contains(audience)) {
            errors.add("Token audience (aud) claim did not contain connector audience: " + audience);
        }

        // check not before
        var notBefore = toVerify.getNotBeforeTime();
        if (notBefore == null) {
            errors.add("Required not before (nbf) claim is missing in token");
        } else if (nowUtc.isBefore(convertToUtcTime(notBefore))) {
            errors.add("Current date/time before the not before (nbf) claim in token");
        }

        // check expiration time
        Date expires = toVerify.getExpirationTime();
        if (expires == null) {
            errors.add("Required expiration time (exp) claim is missing in token");
        } else if (nowUtc.isAfter(convertToUtcTime(expires))) {
            errors.add("Token has expired (exp)");
        }

        // iat and exp integrity check
        Date issuedAt = toVerify.getIssueTime();
        if (issuedAt != null) {
            if (issuedAt.toInstant().isAfter(expires.toInstant())) {
                errors.add("Issued at (iat) claim is after expiration time (exp) claim in token");
            } else if (nowUtc.isBefore(convertToUtcTime(issuedAt))) {
                errors.add("Current date/time before issued at (iat) claim in token");
            }
        }

        if (errors.isEmpty()) {
            return Result.success(toVerify);
        } else {
            return Result.failure(errors);
        }

    }
}
