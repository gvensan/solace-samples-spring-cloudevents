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

import java.net.URI;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.cloudevent.CloudEventMessageBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import com.solace.samples.spring.common.SensorReading;

@SpringBootApplication
public class FahrenheitTempSource {
	private static final Logger log = LoggerFactory.getLogger(FahrenheitTempSource.class);

	private static final UUID sensorIdentifier = UUID.randomUUID();
	private static final Random random = new Random(System.currentTimeMillis());
	private static final int RANDOM_MULTIPLIER = 100;

	public static void main(String[] args) {
		SpringApplication.run(FahrenheitTempSource.class);
	}

	/* 
	 * Basic Supplier which sends messages every X milliseconds
	 * Configurable using spring.cloud.stream.poller.fixed-delay 
	 */
		
	@Bean
	public Supplier<Message<SensorReading>> emitSensorReading() {
		return () -> {
			SensorReading reading = new SensorReading();

			double temperatureCelsius = random.nextDouble() * RANDOM_MULTIPLIER;
			reading.setSensorID(sensorIdentifier.toString());
			reading.setTemperature(temperatureCelsius);
			reading.setBaseUnit(SensorReading.BaseUnit.FAHRENHEIT);


			Message<SensorReading> fahrenheitReading =  CloudEventMessageBuilder
					.withData(reading)
					.setId(UUID.randomUUID().toString())
					.setSource(URI.create("https://spring.cloudevents.sample"))
					.setSpecVersion("1.0")
					.setDataContentType("application/json")
					.setType("com.solace.samples.spring.scs.cloudevents")
					.build();

			log.info("Sending (F) Headers: " + fahrenheitReading.getHeaders());
			log.info("Sending (F) Payload: " + fahrenheitReading.getPayload());
			return fahrenheitReading;
		};
	}

}
