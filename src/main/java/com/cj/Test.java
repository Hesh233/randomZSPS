package com.cj;
import com.alibaba.fastjson.JSON;
import java.io.BufferedReader;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;  
public class Test {

	public static void main(String[] args) {
		InputStreamReader reader;
//		System.out.println(System.getProperty("user.dir"));
//		String line = "";
//		String lines = "";
//		try {
//			reader = new InputStreamReader(
//					new FileInputStream("src\\main\\java\\com\\cj\\mapConfig.txt"));
//			BufferedReader br = new BufferedReader(reader); 
//			while (line != null) {
//					line = br.readLine(); // 一次读入一行数据
//					if(line!=null) {
//						lines += line;
//					}
//			}
//			br.close();
//			System.out.println(lines);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		JSONObject  jsonObject = JSONObject.parseObject(lines);
//		System.out.println(jsonObject.get("pool1"));
//		System.out.println(jsonObject.get("pool1").getClass().getName());
//		JSONArray poolArray = (JSONArray)jsonObject.get("pool1");
//		JSONObject poolJson = poolArray.getJSONObject(0);
//		System.out.println(poolJson);
//		System.out.println(poolJson.get("duihuan").getClass().getName());
//		Random random =new Random();
//		double precent = random.nextDouble()*0.1;	
//		System.out.println(precent);
//		String text = "{\r\n" + 
//				"\"userId\":\"1234567\",\r\n" + 
//				"\"item01\":\"100\",\r\n" + 
//				"\"item02\":\"50\",\r\n" + 
//				"\"item03\":\"10\",\r\n" + 
//				"\"item1007\":\"20\"\r\n" + 
//				"\"baodi\":{\r\n" + 
//				"\"pool1\":\"20\",\r\n" + 
//				"\"pool2\":\"0\",\r\n" + 
//				"\"pool3\":\"10\"}\r\n" + 
//				"}";
//				String pattern = "pool\":\"(.*?)\"";
//				Pattern r = Pattern.compile(pattern);
//				Matcher m = r.matcher(text);
////				boolean isMatch = Pattern.matches(pattern, text);
//				text.replaceAll("pool\":\"(.*?)\"", "70");
//				System.out.println(m.group(0));
//				System.out.println(text);
//		String costItems = "item01|道具1";
//		String costName[] = costItems.split("|");
//		System.out.println(costName[0]);

//			String path = "pool1Record.txt";
//			InputStreamReader reader1;
//			String line = "";	
//			try {
//				reader1 = new InputStreamReader(
//						new FileInputStream(path));
//				BufferedReader br = new BufferedReader(reader1); 
//				int item1001 = 0;
//				int item1002 = 0;
//				int item1003 = 0;
//				int item2003 = 0;
//				int item2004 = 0;
//				int item2005 = 0;
//				while (line != null) {
//						line = br.readLine(); // 一次读入一行数据
//						if(line != null) {
//						if (line.contains("1001")) {
//							item1001 ++;
//						}else if(line.contains("1002")) {
//							item1002 ++;
//						}else if(line.contains("1003")) {
//							item1003 ++;
//						}else if(line.contains("2003")) {
//							item2003 ++;
//						}else if(line.contains("2004")) {
//							item2004 ++;
//						}else if(line.contains("2005")) {
//							item2005 ++;
//						}
//						}
//				}
//				br.close();
//				double itemSum = item1001 + item1002 + item1003 + item2003 + item2004 + item2005;
//				System.out.println(item1001/itemSum);
//				System.out.println(item1002/itemSum);
//				System.out.println(item1003/itemSum);
//				System.out.println(item2003/itemSum);
//				System.out.println(item2004/itemSum);
//				System.out.println(item2005/itemSum);
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//
//			}			
//		String str = UUID.randomUUID().toString().replace("-", "").toLowerCase();
//		System.out.println(str);
//		Map<String,Object> map = new HashMap<String,Object>();
//		JSONObject jo = new JSONObject();
//		jo.put("111","111");
//		jo.put("222","222");
//		jo.put("333","333");
//		for (Map.Entry<String,Object> m : jo.entrySet()) {
//			System.out.println(m.getKey());
//		}
//		System.out.println("--------------------");
//		for (Map.Entry<String,Object> m : jo.entrySet()) {
//			System.out.println(m.getKey());
//		}
//	}
		String str = "{\"0001\":{\"item1007\":\"22\",\"baodi\":\"{\\\"pool2\\\":\\\"0\\\",\\\"pool3\\\":\\\"16\\\",\\\"pool1\\\":\\\"79\\\"}\",\"item03\":\"199\",\"item2005\":\"89\",\"item02\":\"2\",\"item1002\":\"16\",\"item2004\":\"180\",\"item01\":\"96950\",\"pool1\":\"99\",\"item2003\":\"149\",\"item1003\":\"47\",\"item2002\":\"363\",\"item2001\":\"121\",\"item1001\":\"7\",\"userId\":\"1234567\"},\"0002\":{\"item1007\":\"22\",\"baodi\":\"{\\\"pool2\\\":\\\"0\\\",\\\"pool3\\\":\\\"16\\\",\\\"pool1\\\":\\\"79\\\"}\",\"item03\":\"199\",\"item2005\":\"89\",\"item02\":\"2\",\"item1002\":\"16\",\"item2004\":\"180\",\"item01\":\"96951\",\"pool1\":\"99\",\"item2003\":\"149\",\"item1003\":\"47\",\"item2002\":\"363\",\"item2001\":\"121\",\"item1001\":\"7\",\"userId\":\"1234567\"}}";
		JSONObject resJs = JSONObject.parseObject(str);
		JSONObject resVal = (JSONObject) resJs.get("0001");
		
		str = str.replace("[", "");
		str = str.replace("]", "");
		JSONObject jo = JSON.parseObject(str);
		resJs.put("0003", resVal);
		System.out.println(resJs.toJSONString());
		System.out.println(resVal.toJSONString());
	}
	
}
