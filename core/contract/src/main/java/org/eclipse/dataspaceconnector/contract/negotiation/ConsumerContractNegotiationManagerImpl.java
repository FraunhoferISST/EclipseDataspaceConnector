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
 *       Fraunhofer Institute for Software and Systems Engineering - extended method implementation
 *
 */
package org.eclipse.dataspaceconnector.contract.negotiation;

import org.eclipse.dataspaceconnector.spi.contract.negotiation.ConsumerContractNegotiationManager;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.NegotiationWaitStrategy;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.response.NegotiationResponse;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.store.ContractNegotiationStore;
import org.eclipse.dataspaceconnector.spi.contract.validation.ContractValidationService;
import org.eclipse.dataspaceconnector.spi.contract.validation.OfferValidationResult;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.transfer.TransferWaitStrategy;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.agreement.ContractAgreement;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.agreement.ContractAgreementRequest;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.negotiation.ContractNegotiation;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.negotiation.ContractNegotiationStates;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.negotiation.ContractOfferRequest;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.negotiation.ContractRejection;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractOffer;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;
import static org.eclipse.dataspaceconnector.spi.contract.negotiation.response.NegotiationResponse.Status.FATAL_ERROR;
import static org.eclipse.dataspaceconnector.spi.contract.negotiation.response.NegotiationResponse.Status.OK;

/**
 * Implementation of the {@link ConsumerContractNegotiationManager}.
 *
 * TODO
 * - InMemoryContractNegotiationStore (see InMemoryTransferProcessStore), implement ContractNegotiationStore
 * - ContractNegotiation: Builder, transfer change methods
 * - ConsumerContractNegotiationManager & ProviderContractNegotiationManager: add start and stop methods, builder
 * - method call in CoreTransferExtension
 *
 */
public class ConsumerContractNegotiationManagerImpl implements ConsumerContractNegotiationManager {
    private final AtomicBoolean active = new AtomicBoolean();
    private ContractNegotiationStore negotiationStore;
    private ContractValidationService validationService;

    private int batchSize = 5;
    private NegotiationWaitStrategy waitStrategy = () -> 5000L;  // default wait five seconds
    private Monitor monitor;
    private ExecutorService executor;

    private RemoteMessageDispatcherRegistry dispatcherRegistry;

    public ConsumerContractNegotiationManagerImpl() {
    }

    public void start(ContractNegotiationStore store) {
        negotiationStore = store;
        active.set(true);
        executor = Executors.newSingleThreadExecutor();
        executor.submit(this::run);
    }

