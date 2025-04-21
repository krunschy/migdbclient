package org.migdb.migdbclient.config.fromResources;

import org.json.simple.JSONObject;

public class DataSetUpdateRequestMessage {

	private JSONObject messageBody;

	public DataSetUpdateRequestMessage(String clientId, String columnCount,
			String numericCount, String stringCount,String calenderCount, String mappingModel) {
		messageBody = new JSONObject();
		messageBody.put("clientId", clientId);
		messageBody.put("columnCount", columnCount);
		messageBody.put("numericCount", numericCount);
		messageBody.put("stringCount", stringCount);
		messageBody.put("calenderCount", calenderCount);
		messageBody.put("mappingModel", mappingModel);
		System.out.println(messageBody);

	}

	public JSONObject getMessageBody() {
		return messageBody;
	}

}
