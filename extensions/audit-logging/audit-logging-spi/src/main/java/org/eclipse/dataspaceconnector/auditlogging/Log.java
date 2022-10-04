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

package org.eclipse.dataspaceconnector.auditlogging;

import java.io.Serializable;

public class Log implements Serializable {
    protected String sourceId;
    protected String timestamp;

    protected String message;

    public Log(){}
    public Log(String sourceId, String timestamp, String message) {
        this.sourceId = sourceId;
        this.timestamp = timestamp;
        this.message = message;
    }


    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

   public String getUid() {
        return String.format("%s:%s", sourceId, timestamp);
   }

    @Override
    public String toString() {
        return String.format("{%s,%s,%s}",this.sourceId,this.timestamp,this.message);
    }
}