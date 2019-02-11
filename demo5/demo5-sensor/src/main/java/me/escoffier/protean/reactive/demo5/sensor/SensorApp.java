package me.escoffier.protean.reactive.demo5.sensor;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import me.escoffier.protean.reactive.simulator.measures.Patient;

public class SensorApp {
  private final Vertx vertx;
  private final MqttClient mqtt;
  private final Patient patient;

  public SensorApp(Vertx vertx) {
    this.vertx = vertx;
    this.mqtt = MqttClient.create(vertx);
    this.patient = new Neo().getPatient();
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    new SensorApp(vertx).run();
  }

  private void run() {
    mqtt.connect(1883, "localhost", this::onConnection);
  }


  private void onConnection(AsyncResult<MqttConnAckMessage> connection) {
    if (connection.failed()) {
      throw new IllegalStateException("Unable to connect to the MQTT broker");
    }

    vertx.setPeriodic(2000, x -> {
      System.out.println("sending...");
      mqtt.publish("neo", patient.measure().toBuffer(), MqttQoS.AT_LEAST_ONCE, false, false,
        done -> System.out.println("Has been sent: " + done.succeeded()));
    });
  }
}
