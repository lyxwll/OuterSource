package com.fourteen.outersource.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class PackageManagerUtils {

	/**
	 * 检测是否有应用能够处理该intent
	 * @param context
	 * @param intent
	 * @return true if application can hold this intent
	 */
	public static boolean isIntentAvailable(Context context, final Intent intent) {
		final PackageManager packageManager = context.getPackageManager();
		// final Intent intent = new Intent(action);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);

		if (resolveInfo.size() > 0) {
			return true;
		}
		return false;
	}
}
