package org.migdb.migdbclient.models.queryconverter;

public class RemoveDocument {
	
	private String collectionName;
	private MatchCondition matchPairs;
	
	public RemoveDocument(String collectionName) {
		this.collectionName = collectionName;
		this.matchPairs = new MatchCondition();
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public MatchCondition getMatchPairs() {
		return matchPairs;
	}

	public void setMatchPairs(MatchCondition matchPairs) {
		this.matchPairs = matchPairs;
	}

	@Override
	public String toString() {
		String str = "";
		if(!matchPairs.getMatchPair().isEmpty()) {
			str = "\n\t"+matchPairs.toString()+"\n";
		} else {
			str = matchPairs.toString();
		}
		return "db."+collectionName+".remove("+str+")";
	}
	
	

}
