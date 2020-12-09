package curso_kafka.services;

import org.apache.kafka.common.serialization.Serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import curso_kafka_ecommerce.Message;

//To be able to use this serializer with Kafka, we need to implement the Kafka's serializer interface
public class GsonSerializer<T> implements Serializer<T> 
{
	/* To be able to serialize our object for different payloads (sometimes is a string, other is a Order, or then
	 * can be anything else) we need to create an adapter to GsonBuilder
	 */
	private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();

	@Override
	public byte[] serialize(String topic, T data) {
		return gson.toJson(data).getBytes();
	}
}
