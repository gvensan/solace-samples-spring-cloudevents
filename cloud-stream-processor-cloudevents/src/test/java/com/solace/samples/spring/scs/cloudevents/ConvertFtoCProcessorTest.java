/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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

package com.solace.samples.spring.scs.cloudevents;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.function.cloudevent.CloudEventMessageBuilder;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.support.channel.BeanFactoryChannelResolver;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.solace.samples.spring.common.SensorReading;
import com.solace.samples.spring.common.SensorReading.BaseUnit;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ConvertFtoCProcessorTest {
	private static final Logger log = LoggerFactory.getLogger(ConvertFtoCProcessorTest.class);

	@Autowired
	private MessageCollector collector;
	
	@Autowired
	private ApplicationContext context;

	@Test
	public void testFeaturesProcessor() throws InterruptedException {
		
		double temperature = 70.0d;
		SensorReading reading = new SensorReading("test", temperature, BaseUnit.FAHRENHEIT);
		Message<SensorReading> msgInput =  CloudEventMessageBuilder
				.withData(reading)
				.setId(UUID.randomUUID().toString())
				.setSource(URI.create("https://spring.cloudevents.sample"))
				.setSpecVersion("1.0")
				.setDataContentType("application/json")
				.setType("com.solace.samples.spring.scs.cloudevents")
				.build();
				
		// NOTE: Notice the .buiild("ce_") statement. We are forcing the header attribute prefix due to an open issue
		// Check out https://github.com/cloudevents/sdk-java/issues/359
				

		BeanFactoryChannelResolver channelResolver = context.getBean("integrationChannelResolver",
				BeanFactoryChannelResolver.class);
		MessageChannel input = channelResolver.resolveDestination("convertFtoC-in-0");
		MessageChannel output = channelResolver.resolveDestination("convertFtoC-out-0");
		
		assertNotNull(msgInput.toString());
		input.send(msgInput);
		
		
		Message<?> msgOutput = (Message<?>) collector.forChannel(output).poll(5, TimeUnit.SECONDS);
		Object headers = (msgOutput != null) ? msgOutput.getHeaders() : null;
		Object payload = (msgOutput != null) ? msgOutput.getPayload() : null;
		log.info("Received (F) Headers: " + headers.toString());
		log.info("Received (F) Payload: " + payload);
		
		assertNotNull(payload);
		assertThat((String) payload,
				allOf(containsString("sensorID"), containsString("temperature"), containsString("baseUnit"),
						containsString("timestamp"), containsString("CELSIUS"), containsString("21.1")));
		
		
		assertNotNull(headers);
		assertThat((String) headers.toString(), allOf(containsString("ce_source"), containsString("ce_datacontenttype"),
				containsString("ce_specversion"), containsString("ce_type"), containsString("ce_id")));
		
	}

}
