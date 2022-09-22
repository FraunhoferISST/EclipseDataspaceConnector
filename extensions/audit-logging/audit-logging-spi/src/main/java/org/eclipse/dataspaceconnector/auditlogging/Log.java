package org.eclipse.dataspaceconnector.auditlogging;

import java.io.Serializable;

public class Log implements Serializable {
    protected String dataId;
    protected String sourceId;
    protected String timestamp;

    protected String message;

    public Log(){}
    public Log(String id, String sourceId, String timestamp, String message) {
        this.dataId = id;
        this.sourceId = sourceId;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
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

    //public String getUid() {
    //    return String.format("%s:%s:%s", dataId,sourceId, timestamp);
   // }

    @Override
    public String toString() {
        return String.format("{%s,%s,%s,%s}",this.dataId,this.sourceId,this.timestamp,this.message);
    }
}