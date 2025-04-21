package org.migdb.migdbclient.models.queryconverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AggregateLookup {

	private String collectionName;
	private LinkedHashMap<String, Object> projectPair;
	private MatchCondition matchPairs;
	private List<HashMap<String, Object>> lookupList;
	private HashMap<String, String> aliasPair;

	public AggregateLookup() {
		this.projectPair = new LinkedHashMap<String, Object>();
		this.matchPairs = new MatchCondition();
		this.lookupList = new ArrayList<HashMap<String, Object>>();
		this.aliasPair = new HashMap<String, String>();
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public LinkedHashMap<String, Object> getLimitPair() {
		return projectPair;
	}

	public void setLimitPair(LinkedHashMap<String, Object> limitPair) {
		this.projectPair = limitPair;
	}

	public MatchCondition getMatchPairs() {
		return matchPairs;
	}

	public void setMatchPairs(MatchCondition matchPairs) {

		LinkedHashMap<String, Object> pairs = matchPairs.getMatchPair();
		LinkedHashMap<String, Object> newPairs = new LinkedHashMap<String, Object>();
		for(String key: pairs.keySet()) {
			Object value = pairs.get(key);
			
			if(aliasPair.isEmpty()) {
				if(key.contains(collectionName+".")) {
					newPairs.put("\""+key.replace(collectionName+".", "")+"\"", value);
				} else {
					newPairs.put("\""+key+"\"", value);
				}
			} else {
				Object parentAlias = aliasPair.get(collectionName);
				if(key.contains(parentAlias+".")) {
					newPairs.put("\""+key.replace(aliasPair.get(collectionName)+".", "")+"\"", value);
				} else {
					for(String aliasKey: aliasPair.keySet()) {
						Object alias = aliasPair.get(aliasKey);
						if(key.contains(alias.toString()+".")) {
							newPairs.put("\""+key.replace(alias.toString()+".", aliasKey+"."), value);
						}
					}
				}
			}
			
		}
		this.matchPairs.setMatchPairs(newPairs); 
	}

	public List<HashMap<String, Object>> getLookupList() {
		return lookupList;
	}

	public void setLookupList(List<HashMap<String, Object>> lookupList) {
		this.lookupList = lookupList;
	}

	public HashMap<String, String> getAliasPair() {
		return aliasPair;
	}

	public void setAliasPair(HashMap<String, String> aliasPair) {
		this.aliasPair = aliasPair;
	}
	
	public void addAlias(String collection, String alias) {
		this.aliasPair.put(collection, alias);
	}
	
	public void addProject(String field, String collection) {
		if(collection.equals(collectionName)) {
			if(aliasPair.isEmpty()) {
				this.projectPair.put("\""+field.replace(collection+".", "")+"\"", 1);
			} else {
				this.projectPair.put("\""+field.replace(aliasPair.get(collection)+".", "")+"\"", 1);
			}
		} else {
			if(aliasPair.isEmpty()) {
				this.projectPair.put("\""+field+"\"", 1);
			} else {
				this.projectPair.put("\""+field.replace(aliasPair.get(collection), collection)+"\"", 1);
			}
		}
	}

	public void addLookup(String joinedCollection, String localField, String foreignField, String displayName) {
		LinkedHashMap<String, Object> lookup = new LinkedHashMap<>();
		lookup.put("from", "\""+joinedCollection+"\"");
		lookup.put("localField", "\""+localField+"\"");
		lookup.put("foreignField", "\""+foreignField+"\"");
		lookup.put("as", "\""+displayName+"\"");
		HashMap<String,Object> lookupObj = new HashMap<>();
		lookupObj.put("$lookup", lookup);
		lookupList.add(lookupObj);
		HashMap<String,Object> unwindObj = new HashMap<>();
		unwindObj.put("$unwind", "\"$"+displayName+"\"");
		lookupList.add(unwindObj);
	}

	@Override
	public String toString() {
		String str = "db."+collectionName+".aggregate( \n\t";
		if(!matchPairs.getMatchPair().isEmpty()) {
			str += "{ $match: \n\t\t"+matchPairs.toString()+"\n\t},\n\t";
		}
		String lookup = lookupList.toString().substring(1, lookupList.toString().length()-1);
		str += lookup.replace("=", ": ").replace("{", "{ ").replace("}", " }")
				.replaceFirst("\\{ \\$lookup: \\{", "\\{ \\$lookup: \n\t\t\\{")
				.replace(", { $lookup: {", ",\n\t{ $lookup: \n\t\t{")
				.replace("\t\t{ ", "\t\t{ \n\t\t\t").replace(", ", ",\n\t\t\t")
				.replace("} }", "\n\t\t}\n\t}").replace("\t\t\t{ $unwind", "\t{ $unwind");
	
		if(!projectPair.isEmpty()) {
			str += ",\n\t{ $ptoject: \n\t\t"+projectPair.toString().replace("=", ": ")+"\n\t}";
		}
		return str+"\n])";
	}
	
	
	
	

}
