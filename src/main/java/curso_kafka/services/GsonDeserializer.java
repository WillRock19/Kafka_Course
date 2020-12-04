package curso_kafka.services;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonDeserializer<T> implements Deserializer<T> 
{
	public static final String TYPE_CONFIG = "random_initial_value_of_type_config";
	
	private final Gson gson = new GsonBuilder().create();
	private Class<T> type;

	/* This method will get all of kafka's configuration, that will be passed in the produceProperties
	 * from the consumer services we create. So, to deal with the problem that we have in the deserialize
	 * method, we are going to pass, in the properties, the type of the class to which we are going to
	 * deserialize our bytes, and get that information inside of this configure() method (kafka will give
	 * it to us, thankfully).
	 */
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) 
	{
		/* Since the config.get() may return a null, we cannot use .toString(). So we use valueOf() */
		String typeName = String.valueOf(configs.get(TYPE_CONFIG));

		try 
		{
			/*We need to use it inside a try..catch cause it may launch an exception*/
			this.type = (Class<T>)Class.forName(typeName);
		}
		catch(ClassNotFoundException e) 
		{
			throw new RuntimeException("Type for deserialization does not exist in classpath", e);
		}
	}
	
	@Override
	public T deserialize(String topic, byte[] data) {
		
		/* When we try to deserialize, we have a byte array that is going to be deserialized in a type.
		 * we need to know which is going to be te type to where we are going to deserialize. The GSON
		 * library, different from others, does not try to implicit understand what is this type, so we
		 * have to tell him by passing the type as a second parameter.
		 * 
		 * Then, we are stuck in another problem: how are we going to know that, since a consumer can
		 * be a string, a Order, anything? Well, first of all, we have to get the type. We can't use T, 
		 * because T is a type generated in excution time, not compile time. But, for our luck, the kafka's
		 * Deserializer class has a method we can override to get that information; it is called configure.
		 * 
		 * (NOTE TO SELF: this was written before I wrote the configure method of line 19, so take this 
		 * text in account to understand why it is there)
		 * 
		 * Once we set up the configure() method and the property inside our KafkaService class, we can use 
		 * the type here without problems :)
		 */
		return gson.fromJson(new String(data), type);
	}
}