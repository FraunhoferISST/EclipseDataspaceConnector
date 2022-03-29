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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.net.URI;
import java.util.List;

@JsonPropertyOrder({"providedBy", "aggregationOf", "termsAndConditions", "physicalResource", "virtualResource", "policies"})
public class GaiaxSelfDescription {
    
    @JsonProperty("providedBy")
    private URI providedBy;
    
    @JsonProperty("aggregationOf")
    private List<URI> aggregationOf;
    
    @JsonProperty("termsAndConditions")
    private List<URI> termsAndConditions;
    
    @JsonProperty("physicalResource")
    private List<URI> physicalResources;
    
    @JsonProperty("virtualResource")
    private List<URI> virtualResources;
    
    private List<GaiaxPolicy> policies;
    
    private GaiaxSelfDescription() {
    }
    
    public URI getProvidedBy() {
        return providedBy;
    }
    
    public List<URI> getAggregationOf() {
        return aggregationOf;
    }
    
    public List<URI> getTermsAndConditions() {
        return termsAndConditions;
    }
    
    public List<URI> getPhysicalResources() {
        return physicalResources;
    }
    
    public List<URI> getVirtualResources() {
        return virtualResources;
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
        
        public Builder providedBy(URI providedBy) {
            selfDescription.providedBy = providedBy;
            return this;
        }
        
        public Builder aggregationOf(List<URI> aggregationOf) {
            selfDescription.aggregationOf = aggregationOf;
            return this;
        }
        
        public Builder termsAndConditions(List<URI> termsAndConditions) {
            selfDescription.termsAndConditions = termsAndConditions;
            return this;
        }
        
        public Builder physicalResources(List<URI> physicalResources) {
            this.selfDescription.physicalResources = physicalResources;
            return this;
        }
    
        public Builder virtualResources(List<URI> virtualResources) {
            this.selfDescription.virtualResources = virtualResources;
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