    public void stop() {
        active.set(false);
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    /**
     * Method triggered by event.
     */
    @Override
    public NegotiationResponse initiate(ContractOfferRequest contractOffer) {
        var negotiation = ContractNegotiation.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .protocol(contractOffer.getProtocol())
                .counterPartyId(contractOffer.getConnectorId())
                .counterPartyAddress(contractOffer.getConnectorAddress())
                .state(0)
                .stateCount(0)
                .stateTimestamp(Instant.now().toEpochMilli())
                .build();

        negotiation.addContractOffer(contractOffer.getContractOffer());
        negotiationStore.create(negotiation);

        monitor.debug("Contract negotiation initiated. Process " + negotiation.getId() + " is now " + ContractNegotiationStates.from(negotiation.getState()));
        return new NegotiationResponse(OK, negotiation);
    }

    @Override
    public NegotiationResponse offerReceived(ClaimToken token, String negotiationId, ContractOffer contractOffer, String hash) {
        var negotiation = negotiationStore.find(negotiationId);
        if (negotiation == null) {
            return new NegotiationResponse(FATAL_ERROR);
        }

        var latestOffer = negotiation.getLastContractOffer();
        try {
            Objects.requireNonNull(latestOffer, "latestOffer");
        } catch (NullPointerException e) {
            monitor.severe("No offer found for validation. Process id: " + negotiation.getId());
            return new NegotiationResponse(NegotiationResponse.Status.FATAL_ERROR, negotiation);
        }

        OfferValidationResult result = validationService.validate(token, contractOffer, latestOffer);
        negotiation.addContractOffer(contractOffer); // TODO persist unchecked offer of provider?
        if (result.invalid()) {
            if (result.isCounterOfferAvailable()) {
                negotiation.addContractOffer(result.getCounterOffer());
                monitor.debug("Contract offer received. A counter offer is available");
                negotiation.transitionOffering();
            } else {
                // If no counter offer available + validation result invalid, decline negotiation.
                monitor.debug("Contract offer received. Will be rejected");
                negotiation.transitionDeclining();
            }
        } else {
            // Offer has been approved.
            monitor.debug("Contract offer received. Will be approved");
            negotiation.transitionApproving();
        }

        negotiationStore.update(negotiation);
        monitor.debug("Negotiation process " + negotiation.getId() + " is now " + ContractNegotiationStates.from(negotiation.getState()));

        return new NegotiationResponse(OK, negotiation);
    }

    @Override
    public NegotiationResponse confirmed(ClaimToken token, String negotiationId, ContractAgreement agreement, String hash) {
        var negotiation = negotiationStore.find(negotiationId);
        if (negotiation == null) {
            return new NegotiationResponse(FATAL_ERROR);
        }

        var latestOffer = negotiation.getLastContractOffer();
        try {
            Objects.requireNonNull(latestOffer, "latestOffer");
        } catch (NullPointerException e) {
            monitor.severe("No offer found for validation. Process id: " + negotiation.getId());
            return new NegotiationResponse(NegotiationResponse.Status.ERROR_RETRY, negotiation);
        }

        var result = validationService.validate(token, agreement, latestOffer);
        if (!result) {
            // TODO Add contract offer possibility.
            monitor.debug("Contract agreement received. Validation failed");
            negotiation.transitionDeclining();
            negotiationStore.update(negotiation);
            return new NegotiationResponse(OK, negotiation);
        }

        // Agreement has been approved.
        negotiation.setContractAgreement(agreement); // TODO persist unchecked agreement of provider?
        monitor.debug("Contract agreement received. Validation successful");
        negotiation.transitionConfirmed();
        negotiationStore.update(negotiation);
        monitor.debug("Negotiation process " + negotiation.getId() + " is now " + ContractNegotiationStates.from(negotiation.getState()));

        return new NegotiationResponse(OK, negotiation);
    }

    @Override
    public NegotiationResponse declined(ClaimToken token, String negotiationId) {
        var negotiation = negotiationStore.find(negotiationId);
        if (negotiation == null) {
            return new NegotiationResponse(FATAL_ERROR);
        }

        monitor.debug("Contract rejection received. Abort negotiation process");
        negotiation.transitionDeclined();

        monitor.debug("Negotiation process " + negotiation.getId() + " is now " + ContractNegotiationStates.from(negotiation.getState()));
        return new NegotiationResponse(OK, negotiation);
    }

    private CompletableFuture<Object> sendOffer(ContractOffer offer, ContractNegotiation process) {
        var request = ContractOfferRequest.Builder.newInstance()
                .contractOffer(offer)
                .connectorAddress(process.getCounterPartyAddress())
                .protocol(process.getProtocol())
                .connectorId(process.getCounterPartyId())
                .correlationId(process.getId())
                .build();

        // TODO protocol-independent response type?
        return dispatcherRegistry.send(Object.class, request, process::getId);
    }

    private int sendContractOffers() {
        var processes = negotiationStore.nextForState(ContractNegotiationStates.REQUESTING.code(), batchSize);

        for (ContractNegotiation process : processes) {
            var offer = process.getLastContractOffer();
            var response = sendOffer(offer, process);
            if (response.isCompletedExceptionally()) {
                process.transitionRequesting();
                monitor.debug(format("Failed to send contract offer with id %s. Process %s is now %s" + offer.getId(), process.getId(), ContractNegotiationStates.from(process.getState())));
                continue;
            }

            process.transitionRequested();
            monitor.debug("Negotiation process " + process.getId() + " is now " + ContractNegotiationStates.from(process.getState()));
            negotiationStore.update(process);
        }

        return processes.size();
    }

    private int sendCounterOffers() {
        var processes = negotiationStore.nextForState(ContractNegotiationStates.CONSUMER_OFFERING.code(), batchSize);

        for (ContractNegotiation process : processes) {
            var offer = process.getLastContractOffer();
            var response = sendOffer(offer, process);
            if (response.isCompletedExceptionally()) {
                process.transitionOffering();
                monitor.debug(format("Failed to send counter offer with id %s. Process %s is now %s" + offer.getId(), process.getId(), ContractNegotiationStates.from(process.getState())));
                continue;
            }

            process.transitionOffered();
            monitor.debug("Negotiation process " + process.getId() + " is now " + ContractNegotiationStates.from(process.getState()));
            negotiationStore.update(process);
        }

        return processes.size();
    }

    private int approveContractOffers() {
        var processes = negotiationStore.nextForState(ContractNegotiationStates.CONSUMER_APPROVING.code(), batchSize);

        for (ContractNegotiation process : processes) {
            var agreement = process.getContractAgreement();

            var request = ContractAgreementRequest.Builder.newInstance()
                    .protocol(process.getProtocol())
                    .connectorId(process.getCounterPartyId())
                    .connectorAddress(process.getCounterPartyAddress())
                    .contractAgreement(agreement)
                    .correlationId(process.getId())
                    .build();

            // TODO protocol-independent response type?
            var response = dispatcherRegistry.send(Object.class, request, process::getId);
            if (response.isCompletedExceptionally()) {
                process.transitionApproving();
                monitor.debug(format("Failed to send contract agreement with id %s. Process %s is now %s" + agreement.getId(), process.getId(), ContractNegotiationStates.from(process.getState())));
                continue;
            }

            process.transitionApproved();
            monitor.debug("Negotiation process " + process.getId() + " is now " + ContractNegotiationStates.from(process.getState()));
            negotiationStore.update(process);
        }

        return processes.size();
    }

    private int declineContractOffers() {
        var processes = negotiationStore.nextForState(ContractNegotiationStates.DECLINING.code(), batchSize);

        for (ContractNegotiation process : processes) {
            var offer = process.getLastContractOffer();

            var rejection = ContractRejection.Builder.newInstance()
                    .protocol(process.getProtocol())
                    .connectorId(process.getCounterPartyId())
                    .connectorAddress(process.getCounterPartyAddress())
                    .correlationId(process.getId())
                    .rejectionReason(process.getErrorDetail())
                    .build();

            // TODO protocol-independent response type?
            var response = dispatcherRegistry.send(Object.class, rejection, process::getId);
            if (response.isCompletedExceptionally()) {
                process.transitionDeclining();
                negotiationStore.update(process);
                monitor.debug(format("Failed to send contract rejection. Process %s is now %s" + process.getId(), ContractNegotiationStates.from(process.getState())));
                continue;
            }

            process.transitionDeclined();
            monitor.debug("Negotiation process " + process.getId() + " is now " + ContractNegotiationStates.from(process.getState()));
            negotiationStore.update(process);
        }

        return processes.size();
    }

    private void run() {
        while (active.get()) {
            try {
                int requesting = sendContractOffers();
                int offering = sendCounterOffers();
                int approving = approveContractOffers();
                int declining = declineContractOffers();

                if (requesting + offering + approving + declining == 0) {
                    Thread.sleep(waitStrategy.waitForMillis());
                }
                waitStrategy.success();
            } catch (Error e) {
                throw e; // let the thread die and don't reschedule as the error is unrecoverable
            } catch (InterruptedException e) {
                Thread.interrupted();
                active.set(false);
                break;
            } catch (Throwable e) {
                monitor.severe("Error caught in consumer contract negotiation manager", e);
                try {
                    Thread.sleep(waitStrategy.retryInMillis());
                } catch (InterruptedException e2) {
                    Thread.interrupted();
                    active.set(false);
                    break;
                }
            }
        }
    }

    public static class Builder {
        private final ConsumerContractNegotiationManagerImpl manager;

        private Builder() {
            manager = new ConsumerContractNegotiationManagerImpl();
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder negotiationStore(ContractNegotiationStore negotiationStore) {
            manager.negotiationStore = negotiationStore;
            return this;
        }

        public Builder validationService(ContractValidationService validationService) {
            manager.validationService = validationService;
            return this;
        }

        public Builder monitor(Monitor monitor) {
            manager.monitor = monitor;
            return this;
        }

        public Builder executorService(ExecutorService executor) {
            manager.executor = executor;
            return this;
        }

        public Builder batchSize(int batchSize) {
            manager.batchSize = batchSize;
            return this;
        }

        public Builder waitStrategy(NegotiationWaitStrategy waitStrategy) {
            manager.waitStrategy = waitStrategy;
            return this;
        }

        public Builder dispatcherRegistry(RemoteMessageDispatcherRegistry dispatcherRegistry) {
            manager.dispatcherRegistry = dispatcherRegistry;
            return this;
        }

        public ConsumerContractNegotiationManagerImpl build() {
            Objects.requireNonNull(manager.negotiationStore, "contractNegotiationStore");
            Objects.requireNonNull(manager.validationService, "contractValidationService");
            Objects.requireNonNull(manager.monitor, "monitor");
            Objects.requireNonNull(manager.dispatcherRegistry, "dispatcherRegistry");
            return manager;
        }
    }
}
