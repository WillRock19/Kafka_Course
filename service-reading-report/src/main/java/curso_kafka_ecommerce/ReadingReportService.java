package curso_kafka_ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.models.User;

public class ReadingReportService {

	private static final Path source = new File("src/main/resources/report.txt").toPath();
	
	public static void main(String[] args) {
		var readingReportService = new ReadingReportService();
		
		try(var service = new KafkaService<>(
				ReadingReportService.class.getTypeName(), 
				"ECOMMERCE_USER_GENERATE_READING_REPORT",  
				readingReportService::parseRecord,
				User.class,
				new HashMap<>()))
		{
			service.run();
		}	
	}

	private void parseRecord(ConsumerRecord<String, Message<User>> record) throws IOException 
	{
		var message = record.value();
		var user = message.getPayload();
		
		System.out.println("--------------------------------------------");
		System.out.println("Processing report for " + user);
		
		var target = new File(user.getReportPath());
		IO.copyTo(source, target);
		IO.append(target, "Created for " + user.getUUID());
		
		System.out.println("File created: " + target.getAbsolutePath());
	}
}
