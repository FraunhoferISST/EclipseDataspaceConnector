package org.eclipse.edc.protocol.dsp.controlplane.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.JsonObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspDispatcherDelegate;
import org.eclipse.edc.spi.EdcException;

import java.io.IOException;
import java.util.function.Function;


public class TransferProcessRequestDelegate implements DspDispatcherDelegate<DataRequest, JsonObject> {

    private ObjectMapper objectMapper;

    public TransferProcessRequestDelegate(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

    @Override
    public Class<DataRequest> getMessageType() {
        return DataRequest.class;
    }

    @Override
    public Request buildRequest(DataRequest message) {
        //TODO Body

        var requestBody = RequestBody.create("content", MediaType.get(jakarta.ws.rs.core.MediaType.APPLICATION_JSON));

        return new Request.Builder()
                .url(message.getConnectorAddress()+"/transfer-processes/request")
                .header("Authorization", "")
                .post(requestBody)
                .build();
    }

    @Override
    public Function<Response, JsonObject> parseResponse() {
        return response -> {
            try {
                return objectMapper.readValue(response.body().bytes(), JsonObject.class);
            } catch (IOException e) {
                throw new EdcException("Failed to read response body.", e);
            }
        };
    }
}
