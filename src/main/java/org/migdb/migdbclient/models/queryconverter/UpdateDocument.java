package org.migdb.migdbclient.models.queryconverter;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class UpdateDocument {

	private String collectionName;
	private LinkedHashMap<String, Object> updatePairs;
	private MatchCondition matchPairs;
	
	public UpdateDocument(String collectionName) {
		this.collectionName = collectionName;
		this.updatePairs = new LinkedHashMap<String, Object>();
		this.matchPairs = new MatchCondition();
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public LinkedHashMap<String, Object> getUpdatePairs() {
		return updatePairs;
	}

	public void setUpdatePairs(LinkedHashMap<String, Object> updatePairs) {
		this.updatePairs = updatePairs;
	}
	
	public MatchCondition getMatchPairs() {
		return matchPairs;
	}

	public void setMatchPairs(MatchCondition matchPairs) {
		this.matchPairs = matchPairs;
	}

	public void addUpdatePair(String key, Object value, String type) {
		HashMap<String, Object> pair = new HashMap<String, Object>();		
		if(type.equals("inc") || type.equals("sub")) {
			HashMap<String, Object> incObj = (HashMap<String, Object>) updatePairs.get("$inc");
			if(type.equals("inc")) {			
				if(incObj == null) {
					pair.put(key, value);
					updatePairs.put("$inc", pair);
				} else {
					incObj.put(key, value);
				}
			} else if(type.equals("sub")) {
				if(incObj == null) {
					pair.put(key, "-"+value);
					updatePairs.put("$inc", pair);
				} else {
					incObj.put(key, "-"+value);
				}
			}
		} else if(type.equals("mul") || type.equals("div"))  {
			HashMap<String, Object> mulObj = (HashMap<String, Object>) updatePairs.get("$mul");
			if(type.equals("mul")) {
				if(mulObj == null) {
					pair.put(key, value);
					updatePairs.put("$mul", pair);
				} else {
					mulObj.put(key, value);
				}	
			} else if(type.equals("div")) {
				if(mulObj == null) {
					pair.put(key, "1/"+value);
					updatePairs.put("$mul", pair);
				} else {
					mulObj.put(key, "1/"+value);
				}
			}
		} else {
			HashMap<String, Object> setObj = (HashMap<String, Object>) updatePairs.get("$set");
			if(type.equals("long")) {
				if(setObj == null) {
					pair.put(key, "NumberLong(\""+value+"\")");
					updatePairs.put("$set", pair);
				} else {
					setObj.put(key, "NumberLong(\""+value+"\")");
				}
			} else if(type.equals("date")) {
				if(setObj == null) {
					pair.put(key, "new Date(\""+value+"\")");
					updatePairs.put("$set", pair);
				} else {
					setObj.put(key, "new Date(\""+value+"\")");
				}	
			} else {
				if(setObj == null) {
					pair.put(key, value);
					updatePairs.put("$set", pair);
				} else {
					setObj.put(key, value);
				}
			}
		}
	}

	@Override
	public String toString() {
		String str = updatePairs.toString().replace("=", ": ").replace("{", "{ ").replace("}", " }");
		return "db."+collectionName+".update( \n\t"+matchPairs.toString()+", \n\t"+str+", \n\t{ multi: true } \n)";
	}
	
	
	
}
