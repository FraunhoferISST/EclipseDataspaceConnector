/*
 *  Copyright (c) 2021 Microsoft Corporation
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
package org.eclipse.dataspaceconnector.spi.contract.validation;

import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractOffer;

/**
 * The result of a contract offer validation. // TODO Add rejection reason?
 */
public class OfferValidationResult {
    public static final OfferValidationResult INVALID = new OfferValidationResult();

    private ContractOffer validatedOffer;
    private ContractOffer counterOffer;

    public OfferValidationResult(ContractOffer validatedOffer, ContractOffer counterOffer) {
        this.validatedOffer = validatedOffer;
        this.counterOffer = counterOffer;
    }

    public OfferValidationResult() {
    }

    public ContractOffer getValidatedOffer() {
        return validatedOffer;
    }

    public ContractOffer getCounterOffer() {
        return counterOffer;
    }

    public boolean isCounterOfferAvailable() {
        return counterOffer != null;
    }

    public boolean invalid() {
        return validatedOffer == null;
    }
}
