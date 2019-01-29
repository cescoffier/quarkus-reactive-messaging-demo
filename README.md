# Bring Reactive to Java Enterprise Developer demo

## Prerequisites

* SmallRye Reactive Stream Operators implementation

```bash
git clone git@github.com:smallrye/smallrye-reactive-streams-operators.git
cd smallrye-reactive-streams-operators
mvn clean install
```

* SmallRye Reactive Messaging implementation
```bash
git clone git@gitlab.com:clement-escoffier/smallrye-reactive-messaging-provider.git
cd smallrye-reactive-messaging-provider
mvn clean install
```

## Build

```bash
mvn clean install
```

## Demos

### Demo 1

The demo 1 illustrates how you can bridge _Reactive Streams_ and Jax-RS. It forwards data from a `Publisher` into a 
_SSE_ stream.

### Demo 2

The demo 2 illustrates how to use MicroProfile Reactive Messaging to process data conveyed into a _Reactive Stream_. It 
shows how to generate streams using  MicroProfile Reactive Messaging `@Outgoing` annotation and how the items are 
manipulated one by one as in:
 
```java
  @Incoming("health")
  @Outgoing("heartbeat")
  public JsonObject filtered(JsonObject input) {
    LOGGER.info("Received {}", input.encode());
    return input.getJsonObject("heartbeat");
  }
```

### Demo 3

The demo 3 extends demo 2 but manipulate streams instead of individual items. It also shows how asynchronous action (here
a call to a remote service) can be integrated into the pipeline.

### Demo 4

The demo 4 introduces transport connectors. The data is no more generated in the same application, but in another 
application and sent to a MQTT broker. The web application retrieves the data from MQTT and send it to the web frontend.

### Demo 5

Demo 5 is all about event-driven and asynchronous microservices. It is composed by:

* a sensor application sending data to a MQTT broker
* a processor application manipulating data from MQTT and forwarding it to Kafka
* a snapshot service which is a simple HTTP microservice
* a web application retrieving the data from Kafka, acknowledge the messages and forward them to the UI.  

This demo shows:

* Kafka and MQTT support
* Acknowledgement policies
