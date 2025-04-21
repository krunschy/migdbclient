/**
 * 
 */
package org.migdb.migdbclient.config;

/**
 * @author KANI
 *
 */
public enum NotificationConfig {
	
	SHOWSUCCESS("success"),SHOWWARNING("warning"),SHOWERROR("error");
	
	private String info;

	/**
	 * @param info
	 */
	NotificationConfig(String info) {
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

}
