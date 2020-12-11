package curso_kafka.services;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import curso_kafka.dispatcher.CorrelationId;
import curso_kafka.dispatcher.Message;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message>{

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
		
		return jsonObject;
	}

	@Override
	public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException 
	{
		var jsonObject = json.getAsJsonObject();
		var payloadType = jsonObject.get("type").getAsString();
		var correlationId = (CorrelationId) context.deserialize(jsonObject.get("correlationId"), CorrelationId.class);
		
		try 
		{
			/* This opens the change that someone send a class that we cannot work with... one approach to that
			 * would be test the payloadType in some "list of accepted classes". Since we don't want to focus
			 * on this, we are keeping this simple. */
			var payload = context.deserialize(jsonObject.get("payload"), Class.forName(payloadType));
			return new Message(correlationId, payload);
		} 
		catch (JsonParseException | ClassNotFoundException e) 
		{
			throw new JsonParseException(e);
		}
	}

}
