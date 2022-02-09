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

package org.eclipse.dataspaceconnector.iam.oauth2.spi;

import com.nimbusds.jwt.JWTClaimsSet;
import org.eclipse.dataspaceconnector.spi.result.Result;

@FunctionalInterface
public interface ValidationRule {

    /**
     * Rule that a token must satisfy in order to be valid.
     *
     * @param toVerify The Claims to verify.
     * @return ValidationRuleResult
     */
    Result<JWTClaimsSet> checkRule(JWTClaimsSet toVerify);
}
