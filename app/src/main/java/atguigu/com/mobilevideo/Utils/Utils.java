package atguigu.com.mobilevideo.Utils;

import java.util.Formatter;
import java.util.Locale;

public class Utils {

	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;

	public Utils() {
		// ת�����ַ�����ʱ��
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

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
}
