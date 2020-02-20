package com.cfcc.itfe.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

public class DBMonitorTools {

	/**
	 * @param args
	 */
	public static void arrangeInfo(String resorcePath,String targetPath) {
		HashMap<String, A> map = new HashMap<String, A>();
		try {
			BufferedReader br = null;

			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					"c:/statmon.log")));
			String data = null;
			String ttt = "";
			int index=0;
			while ((data = br.readLine()) != null) {
				index++;
				if (data.trim().equals("")) {
					continue;
				}

				if (data.indexOf("Text     :") >= 0) {
					index=0;
					String sql = data.replace("Text     :", "");
					ttt = sql;
					if (map.containsKey(sql)) {
						map.get(sql).setIndex(map.get(sql).getIndex() + 1);
					} else {
						A x = new A();
						x.setIndex(1);
						x.setSqltime(new BigDecimal("0.00"));
						map.put(sql, x);
					}
				} else if (data.indexOf("Elapsed Execution Time:") >= 0&& index ==4) {
					if (ttt.equals("")) {
						continue;
					}
					String time = data.replace("Elapsed Execution Time:", "");
					time = time.replace("seconds", "");
					BigDecimal ddd = new BigDecimal(time.trim());
					BigDecimal x = map.get(ttt).getSqltime().add(ddd);
					map.get(ttt).setSqltime(x);
				}

			}
			br.close();
			Iterator<String> it = map.keySet().iterator();
			StringBuffer buf = new StringBuffer();
			while (it.hasNext()) {
				String sql = it.next();
				A aaa = map.get(sql);
				buf.append("\"" + sql + "\",");
				buf.append("" + aaa.getIndex() + ",+"
						+ aaa.getSqltime() + "\n");
			}
			FileOutputStream output = null;
			BufferedOutputStream bos = null;
			File f = new File("c:/222.txt");
			File dir = new File(f.getParent());

			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (!dir.exists()) {
				dir.mkdirs();
			}
			output = new FileOutputStream("c:/222.txt");
			bos = new BufferedOutputStream(output, 10240);
			bos.write(buf.toString().getBytes());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

class A {
	BigDecimal sqltime;
	int index;



	public BigDecimal getSqltime() {
		return sqltime;
	}

	public void setSqltime(BigDecimal sqltime) {
		this.sqltime = sqltime;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}