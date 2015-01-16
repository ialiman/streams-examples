/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.streams.elasticsearch.example;

import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.elasticsearch.ElasticsearchConfigurator;
import org.apache.streams.elasticsearch.ElasticsearchPersistReader;
import org.apache.streams.elasticsearch.ElasticsearchReaderConfiguration;
import org.apache.streams.hdfs.HdfsConfigurator;
import org.apache.streams.hdfs.HdfsWriterConfiguration;
import org.apache.streams.hdfs.WebHdfsPersistWriter;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class ElasticsearchBackup {

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchBackup.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config elasticsearch = StreamsConfigurator.config.getConfig("elasticsearch");
        ElasticsearchReaderConfiguration elasticsearchReaderConfiguration = ElasticsearchConfigurator.detectReaderConfiguration(elasticsearch);

        ElasticsearchPersistReader elasticsearchReader = new ElasticsearchPersistReader(elasticsearchReaderConfiguration);

        Config hdfs = StreamsConfigurator.config.getConfig("hdfs");
        HdfsWriterConfiguration hdfsWriterConfiguration  = HdfsConfigurator.detectWriterConfiguration(hdfs);

        WebHdfsPersistWriter hdfsWriter = new WebHdfsPersistWriter(hdfsWriterConfiguration);

        StreamBuilder builder = new LocalStreamBuilder(2500);

        builder.newPerpetualStream(ElasticsearchPersistReader.STREAMS_ID, elasticsearchReader);
        builder.addStreamsPersistWriter(WebHdfsPersistWriter.STREAMS_ID, hdfsWriter, 1, ElasticsearchPersistReader.STREAMS_ID);
        builder.start();

    }

}
