# Demo 2

This demo extends Demo 1.

## Main points

* Data manipulation using MicroProfile Reactive Messaging
* How are generated data streams using `@Outgoing`
* How are processed data transiting on the streams using `@Incoming` and `@Outgoing` on the same method.

## Run the demo

``bash
cd demo2
mvn compile shamrock:dev
``

Open http://localhost:8080.

The application is stopped with `CTRL+C`.

## Points of interest

* The `HealthDataBean` class:
  
  * Creates a stream using `@Outgoing` - the name is the _internal topic_ (`health`)
  * The method is a generator paced using a scheduler.
  
* The `HealthDataProcessor` class:

  * A _processor_ method (annotated with `@Incoming` and `@Outgoing`).
  * Manipulate incoming payload and returned a sub-part of it.
  * The application output shows the received payload.  
   
* The `MyWebResource` class:

  * Injection of a `Publisher` using the `@Stream` qualifier
  * Forward it to SSE
  
* With HTTPie: `http :8080/app/beat --stream` to illustrate what is produced by the stream:

```text
$ http :8080/beat --stream
HTTP/1.1 200 OK
Content-Type: text/event-stream
Transfer-Encoding: chunked



data: {"timestamp":1538898103495,"name":"heartbeat","heartbeat":130.88013488658092}

data: {"timestamp":1538898103497,"name":"heartbeat","heartbeat":129.92865716875835}


```   


## Potential questions

* `@Broadcast` - the annotation is not yet in the spec, it allows to dispatch the same data to several subscribers.
* `@Stream` - the annotation is not in the spec, it allows injecting `Publisher` managed by the framework into beans and
JAX-RS resources


## Native packaging

```bash
mvn clean package -Pnative
./target/demo2-1.0-SNAPSHOT-runner
```