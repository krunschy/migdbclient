/**
 * 
 */
package org.migdb.migdbclient.config.fromResources.threads;

/**
 * @author HP
 *
 */
public enum ConnectivityIsShowInstance {
	INSTANCE;

	private boolean isShow;

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

}
