# Solace Samples - Spring & CloudEvents

Demonstrates how the CloudEvents, a common data format for events, can be used with Spring Cloud Stream, enabling applications to exchange messages via Solace PubSub+ Broker.

## Prerequisites

Install the data model
``` bash
cd spring-samples-datamodel
mvn clean install
```

## Running the Samples

To try individual samples, go into the project directory and run the sample using maven.

To run CloudEvents producer:
``` bash
cd cloud-streams-source-cloudevents
mvn spring-boot:run
```
To run CloudEvents processor:
``` bash
cd cloud-streams-processor-cloudevents
mvn spring-boot:run
```


