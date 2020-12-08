package curso_kafka.models;

public class User 
{
	private final String uuid;
	
	public User(String uuid) 
	{
		this.uuid = uuid;
	}

	public String getReportPath() {
		return "target/" + uuid + "_report.txt";
	}

	public String getUUID() {
		return this.uuid;
	}
}
