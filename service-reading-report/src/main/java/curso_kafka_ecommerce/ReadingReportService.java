package curso_kafka_ecommerce;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.models.User;

public class ReadingReportService {

	private static final Path source = new File("src/main/resources/Report.txt").toPath();
	private final KafkaDispatcher<User> orderDispatcher = new KafkaDispatcher<User>();
	
	public static void main(String[] args) {
		var readingReportService = new ReadingReportService();
		
		try(var service = new KafkaService<>(
				ReadingReportService.class.getTypeName(), 
				"USER_GENERATE_READING_REPORT",  
				readingReportService::parseRecord,
				User.class,
				new HashMap<>()))
		{
			service.run();
		}	
	}

	private void parseRecord(ConsumerRecord<String, User> record) throws IOException 
	{
		var user = record.value();
		
		System.out.println("--------------------------------------------");
		System.out.println("Processing report for " + user);
		
		var target = new File(user.getReportPath());
		IO.copyTo(source, target);
		IO.append(target, "Created for " + user.getUUID());
		
		System.out.println("File created: " + target.getAbsolutePath());
	}
}
