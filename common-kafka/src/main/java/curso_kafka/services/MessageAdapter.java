package curso_kafka.services;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import curso_kafka_ecommerce.Message;

public class MessageAdapter implements JsonSerializer<Message>{

	@Override
	public JsonElement serialize(Message message, Type typeOfSrc, JsonSerializationContext context) 
	{
		/*Creates an empty json object (in other words: {})*/
		var jsonObject = new JsonObject();
		
		/*Creating a property in my json*/
		jsonObject.addProperty("type", message.getPayload().getClass().getName());
		
		/*Creating objects inside my json*/
		jsonObject.add("payload", context.serialize(message.getPayload()));
		jsonObject.add("correlationId", context.serialize(message.getCorrelationId()));
		
		return null;
	}

}
