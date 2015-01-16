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

package org.apache.streams.moreover.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.data.moreover.MoreoverConfigurator;
import org.apache.streams.data.moreover.MoreoverProvider;
import org.apache.streams.hdfs.HdfsConfiguration;
import org.apache.streams.hdfs.HdfsConfigurator;
import org.apache.streams.hdfs.HdfsWriterConfiguration;
import org.apache.streams.hdfs.WebHdfsPersistWriter;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.moreover.MoreoverConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class MoreoverMetabaseHdfs {

    private final static Logger LOGGER = LoggerFactory.getLogger(MoreoverMetabaseHdfs.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args)
    {

        Config moreover = StreamsConfigurator.config.getConfig("moreover");
        MoreoverConfiguration moreoverConfiguration = MoreoverConfigurator.detectConfiguration(moreover);

        Config hdfs = StreamsConfigurator.config.getConfig("hdfs");
        HdfsConfiguration hdfsConfiguration = HdfsConfigurator.detectConfiguration(hdfs);
        HdfsWriterConfiguration hdfsWriterConfiguration  = mapper.convertValue(hdfsConfiguration, HdfsWriterConfiguration.class);
        hdfsWriterConfiguration.setWriterPath(MoreoverProvider.STREAMS_ID);
        hdfsWriterConfiguration.setWriterFilePrefix("data");

        MoreoverProvider moreoverProvider = new MoreoverProvider(moreoverConfiguration);
        WebHdfsPersistWriter hdfsPersistWriter = new WebHdfsPersistWriter(hdfsWriterConfiguration);
        StreamBuilder builder = new LocalStreamBuilder();

        builder.newPerpetualStream(MoreoverProvider.STREAMS_ID, moreoverProvider);
        builder.addStreamsPersistWriter(WebHdfsPersistWriter.STREAMS_ID, hdfsPersistWriter, 1, MoreoverProvider.STREAMS_ID);
        builder.start();

    }

}
