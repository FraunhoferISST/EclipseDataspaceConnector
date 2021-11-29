/*
 *  Copyright (c) 2021 Fraunhofer Institute for Software and Systems Engineering
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.spi.types.domain.contract.agreement;

import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;

import java.util.Objects;

public class ContractAgreementRequest implements RemoteMessage {

    private String protocol;
    private String connectorId;
    private String connectorAddress;
    private String correlationId;
    private ContractAgreement contractAgreement;

    @Override
    public String getProtocol() {
        return protocol;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public String getConnectorAddress() {
        return connectorAddress;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public ContractAgreement getContractAgreement() {
        return contractAgreement;
    }

    public static class Builder {
        private final ContractAgreementRequest contractAgreementRequest;

        private Builder() {
            this.contractAgreementRequest = new ContractAgreementRequest();
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder protocol(String protocol) {
            this.contractAgreementRequest.protocol = protocol;
            return this;
        }

        public Builder connectorId(String connectorId) {
            this.contractAgreementRequest.connectorId = connectorId;
            return this;
        }

        public Builder connectorAddress(String connectorAddress) {
            this.contractAgreementRequest.connectorAddress = connectorAddress;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.contractAgreementRequest.correlationId = correlationId;
            return this;
        }

        public Builder contractAgreement(ContractAgreement contractAgreement) {
            this.contractAgreementRequest.contractAgreement = contractAgreement;
            return this;
        }

        public ContractAgreementRequest build() {
            Objects.requireNonNull(contractAgreementRequest.protocol, "protocol");
            Objects.requireNonNull(contractAgreementRequest.connectorId, "connectorId");
            Objects.requireNonNull(contractAgreementRequest.connectorAddress, "connectorAddress");
            Objects.requireNonNull(contractAgreementRequest.contractAgreement, "contractAgreement");
            Objects.requireNonNull(contractAgreementRequest.correlationId, "correlationId");
            return contractAgreementRequest;
        }
    }
}
