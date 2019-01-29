# Demo 4

This demo extends Demo 3.

## Main points

* Retrieving data from MQTT
* Manipulating `Message` instead of _payload_ (important topic for the next demo as it requires acknowledgement)

## Run the demo

You need 3 terminal windows to run this demo

* Terminal 1 - Run the MQTT broker
```bash
cd demo4
./run-mqtt-broker.sh
```

The broker is stopped using `CTRL+C`

* Terminal 2 - Run the MQTT "sensor" application
```bash
cd demo4
java -cp target/demo4-1.0-SNAPSHOT.jar me.escoffier.reactive_summit.mqtt.Sensor
```

The application is stopped using `CTRL+C`

* Terminal 3 - Run the application
``bash
cd demo4
java -jar target/demo4-1.0-SNAPSHOT.jar
``

Open http://localhost:9000/webroot/index.html.

The UI is the same as _demo 3_ except that the data comes form MQTT.

The application is stopped with `CTRL+C`.

## Points of interest

* The `me.escoffier.reactive_summit.demo4.HealthDataProcessor.process` method:
  
  * Retrieve a stream MQTT Message
  * `MqttMessage` is the envelope. So far we only manipulated payload.
  * `MqttMessage` payloads are `byte[]`
  * We transform the payload into JSON object 
  * We emit messages using the `Message.of` method
    
* The `src/main/resources/META-INF/microprofile-config.properties` file:

  * Configure the access to the MQTT broker
  * Use MicroProfile Config
  * Naming convention - `smallrye.messaging.[source|sink].[name].$attribute=$value`
  
    
