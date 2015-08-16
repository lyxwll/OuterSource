package com.fourteen.outersource.notification;

import java.util.ArrayList;

public class SavedNotificationList {
	
	public static int NOTIFICATION_BASE = 1000;
	public static Integer notificationIndex = 0;
	private static ArrayList<Integer> notificationIdList = new ArrayList<Integer>();
		
	public static void addNotificationId(){
		synchronized (notificationIndex) {
			notificationIndex ++;
		}
	}
	
	public static void pushNotification(int notificationId){
		synchronized (notificationIdList) {
			notificationIdList.add(notificationId);
		}
	}
	
	public static void removeNotification(int notificationId){
		synchronized (notificationIdList) {
			notificationIdList.remove(Integer.valueOf(notificationId));
		}
	}
}
