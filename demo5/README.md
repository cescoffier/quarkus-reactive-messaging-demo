# Demo 5

This demo shows event-driven and async microservices. It uses HTTP, MQTT and Kafka.

## Main points

* Using Kafka and MQTT
* Acknowledgement policies
* Using RX Java 2

## Build the demo

Take a deep breathe...

```bash
cd demo5
mvn clean install -Pnative
``` 

Will take a lot of time...

## Run the demo

You need 6 terminal windows to run this demo

* Terminal 1 - Run the MQTT broker
```bash
cd demo5
./run-mqtt-broker.sh
```

The broker is stopped using `CTRL+C`. You can reuse the one started as part of demo 4.

* Terminal 2 - Run the MQTT "sensor" application
```bash
cd demo5/demo5-sensor
./target/demo5-sensor-1.0-SNAPSHOT-runner 
```

The application is stopped using `CTRL+C`

* Terminal 3 - Run Kafka
```bash
cd demo5
./run-kafka-broker.sh
```

The broker is stopped with `CTRL+C`.

* Terminal 4 - Run the collector application

This application retrieves the data from MQTT and store it into Kafka. It also apply different actions on the data.

```bash
cd demo5/demo5-collector
./target/demo5-collector-1.0-SNAPSHOT-runner
```

The application is stopped using `CTRL+C`

* Terminal 5 - The snapshot service

```bash
cd demo5/demo5-snapshot-service
./target/demo5-snapshot-service-1.0-SNAPSHOT-runner 
```

The application is stopped using `CTRL+C`.

* Terminal 6 - The web application

```bash
cd demo5/demo5-web
./target/demo5-web-1.0-SNAPSHOT-runner
```

Open http://localhost:8080.

The UI has a new card (slower) displaying the number of leaps. It's slower because the data is accumulated using a time 
window.

The application is stopped with `CTRL+C`.

## Points of interest

* The `HealthDataCollector` class:
  
  * shows the manipulation of MQTT messages and Kafka messages (key/value)
  * Creation of `KafkaMessage` allows setting the topic, key and value
  * the method `filterTemperature` shows how to build a _processor_ (no parameter) 
  * the method `filterHeartbeat` shows how to build a _processor_ manipulates and returns `PublisherBuilder` objects
  * the method `filterState` method mixes Reactive Streams and RX Java 2 `Flowable`. It filters out identical elements
  * the method `processSteps` method use RX Java 2 and returns a `Flowable`. Notice the `window` operator to sum the 
  number of leaps
  
* The `application.properties` files shows Kafka and MQTT configuration

  * Kafka sink configuration
  * MQTT source configuration  
      
* The `ForwarderProcessors` class:

  * Different acknowledgement policies (required by Kafka)
  * Manual -> you do it, Pre-processing -> before processing the message, Post-processing -> after processing the message
  * Default is either pre or post (post if supported)
  * Manual acknowledgement -> `ack` method is asynchronous
  
  
* The `TemperatureProcessor` class:

  * Show a "sink" in code (`@Incoming` only)
  * For each input, send to the _snapshot_ service    
