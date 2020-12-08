package curso_kafka_ecommerce;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.models.User;

public class ReadingReportService {

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

	private void parseRecord(ConsumerRecord<String, User> record) throws InterruptedException, ExecutionException 
	{
		System.out.println("--------------------------------------------");
		System.out.println("Processing report for " + record.value());
		
	
	}
}
