package org.eclipse.dataspaceconnector.auditlogging;

import java.io.Serializable;

public class Log implements Serializable {
    private String dataId;
    private String logMessage;
    private String sourceId;
    private String timestamp;

    public Log(String id, String sourceId, String timestamp, String message) {
        this.dataId = id;
        this.logMessage = message;
        this.sourceId = sourceId;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return String.format("%s:%s", dataId, timestamp);
    }

    public String toString() {
        return String.format("{%s,%s,%s,%s}", dataId, sourceId, timestamp, logMessage);
    }
}