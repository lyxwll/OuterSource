package com.fourteen.outersource.toast;


import com.fourteen.outersource.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 自定义的Toast
 * @author Administrator
 *
 */
public class CustomToast extends Toast{
	
	/**
     * Show the view or text notification for a short period of time.  This time
     * could be user-definable.  This is the default.
     * @see #setDuration
     */
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;

    /**
     * Show the view or text notification for a long period of time.  This time
     * could be user-definable.
     * @see #setDuration
     */
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;


	public CustomToast(Context context) {
		super(context);
	}

	public static CustomToast makeText(Context context, CharSequence text, int duration) {
		CustomToast result = new CustomToast(context);

        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.custom_toast_layout, null);
        TextView tv = (TextView)v.findViewById(R.id.message);
        tv.setText(text);
        
        result.setView(v);
        result.setDuration(duration);
        return result;
    }
	
	public static CustomToast makeText(Context context, int resId, int duration) {
		return makeText(context, context.getResources().getString(resId), duration);
	}
}
