package org.eclipse.dataspaceconnector.auditlogging;

import java.io.Serializable;

public class CustomLog extends Log implements Serializable {
    private String message;

    public CustomLog(String id, String sourceId, String timestamp, String message){
        super(id,sourceId,timestamp, "");
        this.message = message;
    }

    public String toString() {
        return String.format("{%s,%s,%s,%s}", dataId, sourceId, timestamp,message);
    }
}
