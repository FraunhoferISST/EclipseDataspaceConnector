/*
 *  Copyright (c) 2020 - 2022
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

package org.eclipse.dataspaceconnector.iam.oauth2.core.rule;

import com.nimbusds.jwt.SignedJWT;
import org.eclipse.dataspaceconnector.iam.oauth2.spi.ValidationRule;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Map;

public class IdsValidationRule implements ValidationRule {
    /**
     * Validates the JWT by checking extended IDS rules.
     */
    @Override
    public Result<SignedJWT> checkRule(SignedJWT jwt, @Nullable Map<String, Object> additional) {
        if (additional != null && additional.containsKey("IdsValidationRule") && (Boolean) additional.get("IdsValidationRule")) {
            var issuerConnector = additional.get("issuerConnector");
            if (issuerConnector == null) {
                return Result.failure("Required issuerConnector is missing in message");
            }

            var validateReferring = additional.get("validateReferring");
            if (validateReferring == null) {
                throw new EdcException("Required setting for validateReferring is missing");
            }

            String securityProfile = null;
            if (additional.containsKey("securityProfile")) {
                securityProfile = additional.get("securityProfile").toString();
            }

            return verifyTokenIds(jwt, issuerConnector.toString(), (Boolean) validateReferring, securityProfile);

        } else {
            return Result.success(jwt);
        }
    }

    private Result<SignedJWT> verifyTokenIds(SignedJWT jwt, String issuerConnector, Boolean validateReferring, @Nullable String securityProfile) {
        try {
            var claims = jwt.getJWTClaimsSet().getClaims();

            //referringConnector (DAT, optional) vs issuerConnector (Message-Header, mandatory)
            var referringConnector = claims.get("referringConnector");

            if (Boolean.TRUE.equals(validateReferring) && referringConnector != null && !referringConnector.equals(issuerConnector)) {
                return Result.failure("refferingConnector in token does not match issuerConnector in message");
            }

            //securityProfile (DAT, mandatory) vs securityProfile (Message-Payload, optional)
            try {
                var tokenSecurityProfile = claims.get("securityProfile");

                if (securityProfile != null && !securityProfile.equals(tokenSecurityProfile)) {
                    return Result.failure("securityProfile in token does not match securityProfile in payload");
                }
            } catch (Exception e) {
                //Nothing to do, payload mostly no connector instance
            }
        } catch (ParseException e) {
            throw new EdcException("IdsValidationRule: unable to parse SignedJWT (" + e.getMessage() + ")");
        }

        return Result.success(jwt);
    }
}
