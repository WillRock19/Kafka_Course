package curso_kafka_ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.consumer.ServiceRunner;
import curso_kafka.consumer.interfaces.IConsumerService;
import curso_kafka.dispatcher.Message;
import curso_kafka.models.User;

public class ReadingReportService implements IConsumerService<User> {

	private static final Path source = new File("src/main/resources/report.txt").toPath();
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException 
	{
		var numberOfThreads = 5;
		new ServiceRunner<>(ReadingReportService::new).start(numberOfThreads);
	}
	
	@Override
	public String getTopic() {
		return "ECOMMERCE_USER_GENERATE_READING_REPORT";
	}

	@Override
	public String getConsumerGroup() {
		return ReadingReportService.class.getTypeName();
	}

	public void parseRecord(ConsumerRecord<String, Message<User>> record) throws IOException 
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
