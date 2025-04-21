/**
 * 
 */
package org.migdb.migdbclient.utils;

import org.controlsfx.control.Notifications;
import org.migdb.migdbclient.config.NotificationConfig;

import javafx.geometry.Pos;
import javafx.util.Duration;

/**
 * @author KANI
 *
 */
public class MigDBNotifier {

	private String title;
	private String message;
	private String notificationType;
	private int showTime;
	
	

	/**
	 * Constructor for create to defined notification
	 * @param title
	 * @param message
	 * @param notificationType
	 * @param showTime
	 */
	public MigDBNotifier(String title, String message, String notificationType, int showTime) {
		super();
		this.title = title;
		this.message = message;
		this.notificationType = notificationType;
		this.showTime = showTime;
	}

	/**
	 * Defined notification create method
	 */
	public void createDefinedNotification() {
		
		if(notificationType.equals(NotificationConfig.SHOWSUCCESS.getInfo())){
			Notifications.create().title(title).darkStyle()
			.text(message)
			.position(Pos.BOTTOM_RIGHT)
			.hideAfter(Duration.seconds(showTime))
			.showInformation();
		} else if (notificationType.equals(NotificationConfig.SHOWWARNING.getInfo())) {
			Notifications.create().title(title).darkStyle()
			.text(message)
			.position(Pos.BOTTOM_RIGHT)
			.hideAfter(Duration.seconds(showTime))
			.showWarning();
		} else {
			Notifications.create().title(title).darkStyle()
			.text(message)
			.position(Pos.BOTTOM_RIGHT)
			.hideAfter(Duration.seconds(showTime))
			.showError();
		}
	}

}
