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
import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.cloudevent.CloudEventHeaderEnricher;
import org.springframework.cloud.function.cloudevent.CloudEventMessageBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import com.solace.samples.spring.common.SensorReading;

@SpringBootApplication
public class ConvertFtoCProcessor {

	private static final Logger log = LoggerFactory.getLogger(ConvertFtoCProcessor.class);

	public static void main(String[] args) {
		SpringApplication.run(ConvertFtoCProcessor.class, args);
	}
	
	// Spring Cloud Function to convert SensorReading data from Fahreinheit => Celsius //

	
	// OPTION 1: Allowing non-functional aspects into the 
	// business logic (construction of CloudEvent, decorating attributes etc)
	
	@Bean
	public Function<Message<SensorReading>, Message<SensorReading>> convertFtoC() {
		return message -> {
			SensorReading reading = message.getPayload();
			log.info("Received (F): " + reading);

			double temperatureCelsius = (reading.getTemperature().doubleValue() - 32) * 5 / 9;
			reading.setTemperature(temperatureCelsius);
			reading.setBaseUnit(SensorReading.BaseUnit.CELSIUS);


			Message<SensorReading> celsiusReading =  CloudEventMessageBuilder
														.withData(reading)
														.setId(UUID.randomUUID().toString())
														.setSource(URI.create("https://spring.cloudevenets.sample"))
														.setSpecVersion("1.0")
														.setDataContentType("application/json")
														.setType("com.solace.samples.spring.scs.cloudevents")
														.build();
			log.info("Sending (C) Headers: " + celsiusReading.getHeaders());
			log.info("Sending (C) Payload: " + celsiusReading.getPayload());
			return celsiusReading;
		};
	}
	
	// OPTION 2: Keeping the non-functional aspects separate from the business logic
	// To enable this 
	//	a) comment the function blocks:
	// 			Function<Message<byte[]>, Message<SensorReading>> convertFtoC()
	//			Function<Message<byte[]>, Message<byte[]>> convertFtoC()
	//  b) uncomment the function blocks:
	
//	@Bean
//	public Function<SensorReading, Message<SensorReading>> convertFtoC() {
//		return f_reading -> {
//			log.info("Received (F): " + f_reading);
//
//			double temperatureCelsius = (f_reading.getTemperature().doubleValue() - 32) * 5 / 9;
//			SensorReading c_reading = new SensorReading(
//												f_reading.getSensorID(),
//												temperatureCelsius,
//												SensorReading.BaseUnit.CELSIUS);
//			Message<SensorReading> celsiusReading = CloudEventMessageBuilder
//															.withData(c_reading)
//													        .build();
//			log.info("Sending (C) Headers: " + celsiusReading.getHeaders());
//			log.info("Sending (C) Payload: " + celsiusReading.getPayload());
//			return celsiusReading;
//		};
//	}
//	
//	@Bean
//	public CloudEventHeaderEnricher enrich() {
//		return headers -> headers
//							.setId(UUID.randomUUID().toString())
//							.setSource(URI.create("https://spring.cloudevenets.sample"))
//							.setSpecVersion("1.0")
//							.setDataContentType("application/json")
//							.setType("com.solace.samples.spring.scs.cloudevents");
//	}
	
		
}
