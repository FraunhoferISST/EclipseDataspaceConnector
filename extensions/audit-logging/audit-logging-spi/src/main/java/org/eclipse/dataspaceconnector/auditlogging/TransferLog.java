package org.eclipse.dataspaceconnector.auditlogging;

import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataRequest;

import java.io.Serializable;

public class TransferLog extends Log implements Serializable {
    private DataRequest request;
    private String message;
    public TransferLog(String id, String sourceId, String timestamp, DataRequest request){
        super(id,sourceId,timestamp,"");
        this.request = request;
        message = "TEst";
    }

    public String toString() {
        return String.format("{%s,%s,%s,%s}", dataId, sourceId, timestamp,message);
    }

    private String createMessage(){
        return String.format("Das Asset %s wird vom Dataprovider %s:%s zu dem DatenConsumer %s mit Hilfe der Dataplane versendet.",this.dataId,this.request.getConnectorId(),this.request.getConnectorAddress(),this.request.getDataDestination().getProperties());
    }
}
