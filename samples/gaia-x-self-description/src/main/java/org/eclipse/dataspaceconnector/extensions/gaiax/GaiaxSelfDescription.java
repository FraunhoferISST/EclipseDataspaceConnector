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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.List;

public class GaiaxSelfDescription {
    
    @JsonProperty("providedBy")
    private URI serviceProvider;
    
    @JsonProperty("aggregationOf")
    private List<URI> resources;
    
    @JsonProperty("termsAndConditions")
    private List<URI> termsAndConditions;
    
    private List<GaiaxPolicy> policies;
    
    private GaiaxSelfDescription() {
    }
    
    public URI getServiceProvider() {
        return serviceProvider;
    }
    
    public List<URI> getResources() {
        return resources;
    }
    
    public List<URI> getTermsAndConditions() {
        return termsAndConditions;
    }
    
    public List<GaiaxPolicy> getPolicies() {
        return policies;
    }
    
    public static class Builder {
        private final GaiaxSelfDescription selfDescription;
        
        private Builder() {
            this.selfDescription = new GaiaxSelfDescription();
        }
        
        public static Builder newInstance() {
            return new Builder();
        }
        
        public Builder serviceProvider(URI serviceProvider) {
            selfDescription.serviceProvider = serviceProvider;
            return this;
        }
        
        public Builder resources(List<URI> resources) {
            selfDescription.resources = resources;
            return this;
        }
        
        public Builder termsAndConditions(List<URI> termsAndConditions) {
            selfDescription.termsAndConditions = termsAndConditions;
            return this;
        }
        
        public Builder policies(List<GaiaxPolicy> policies) {
            selfDescription.policies = policies;
            return this;
        }
        
        public GaiaxSelfDescription build() {
            return selfDescription;
        }
    }
}
