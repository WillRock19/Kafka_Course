package curso_kafka.models;

/* Class created so we can use the GsonDeserializer inside our KafkaService (see comments there to know more)
 */
public class Email 
{
	private final String subject, body;

	public Email(String subject, String body) 
	{
		this.subject = subject;
		this.body = body;
	}
}
