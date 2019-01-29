package mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AsyncResult;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.mqtt.MqttClient;
import io.vertx.reactivex.mqtt.messages.MqttConnAckMessage;
import me.escoffier.protean.reactive.Neo;
import me.escoffier.protean.reactive.simulator.measures.Patient;

public class Sensor {


  private final Vertx vertx;
  private final MqttClient mqtt;
  private final Neo neo;
  private final Patient patient;

  public Sensor(Vertx vertx) {
    this.vertx = vertx;
    this.mqtt = MqttClient.create(vertx);
    this.neo = new Neo();
    this.patient = neo.getPatient();
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    new Sensor(vertx).run();
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
      mqtt.publish("neo", new Buffer(patient.measure().toBuffer()), MqttQoS.AT_LEAST_ONCE, false, false, done -> {
        System.out.println("Has been sent: " + done.succeeded());
      });
    });

  }
}
