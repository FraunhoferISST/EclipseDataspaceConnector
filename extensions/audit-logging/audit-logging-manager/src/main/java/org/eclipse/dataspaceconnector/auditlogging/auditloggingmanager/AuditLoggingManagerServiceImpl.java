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
import java.sql.Timestamp;
import java.util.List;

public class AuditLoggingManagerServiceImpl implements AuditLoggingManagerService {
   // private ImmuClient immuClient;
    private Config elasticConfig;
    private Monitor monitor;

    private RestClient restClient;
    private ElasticsearchTransport transport;
    private ElasticsearchClient client;


    public AuditLoggingManagerServiceImpl(Config conf, Monitor monitor) {
        this.elasticConfig = conf;

        this.monitor = monitor;

        // Create the low-level client
        this.restClient = RestClient.builder(
            new HttpHost(elasticConfig.getString("url"), elasticConfig.getInteger("port"))).build();

        // Create the transport with a Jackson mapper
        this.transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

        // And create the API client
        this.client = new ElasticsearchClient(transport);


      /*  try {
            //Create index on elasticsearch for the logs
            if (!client.indices().exists(new ExistsRequest.Builder().index(elasticConfig.getString("indexprefix")).build()).value()){
                client.indices().create(c -> c.index(elasticConfig.getString("indexprefix")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
      */  monitor.info("SERVER VERBINDUNG WURDE AUFGEBAUT");
    }

    @Override
    public void addLog(Log log) {
        IndexResponse response = null;
        try {
            if (!client.indices().exists(new ExistsRequest.Builder().index(elasticConfig.getString("indexprefix")).build()).value()){
                client.indices().create(c -> c.index(elasticConfig.getString("indexprefix")));
            }
            response = client.index(i -> i
                    .index(elasticConfig.getString("indexprefix"))
                    .id(log.getUid())
                    .document(log)
            );
            monitor.info("Indexed with version " +response.version());
        } catch (IOException e) {
            monitor.severe("Log could not be written to Elastic");
            monitor.severe("Check if Elasticsearch Server is available");
            monitor.info(log.toString());
        }
    }

    @Override
    public List<Log> getAllLogs() {
        return null;
    }

}
