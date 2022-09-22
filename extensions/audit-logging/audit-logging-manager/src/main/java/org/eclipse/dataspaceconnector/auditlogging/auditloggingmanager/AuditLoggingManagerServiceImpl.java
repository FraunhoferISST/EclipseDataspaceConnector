package org.eclipse.dataspaceconnector.auditlogging.auditloggingmanager;

import co.elastic.clients.elasticsearch.ElasticsearchClient;


import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.eclipse.dataspaceconnector.auditlogging.AuditLoggingManagerService;
import org.eclipse.dataspaceconnector.auditlogging.Log;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.configuration.Config;
import org.elasticsearch.client.RestClient;

import java.io.*;
import java.util.List;

public class AuditLoggingManagerServiceImpl implements AuditLoggingManagerService {
   // private ImmuClient immuClient;
    private Config databaseConfig;
    private Monitor monitor;

    private RestClient restClient;
    private ElasticsearchTransport transport;
    private ElasticsearchClient client;


    public AuditLoggingManagerServiceImpl(Config conf, Monitor monitor) {
        this.databaseConfig = conf;

        this.monitor = monitor;

        // Create the low-level client
        this.restClient = RestClient.builder(
            new HttpHost("localhost", 9200)).build();

        // Create the transport with a Jackson mapper
        this.transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

        // And create the API client
        this.client = new ElasticsearchClient(transport);

        //Create index on elasticsearch for the logs
        try {
            if (!client.indices().exists(new ExistsRequest.Builder().index("auditlogging").build()).value()){
                client.indices().create(c -> c.index("auditlogging"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        monitor.info("SERVER VERBINDUNG WURDE AUFGEBAUT");
    }

    @Override
    public void addLog(Log log) {
        IndexResponse response = null;
        try {
            response = client.index(i -> i
                    .index("auditlogging")
                    .id("test6")
                    .document(log)
            );
            monitor.info("Indexed with version " +response.version());

            monitor.info("Try get Document2");
            GetResponse<Log> response2 = client.get(g -> g
                            .index("auditlogging")
                            .id("test6"),
                    Log.class
            );

            monitor.info("Object searched" + response2.id());
            if (response2.found()) {
                Log product = response2.source();
                monitor.info("LOG: " + product.toString());
            } else {
                monitor.info ("Product not found");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        monitor.info(log.toString());
        /*immuClient.useDatabase(databaseConfig.getString("db"));

        try {
            immuClient.set(log.getUid(), logToByte(log));
        } catch (CorruptedDataException e) {
            monitor.info(e.getMessage());
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public List<Log> getAllLogs() {


        /*
        try {
            List<Log> returnListLogs = new ArrayList<>();
            var listLogs = immuClient.scan("");
            listLogs.forEach(x -> {
                returnListLogs.add(byteToLog(x.getValue()));
                monitor.info(byteToLog(x.getValue()).toString());
            });
            return returnListLogs;
        } catch (KeyNotFoundException e) {
            monitor.info(e.getMessage());
            throw new RuntimeException(e);
        }*/
        return null;
    }

    private byte[] logToByte(Log log) {
        /*try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(log);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            monitor.info(e.getMessage());
            throw new RuntimeException(e);
        }*/

        return null;
    }

    private Log byteToLog(byte[] pByte) {
        /*
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(pByte);
            ObjectInputStream in = new ObjectInputStream(bis);

            return (Log) in.readObject();
        } catch (IOException e) {
            monitor.info(e.getMessage());
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            monitor.info(e.getMessage());
            throw new RuntimeException(e);
        }*/
        return null;
    }
}
