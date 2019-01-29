# Demo 5

This demo shows event-driven and async microservices. It uses HTTP, MQTT and Kafka.

## Main points

* Using Kafka and MQTT
* Acknowledgement policies
* Using RX Java 2 

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
java -jar target/demo5-sensor-1.0-SNAPSHOT.jar 
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

``bash
cd demo5/demo5-collector
java -jar target/demo5-collector-1.0-SNAPSHOT.jar
``

The application is stopped using `CTRL+C`

* Terminal 5 - The snapshot service

In previous demos, the service was running in the same process. In this version, it's a separated process.

``bash
cd demo5/demo5-snapshot-service
java -jar target/demo5-snapshot-service-1.0-SNAPSHOT.jar 
``

The application is stopped using `CTRL+C`.

* Terminal 6 - The web application

```bash
cd demo5/demo5-web
java -jar target/demo5-web-1.0-SNAPSHOT.jar
```

Open http://localhost:9000/webroot/index.html.

The UI has a new card (slower) displaying the number of leaps. It's slower because the data is accumulated using a time 
window.

The application is stopped with `CTRL+C`.

## Points of interest

* The `me.escoffier.reactive_summit.demo5.collector.HealthDataCollector` class:
  
  * shows the manipulation of MQTT messages and Kafka messages (key/value)
  * Creation of `KafkaMessage` allows setting the topic, key and value
  * the method `filterTemperature` shows how to build a _processor_ (no parameter) 
  * the method `filterHeartbeat` shows how to build a _processor_ manipulates and returns `PublisherBuilder` objects
  * the method `filterState` method mixes Reactive Streams and RX Java 2 `Flowable`. It filters out identical elements
  * the method `processSteps` method use RX Java 2 and returns a `Flowable`. Notice the `window` operator to sum the 
  number of leaps
  
* The `META-INF/microprofile-config.properties` files shows Kafka and MQTT configuration

  * Kafka sink configuration
  * MQTT source configuration  
      
* The `me.escoffier.reactive_summit.demo5.ForwarderProcessors` class:

  * Different acknowledgement policies (required by Kafka)
  * Manual -> you do it, Pre-processing -> before processing the message, Post-processing -> after processing the message
  * Default is either pre or post (post if supported)
  * Manual acknowledgement -> `ack` method is asynchronous
  
  
* The `me.escoffier.reactive_summit.demo5.TemperatureProcessor` class:

  * Show a "sink" in code (`@Incoming` only)
  * For each input, send to the _snapshot_ service    
