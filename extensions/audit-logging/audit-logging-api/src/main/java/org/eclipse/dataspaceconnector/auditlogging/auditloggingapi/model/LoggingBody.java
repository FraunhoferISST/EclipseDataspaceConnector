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


package org.eclipse.dataspaceconnector.auditlogging.auditloggingapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.NotNull;

import java.util.Map;


@JsonDeserialize(builder = LoggingBody.Builder.class)
public class LoggingBody {

    @NotNull(message = "dataID cannot be null")
    private String dataID;

    @NotNull(message = "timestamp cannot be null")
    private String timestamp;

    @NotNull(message = "sourceID cannot be null")
    private String sourceID;

    @NotNull(message = "logText cannot be null")
    private String logText;


    private LoggingBody(){

    }

    public String getDataID() {
        return dataID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSourceID() {
        return sourceID;
    }

    public String getLogText() {
        return logText;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder{

        private final LoggingBody loggingBody;

        private Builder(){
            loggingBody = new LoggingBody();
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder dataID(String dataID) {
            loggingBody.dataID = dataID;
            return this;
        }

        public Builder timestamp(String timestamp){
            loggingBody.timestamp= timestamp;
            return this;
        }

        public Builder sourceID(String sourceID){
            loggingBody.sourceID = sourceID;
            return this;
        }

        public Builder logText(String logText){
            loggingBody.logText = logText;
            return this;
        }

        public LoggingBody build() {
            return loggingBody;
        }
    }
}
