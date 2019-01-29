# Demo 3

This demo extends Demo 2.

## Main points

* Stream manipulation using Reactive Stream Operators
* How to compose an asynchronous operation in the pipeline

## Run the demo

``bash
cd demo3
java -jar target/demo3-1.0-SNAPSHOT.jar
``

Open http://localhost:9000/webroot/index.html.

On the UI, another card is displayed (temperature). The data is retrieved by periodic polling from the _snapshot_ service.

The application is stopped with `CTRL+C`.

## Points of interest

* The `me.escoffier.reactive_summit.demo3.HealthDataProcessor` class:
  
  * Rewrite of the processor method to manipulate streams using MicroProfile Reactive Stream Operators
  * `PublisherBuilder` can be seen as a Reactive Streams Publisher with operators.
  * `flatMapCompletionStage` allow sequential composition of asynchronous action in the processing pipeline
  
