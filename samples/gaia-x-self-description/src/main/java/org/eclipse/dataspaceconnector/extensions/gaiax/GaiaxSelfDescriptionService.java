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
        
        var providedBy = URI.create(config.getString("providedby"));
        var aggregationOf = getUris(config, "aggregationof");
        var termsAndConditions = getUris(config, "termsandconditions");
        var physicalResources = getUris(config, "physicalresource");
        var virtualResources = getUris(config, "virtualresource");
        
        selfDescription = GaiaxSelfDescription.Builder.newInstance()
                .providedBy(providedBy)
                .aggregationOf(aggregationOf)
                .termsAndConditions(termsAndConditions)
                .physicalResources(physicalResources)
                .virtualResources(virtualResources)
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
        if (selfDescription.getAggregationOf() != null) {
            resources.addAll(selfDescription.getAggregationOf());
        }
    
        var termsAndConditions = new ArrayList<URI>();
        if (selfDescription.getTermsAndConditions() != null) {
            termsAndConditions.addAll(selfDescription.getTermsAndConditions());
        }
        
        var physicalResources = new ArrayList<URI>();
        if (selfDescription.getPhysicalResources() != null) {
            physicalResources.addAll(selfDescription.getPhysicalResources());
        }
    
        var virtualResources = new ArrayList<URI>();
        if (selfDescription.getVirtualResources() != null) {
            virtualResources.addAll(selfDescription.getVirtualResources());
        }
        
        var policies = new ArrayList<GaiaxPolicy>();
        if (selfDescription.getPolicies() != null) {
            selfDescription.getPolicies()
                    .forEach(p -> policies.add(new GaiaxPolicy(p.getType(), p.getContent())));
        }
        
        return GaiaxSelfDescription.Builder.newInstance()
                .providedBy(selfDescription.getProvidedBy())
                .aggregationOf(resources)
                .termsAndConditions(termsAndConditions)
                .physicalResources(physicalResources)
                .virtualResources(virtualResources)
                .policies(policies)
                .build();
    }
    
    private List<URI> getUris(Config config, String subConfigName) {
        var list = new ArrayList<URI>();
        for (var entry : config.getConfig(subConfigName).getEntries().entrySet()) {
            try {
                list.add(URI.create(entry.getValue()));
            } catch (IllegalArgumentException e) {
                monitor.info(format("Invalid entry: [(%s), (%s)]", entry.getKey(), entry.getValue()));
            }
        }
        return list;
    }
    
}
