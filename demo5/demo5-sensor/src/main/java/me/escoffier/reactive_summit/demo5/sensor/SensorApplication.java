package me.escoffier.reactive_summit.demo5.sensor;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import me.escoffier.protean.reactive.simulator.measures.Patient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SensorApplication {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    new SensorApplication(vertx).run();
  }

  private final Vertx vertx;
  private final MqttClient mqtt;
  private final Patient patient;

  private static final Logger LOGGER = LogManager.getLogger(SensorApplication.class);

  private SensorApplication(Vertx vertx) {
    this.vertx = vertx;
    this.mqtt = MqttClient.create(vertx);
    Neo neo = new Neo();
    this.patient = neo.getPatient();
  }

  private void run() {
    mqtt.connect(1883, "localhost", this::onConnection);
  }

  private void onConnection(AsyncResult<MqttConnAckMessage> connection) {
    if (connection.failed()) {
      throw new IllegalStateException("Unable to connect to the MQTT broker");
    }

    vertx.setPeriodic(2000, x -> {
      JsonObject measure = patient.measure();
      LOGGER.info("Sending health data to MQTT - {}", measure.encode());
      mqtt.publish("neo", measure.toBuffer(), MqttQoS.AT_LEAST_ONCE, false, false);
    });
  }
}
