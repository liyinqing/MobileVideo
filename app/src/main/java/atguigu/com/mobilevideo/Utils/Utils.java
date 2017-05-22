package atguigu.com.mobilevideo.Utils;

import android.content.Context;
import android.net.TrafficStats;

import java.util.Formatter;
import java.util.Locale;

public class Utils {

	private final Context context;
	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;

	public Utils(Context context) {
		// ת�����ַ�����ʱ��
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
		this.context = context;
	}

	/**
	 * �Ѻ���ת���ɣ�1:20:30������ʽ
	 * @param timeMs
	 * @return
	 */
	public String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int seconds = totalSeconds % 60;
		
		int minutes = (totalSeconds / 60) % 60;
		
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}


	/**
	 * 是否是网络资源
	 * @param data
	 * @return
	 */
	public boolean isNetUri(String data) {
		boolean isNetUri = false;
		if (data != null) {
			if (data.toLowerCase().startsWith("http") || data.toLowerCase().startsWith("mms") || data.toLowerCase().startsWith("rtsp")) {
				//网络资源
				isNetUri = true;
			}
		}
		return isNetUri;
	}

	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;
	public String showNetSpeed() {

		long nowTotalRxBytes = getTotalRxBytes();
		long nowTimeStamp = System.currentTimeMillis();
		long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

		lastTimeStamp = nowTimeStamp;
		lastTotalRxBytes = nowTotalRxBytes;
		String msg  = String.valueOf(speed) + " kb/s";
		return msg;

	}
	private long getTotalRxBytes() {
		return TrafficStats.getUidRxBytes( context.getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
	}
}
