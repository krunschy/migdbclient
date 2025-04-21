package org.migdb.migdbclient.config.fromResources;

import org.json.simple.JSONObject;

public class MappingRequestMessage {

	private JSONObject messageBody;

	public MappingRequestMessage(String clientId, String requestId, String columnCount, String numericCount, String stringCount,
			String calenderCount) {
		messageBody = new JSONObject();
		messageBody.put("clientId", clientId);
		messageBody.put("requestId", requestId);
		messageBody.put("columnCount", columnCount);
		messageBody.put("numericCount", numericCount);
		messageBody.put("stringCount", stringCount);
		messageBody.put("calenderCount", calenderCount);
		System.out.println(messageBody);

	}

	public JSONObject getMessageBody() {
		return messageBody;
	}
	
}
