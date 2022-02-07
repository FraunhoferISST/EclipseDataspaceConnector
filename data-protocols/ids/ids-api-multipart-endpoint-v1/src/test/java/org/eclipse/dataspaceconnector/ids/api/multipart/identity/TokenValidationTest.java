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

import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.iam.TokenRepresentation;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenValidationTest {
    private IdentityService identityService;
    private ObjectMapper objectMapper;
    private DynamicAttributeToken dat;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        var tokenResult = TokenRepresentation.Builder.newInstance().token("token").build();
        var claimToken = ClaimToken.Builder.newInstance()
           .claim("referringConnector", "issuerConnector")
           .build();
        identityService = mock(IdentityService.class);
        when(identityService.obtainClientCredentials(any())).thenReturn(Result.success(tokenResult));
        when(identityService.verifyJwtToken(any())).thenReturn(Result.success(claimToken));
        dat = new DynamicAttributeTokenBuilder()._tokenValue_("fake").build();
    }

    @Test
    void validateToken_succeeded() {
        DescriptionRequestMessage descriptionRequestMessage = new DescriptionRequestMessageBuilder()
                ._securityToken_(dat)
                ._issuerConnector_(URI.create("issuerConnector"))
                .build();

        var tokenValidation = new TokenValidation(false, objectMapper, identityService);
        var result = tokenValidation.validateToken(descriptionRequestMessage, null);
        assertTrue(result.succeeded());
    }

    @Test
    void validateToken_validateReferring_succeeded() {
        DescriptionRequestMessage descriptionRequestMessage = new DescriptionRequestMessageBuilder()
                ._securityToken_(dat)
                ._issuerConnector_(URI.create("issuerConnector"))
                .build();

        var tokenValidation = new TokenValidation(true, objectMapper, identityService);
        var result = tokenValidation.validateToken(descriptionRequestMessage, null);
        assertTrue(result.succeeded());
    }

    @Test
    void validateToken_validateReferring_failed() {
        DescriptionRequestMessage descriptionRequestMessage = new DescriptionRequestMessageBuilder()
                ._securityToken_(dat)
                ._issuerConnector_(URI.create("notDatClaimsReferringConnector"))
                .build();

        var tokenValidation = new TokenValidation(true, objectMapper, identityService);
        var result = tokenValidation.validateToken(descriptionRequestMessage, null);
        assertTrue(result.getFailureMessages().contains("refferingConnector in token does not match issuerConnector in message"));
    }

    @Test
    void validateToken_noDat_failed() {
        DescriptionRequestMessage descriptionRequestMessage = new DescriptionRequestMessageBuilder()
                ._securityToken_(null)
                ._issuerConnector_(URI.create("issuerConnector"))
                .build();

        var tokenValidation = new TokenValidation(false, objectMapper, identityService);
        var result = tokenValidation.validateToken(descriptionRequestMessage, null);
        assertTrue(result.getFailureMessages().contains("Required DAT is missing in message"));
    }

    @Test
    void validateToken_noIssuer_failed() {
        DescriptionRequestMessage descriptionRequestMessage = new DescriptionRequestMessageBuilder()
                ._securityToken_(dat)
                .build();

        var tokenValidation = new TokenValidation(false, objectMapper, identityService);
        var result = tokenValidation.validateToken(descriptionRequestMessage, null);
        assertTrue(result.getFailureMessages().contains("Required issuerConnector is missing in message"));
    }
}
