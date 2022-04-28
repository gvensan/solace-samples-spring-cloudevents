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
To run CloudEvents consumer:
``` bash
cd cloud-streams-sink-cloudevents
mvn spring-boot:run
```

## CloudEvents Data Format encoding

CloudEvents supports structured and binary format encoding. In the samples, necessary code block is built but commented out for brevity. 

Current flow in the code:

1) Source (CloudEvent w/ Binary format encoding)
2) Processor (
        Incoming Message: CloudEvent w/ Binary format encoding),
        Outgoing Message: CloudEvent w/ Structured format encoding)
    )
3) Sink (CloudEvent w/ Binary format encoding)

Feel free to comment/uncomment respective blocks to try out other combination (mix) of binary & structured format encoding.




