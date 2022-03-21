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
 *       Fraunhofer Institute for Software and Systems Engineering - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.extensions.gaiax;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.configuration.Config;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class GaiaxSelfDescriptionService {
    
    private GaiaxSelfDescription selfDescription;
    private ContractDefinitionStore contractStore;
    private Monitor monitor;
    private ObjectMapper objectMapper;
    
    public GaiaxSelfDescriptionService(Monitor monitor, Config config, ContractDefinitionStore contractStore) {
        this.contractStore = contractStore;
        this.monitor = monitor;
        this.objectMapper = new ObjectMapper();
        
        var serviceProvider = URI.create(config.getString("serviceprovider"));
        var resources = getResources(config);
        var termsAndConditions = getTermsAndConditions(config);
        selfDescription = GaiaxSelfDescription.Builder.newInstance()
                .serviceProvider(serviceProvider)
                .resources(resources)
                .termsAndConditions(termsAndConditions)
                .build();
    }
    
    public GaiaxSelfDescription getSelfDescription() {
        var copy = copySelfDescription(selfDescription);
        
        var contractDefinitions = contractStore.findAll();
        for (var contractDefinition : contractDefinitions) {
            var accessPolicy = contractDefinition.getAccessPolicy();
            try {
                var accessPolicyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(accessPolicy);
                copy.getPolicies().add(new GaiaxPolicy("ordl", accessPolicyJson));
            } catch (JsonProcessingException e) {
                monitor.warning(format("Failed to parse policy with ID [%s]: %s",
                        accessPolicy.getUid(), e.getMessage()));
            }
            
            var contractPolicy = contractDefinition.getContractPolicy();
            try {
                var contractPolicyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contractPolicy);
                copy.getPolicies().add(new GaiaxPolicy("ordl", contractPolicyJson));
            } catch (JsonProcessingException e) {
                monitor.warning(format("Failed to parse policy with ID [%s]: %s",
                        contractPolicy.getUid(), e.getMessage()));
            }
        }
        
        return copy;
    }
    
    private GaiaxSelfDescription copySelfDescription(GaiaxSelfDescription selfDescription) {
        var resources = new ArrayList<URI>();
        if (selfDescription.getResources() != null) {
            resources.addAll(selfDescription.getResources());
        }
    
        var termsAndConditions = new ArrayList<URI>();
        if (selfDescription.getTermsAndConditions() != null) {
            termsAndConditions.addAll(selfDescription.getTermsAndConditions());
        }
        
        var policies = new ArrayList<GaiaxPolicy>();
        if (selfDescription.getPolicies() != null) {
            selfDescription.getPolicies()
                    .forEach(p -> policies.add(new GaiaxPolicy(p.getType(), p.getContent())));
        }
        
        return GaiaxSelfDescription.Builder.newInstance()
                .serviceProvider(selfDescription.getServiceProvider())
                .resources(resources)
                .termsAndConditions(termsAndConditions)
                .policies(policies)
                .build();
    }
    
    private List<URI> getResources(Config config) {
        var resources = new ArrayList<URI>();
        for (var entry : config.getConfig("resource").getEntries().entrySet()) {
            try {
                resources.add(URI.create(entry.getValue()));
            } catch (IllegalArgumentException e) {
                monitor.info(format("Invalid entry for resource: [(%s), (%s)]", entry.getKey(), entry.getValue()));
            }
        }
        return resources;
    }
    
    private List<URI> getTermsAndConditions(Config config) {
        var termsAndConditions = new ArrayList<URI>();
        for (var entry : config.getConfig("termsandconditions").getEntries().entrySet()) {
            try {
                termsAndConditions.add(URI.create(entry.getValue()));
            } catch (IllegalArgumentException e) {
                monitor.info(format("Invalid entry for terms and conditions: [(%s), (%s)]", entry.getKey(), entry.getValue()));
            }
        }
        return termsAndConditions;
    }
    
}
