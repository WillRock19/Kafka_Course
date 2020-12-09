package curso_kafka.services;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import curso_kafka_ecommerce.Message;

public class GsonDeserializer<T> implements Deserializer<Message> 
{	
	/* We'll use the same typeAdapter that we've used in the serializer*/
	private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();
	
	@Override
	public Message deserialize(String topic, byte[] data) 
	{	
		return gson.fromJson(new String(data), Message.class);
	}
}