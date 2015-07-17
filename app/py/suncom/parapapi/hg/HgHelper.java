package py.suncom.parapapi.hg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class HgHelper {

	private static final String CHANGESET = "changeset:";

	public static void main(String[] args) {
		try {
			getTip();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getTip() throws IOException, InterruptedException {
		String ret = exec("/usr/local/bin/hg tip");
		ret = ret.substring(ret.indexOf(CHANGESET) + CHANGESET.length());
		ret = ret.substring(0, ret.indexOf(":"));
		ret = ret.trim();
		return ret;
	}

	public static String exec(String cmd) throws IOException,
			InterruptedException {
		StringBuffer buf = new StringBuffer();
		String line;
		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		BufferedReader input = new BufferedReader(new InputStreamReader(
				p.exitValue() == 0 ? p.getInputStream() : p.getErrorStream()));
		while ((line = input.readLine()) != null) {
			buf.append(line + System.getProperty("line.separator"));
		}
		input.close();
		return buf.toString();
	}

}
