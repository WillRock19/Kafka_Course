package curso_kafka.services;

import org.apache.kafka.common.serialization.Serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//To be able to use this serializer with Kafka, we need to implement the Kafka's serializer interface
public class GsonSerializer<T> implements Serializer<T> 
{
	private final Gson gson = new GsonBuilder().create();

	@Override
	public byte[] serialize(String topic, T data) {
		return gson.toJson(data).getBytes();
	}
}
