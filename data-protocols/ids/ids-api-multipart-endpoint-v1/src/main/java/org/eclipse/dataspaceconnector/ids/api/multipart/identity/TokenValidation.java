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
 *       Fraunhofer Institute for Software and Systems Engineering
 *
 */

package org.eclipse.dataspaceconnector.ids.api.multipart.identity;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.Message;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TokenValidation {
    private final Boolean validateReferring;
    private final ObjectMapper objectMapper;
    private final IdentityService identityService;

    public TokenValidation(@NotNull Boolean validateReferring,
                           @NotNull ObjectMapper objectMapper,
                           @NotNull IdentityService identityService) {
        this.validateReferring = Objects.requireNonNull(validateReferring);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.identityService = Objects.requireNonNull(identityService);
    }

    public Result<ClaimToken> validateToken(Message header, String payload) {
        Result<ClaimToken> verificationResult;

        DynamicAttributeToken dynamicAttributeToken = header.getSecurityToken();
        if (dynamicAttributeToken == null || dynamicAttributeToken.getTokenValue() == null) {
            return Result.failure("Required DAT is missing in message");
        } else {
            // 1. Oauth2 token validation
            verificationResult = identityService.verifyJwtToken(dynamicAttributeToken.getTokenValue());

            if (verificationResult.failed()) {
                return verificationResult;
            }

            // 2. IDS specific token validation
            verificationResult = verifyTokenIds(verificationResult, header, payload);

            return verificationResult;
        }
    }

    private Result<ClaimToken> verifyTokenIds(Result<ClaimToken> verificationResult, Message header, String payload) {
        //referringConnector (DAT, optional) vs issuerConnector (Message, mandatory)
        var referringConnector = verificationResult.getContent().getClaims().get("referringConnector");
        var issuerConnector = header.getIssuerConnector();

        if (issuerConnector == null) {
            return Result.failure("Required issuerConnector is missing in message");
        }

        if (Boolean.TRUE.equals(validateReferring) && referringConnector != null && !referringConnector.equals(issuerConnector.toString())) {
            return Result.failure("refferingConnector in token does not match issuerConnector in message");
        }

        //securityProfile (DAT, mandatory) vs securityProfile (Message, optional)
        try {
            var payloadSecurityProfile = objectMapper.readValue(payload, Connector.class).getSecurityProfile();
            var tokenSecurityProfile = verificationResult.getContent().getClaims().get("securityProfile");

            if (payloadSecurityProfile != null && !payloadSecurityProfile.toString().equals(tokenSecurityProfile)) {
                return Result.failure("securityProfile in token does not match securityProfile in payload");
            }
        } catch (Exception e) {
            //Nothing to do, payload mostly no connector instance
        }

        return Result.success(verificationResult.getContent());
    }
}
