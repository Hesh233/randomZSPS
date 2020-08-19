package com.cj.service.impl;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.cj.service.MainService;
import com.cj.util.ServerException;

@Service("MainService")
public class MainServiceImpl implements MainService {
	@JSONField(jsonDirect=true)
	public boolean editFile(String string,String path) {
	   PrintStream stream=null;
	    try {
	        stream=new PrintStream(path);//写入的文件path
	        stream.print(string);//写入的字符串
	        stream.close();
	        return true;
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	public static void addFile(String conent, String file) {
		BufferedWriter out = null;
		try {
		out = new BufferedWriter(new OutputStreamWriter(
		new FileOutputStream(file, true)));
		out.write(conent+"\r\n");
		} catch (Exception e) {
		e.printStackTrace();
		} finally {
		try {
		out.close();
		} catch (IOException e) {
		e.printStackTrace();
		}
		}
	}
	//param2-不换行
	public String getText(String path) {
		InputStreamReader reader;
//		  InputStream stream = this.getClass().getResourceAsStream(path);
		String line = "";
		String lines = "";			
		try {
			reader = new InputStreamReader(
					new FileInputStream(path));
			BufferedReader br = new BufferedReader(reader); 
//			BufferedReader br = new BufferedReader(new InputStreamReader(stream,"UTF-8")) ;
			while (line != null) {
					line = br.readLine(); // 一次读入一行数据
					if(line!=null) {
						lines += line;
					}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}			
		return lines;
	} 
	public String getAndChangItemText(String path,String tex,String account) {
		JSONObject  itemJs = JSONObject.parseObject(getText("itemList.txt"));		
		InputStreamReader reader;
		String line = "";
		String lines = "";			
		try {
			reader = new InputStreamReader(
					new FileInputStream(path));
			BufferedReader br = new BufferedReader(reader); 
			while (line != null) {
					line = br.readLine(); // 一次读入一行数据
					if(line != null) {
					if (account.equals(line.split("==")[0])) {
						line = line.split("==")[1];
						if ( line.contains(";")) {
							String vals = line.split("--")[1];
							String val1 = itemJs.getString(vals.split(";")[0].split("\\|")[0]).split("\\|")[0];
							String val1Count = vals.split(";")[0].split("\\|")[1];
							String val2 = itemJs.getString(vals.split(";")[1].split("\\|")[0]).split("\\|")[0];
							String val2Count = vals.split(";")[1].split("\\|")[1];
							line = line.split("--")[0]+" "+ val1+"x"+val1Count+" "+val2+"x"+val2Count;
						}else {
							String vals = line.split("--")[1];
							String val = itemJs.getString(vals.split("\\|")[0]).split("\\|")[0];
							String valCount = vals.split("\\|")[1];
							line = line.split("--")[0] +" "+val+"x"+valCount;
						}
						lines += line+tex;
						}						
					}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}			
		return lines;
	} 	
	@Override
	public JSONObject allCardResult(int num,String pool,String account) {
		String costPath = "costItem.txt";
		String hasPath = "userItem.txt";
		String costItemStr = getText(costPath);
		JSONObject  costJs = JSONObject.parseObject(costItemStr);
		
		String costItems =  (String) costJs.get(pool);
		String costName = costItems.split("\\|")[1];
		String costItem = costItems.split("\\|")[0];		
		JSONObject jo = new JSONObject();		
		String userAccount = "";
		JSONObject  hasItemJs = new JSONObject();
		JSONObject resJs = JSON.parseObject(getText(hasPath));
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(getText(hasPath), new TypeReference<LinkedHashMap<String, Object>>() {});	
		for (Map.Entry<String,Object> map : jsonMap.entrySet()) {
			Integer intKey = Integer.parseInt(map.getKey());
			if(intKey.toString().equals(account)) {
				userAccount = map.getKey();
				hasItemJs = (JSONObject)map.getValue();
			}
		}
		String hasItemNum = hasItemJs.getString(costItem);
		Integer hasNum = Integer.parseInt(hasItemNum);
		if (num > hasNum) {
			jo.put("-1",costName+"数量不足");
			return jo;
		}else {
			int leftItemNum = hasNum-num;
			hasItemJs.put(costItem, Integer.toString(leftItemNum));
			resJs.put(userAccount,hasItemJs);
			//扣除对应道具
			editFile(resJs.toJSONString(),hasPath);
		}
		
 		for (int i=0;i<num;i++) {
 			JSONObject value = getCard(pool,account);
			jo.put(String.valueOf(i),value);
		}
		
		return jo;
	}

	@Override
	public JSONObject getCard(String poolName,String account) {
		//读取配置文件		
		//TODO登录账号判断（以后做）
		JSONObject  poolJsons = JSONObject.parseObject(getText("mapConfig.txt"));		
		JSONArray poolArray = (JSONArray)poolJsons.get(poolName);
		JSONObject poolJson = poolArray.getJSONObject(0);
		String timeSt = ((String) poolJson.get("time")).split("\\|")[0];
		String timeEd = ((String) poolJson.get("time")).split("\\|")[1];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		Date dateSt = null;
		Date dateEd = null;
		try {
			dateSt = sdf.parse(timeSt);
			dateEd = sdf.parse(timeEd);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//判断时间是否过期（极端条件，一般到时间了不予显示）
		Date now = new Date(); 
		//时间没过期
		JSONObject resJs = new JSONObject();
		String itemName = "";
		String itemImgPath = "";		
		if(now.before(dateEd) && now.after(dateSt)) {
			//TODO 正确执行
			//次数
			//isDuiHuan
			String resExchangeString = "";
			String userRecordPath = ""+poolName+"Record.txt";
			String userItemPath = "userItem.txt";
			String itemPath  = "itemList.txt";
			String userItemText = getText(userItemPath);
			String userAccount = "";
			String itemText = getText(itemPath);
			JSONObject hasJs = new JSONObject();
			resJs = JSONObject.parseObject(userItemText);
			LinkedHashMap<String, Object> jsonMap = JSON.parseObject(userItemText, new TypeReference<LinkedHashMap<String, Object>>() {});	
			for (Map.Entry<String,Object> map : jsonMap.entrySet()) {
				Integer intKey = Integer.parseInt(map.getKey());
				if(intKey.toString().equals(account)) {
					userAccount = map.getKey();
					
					hasJs = (JSONObject)map.getValue();
				}
			}
			JSONObject  itemJs = JSONObject.parseObject(itemText);
			if(poolJson.getString("isDuihuan").equals("True")) {
				//遍历兑换
				String item = "";
				int num = 0;
				int precent = 0;
				int allprecent = 0;
				Map<String,Integer> exchangeMap = new HashMap<String,Integer>();
				for (Entry<String, Object> entry : ((JSONObject) poolJson.get("duihuan")).entrySet()) {
					String its[] = ((String) entry.getKey()).split("\\|");
					item = its[0];
					num = Integer.parseInt(its[1]);
					precent = Integer.parseInt((String) entry.getValue());
					exchangeMap.put(item+"|"+num,precent);
					allprecent += precent;
		        }				
				resExchangeString = getResult(exchangeMap,allprecent);
			}

			if(poolJson.getString("isBaoDi").equals("True")){				
				int lastTime = Integer.parseInt(poolJson.getString("baodiTime"));
				//获取已抽次数
				String  hasTimes = hasJs.getString("baodi");
				JSONObject hasTimeJs = JSONObject.parseObject(hasTimes);
				Integer hasTime = Integer.parseInt(hasTimeJs.getString(poolName));
				if(hasTime + 1 >= lastTime) {
					//回归次数
					JSONObject resultJs = new JSONObject();
					hasTimeJs.put(poolName, 0);
					hasJs.put("baodi",hasTimeJs.toJSONString());
					resJs.put(userAccount,hasJs);
					editFile(resJs.toJSONString(),userItemPath);
					itemName = itemJs.getString(poolJson.getString("baodi").split("\\|")[0]).split("\\|")[0];
					itemImgPath = itemJs.getString(poolJson.getString("baodi").split("\\|")[0]).split("\\|")[1]; 
					resultJs.put("resItem",itemName+"|"+itemImgPath+"|"+poolJson.getString("baodi").split("\\|")[1]);
					return resultJs;
				}else {
//					userItemText.replaceAll(poolName+":\"(.*?)\"", Integer.toString(hasTime+1));
					hasTimeJs.put(poolName, Integer.toString(hasTime+1));
					hasJs.put("baodi", hasTimeJs.toString());					
				}				

			}
			String item = "";
			int num = 0;
			int precent = 0;
			int allprecent = 0;
			Map<String,Integer> rewardMap = new HashMap<String,Integer>();
			for (Map.Entry<String,Object> entry : ((JSONObject) poolJson.get("precent")).entrySet()) {
				String its[] = ((String) entry.getKey()).split("\\|");
				item = its[0];
				num = Integer.parseInt(its[1]);
				precent = Integer.parseInt((String) entry.getValue());
				rewardMap.put(item+"|"+num,precent);
				allprecent += precent;
	        }	
			String resRewardString = getResult(rewardMap,allprecent);		
			String addItem = resRewardString.split("\\|")[0];
			if (poolJson.getString("isBaoDi").equals("True")) {
				if (addItem.equals(poolJson.getString("baodi").split("\\|")[0])) {
					//保底回归次数
					String  hasTimes = hasJs.getString("baodi");
					JSONObject hasTimeJs = JSONObject.parseObject(hasTimes);		
					hasTimeJs.put(poolName, 0);
					hasJs.put("baodi",hasTimeJs.toJSONString());
					resJs.put(userAccount, hasJs);
					editFile(resJs.toString(),userItemPath);
				}
			}
			
			String hasCount = hasJs.getString(resRewardString.split("\\|")[0]);
			Integer addCount = 0;
			addCount =  Integer.parseInt(resRewardString.split("\\|")[1]);
			if (hasCount != null) {
				hasJs.put(addItem,String.valueOf(Integer.parseInt(hasCount)+addCount));
			}else {
				hasJs.put(addItem,String.valueOf(addCount));
			}		
			jsonMap.put(userAccount, hasJs.toJSONString());
			resJs.put(userAccount, hasJs);
			editFile(resJs.toJSONString(),"userItem.txt");
			Date dateNow = new Date();
			String dateNowStr = sdf.format(dateNow);
			JSONObject resultJs = new JSONObject();
			itemName = itemJs.getString(resRewardString.split("\\|")[0]).split("\\|")[0];
			itemImgPath = itemJs.getString(resRewardString.split("\\|")[0]).split("\\|")[1];
			resultJs.put("resItem",itemName+"|"+itemImgPath+"|"+resRewardString.split("\\|")[1]);
			
			if(resExchangeString != "") {
				addItem = resExchangeString.split("\\|")[0];
				hasCount = hasJs.getString(resExchangeString.split("\\|")[0]);				
				addCount =  Integer.parseInt(resExchangeString.split("\\|")[1]);
				if(hasCount != null) {					
					hasJs.put(addItem,String.valueOf(Integer.parseInt(hasCount)+addCount));
				}else {
					hasJs.put(addItem,String.valueOf(addCount));
				}				
				jsonMap.put(userAccount, hasJs.toJSONString());
				resJs.put(userAccount, hasJs);
				editFile(resJs.toJSONString(),"userItem.txt");
				addFile(account.toString() +"=="+ dateNowStr+"--"+resExchangeString+";"+resRewardString,userRecordPath);
				itemName = itemJs.getString(resExchangeString.split("\\|")[0]).split("\\|")[0];
				itemImgPath = itemJs.getString(resExchangeString.split("\\|")[0]).split("\\|")[1];
				resultJs.put("exchangItem",itemName+"||"+itemImgPath+"||"+resExchangeString.split("\\|")[1]);
				return resultJs;
			}else {
				
			addFile(account.toString() +"=="+ dateNowStr+"--"+resRewardString,userRecordPath);				
				return resultJs;
			}
		}else {
//			resJs.put("message","out of date");
			throw new ServerException("out of date");
//			return resJs;
		}
	}
	@Override
	public String getCount() {
		String hasPath = "userItem.txt";
		String costPath = "itemCost.txt";
		JSONObject  costItemJs = JSONObject.parseObject(getText(costPath));
		JSONObject  hasItemJs = JSONObject.parseObject(getText(hasPath));
		return hasItemJs.toJSONString()+costItemJs.toJSONString();		
	}
	public String getHasCount() {
		String hasPath = "userItem.txt";
		String costPath = "itemCost.txt";
		JSONObject  costItemJs = JSONObject.parseObject(getText(costPath));
		JSONObject  hasItemJs = JSONObject.parseObject(getText(hasPath));
		return hasItemJs.toJSONString()+costItemJs.toJSONString();		
	}
	@Override
	public JSONObject getPool(Integer account) {
		String poolPath = "mapConfig.txt";
		String itemListPath = "itemList.txt";
		JSONObject  itemJs = JSONObject.parseObject(getText(itemListPath));
		JSONObject mapRes = new JSONObject();	
		int index = 0;
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(getText(poolPath), new TypeReference<LinkedHashMap<String, Object>>() {});
		for (Map.Entry<String,Object> entry : jsonMap.entrySet()) {
			JSONObject poolRes = new JSONObject();
			JSONObject tempPool = ((JSONObject)((JSONArray) entry.getValue()).getJSONObject(0));
			String datStr[] = (tempPool).getString("time").split("\\|");
			String mes = (tempPool).getString("message");
			String costItem = (tempPool).getString("cost");
			String underTime = (tempPool).getString("baodiTime");
			String imagePath = (tempPool).getString("imagePath");
			String underItem = (tempPool).getString("baodi");
			String poolName = (tempPool).getString("poolName");
			JSONObject precent = (JSONObject) (tempPool).get("precent");
			LinkedHashMap<String, Object> preLinkMap = JSON.parseObject(precent.toJSONString(), new TypeReference<LinkedHashMap<String, Object>>() {});
			JSONObject precentRes = new JSONObject();
			double allPrecent = 0;
			double onePrecent = 0;
			for (Map.Entry<String,Object> preEntry : preLinkMap.entrySet()) {
				onePrecent = Double.parseDouble((String) preEntry.getValue());
				allPrecent += onePrecent;
			}
			int preIndex = 0;
			for (Map.Entry<String,Object> preEntry : preLinkMap.entrySet()) {
				String itemId = preEntry.getKey().split("\\|")[0];
				String itemName = itemJs.getString(itemId);
				onePrecent = Double.parseDouble((String) preEntry.getValue());
				double realPrecent = onePrecent/allPrecent;
				if (itemName == null) {
					throw new ServerException("没有找到"+itemId);
				}
				BigDecimal b = new BigDecimal(realPrecent*100);  
				realPrecent = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
				precentRes.put(String.valueOf(preIndex),itemName+"|"+String.valueOf(realPrecent)+"%");
				preIndex ++;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");	
			Date dateSt = null;
			Date dateEd = null;
			try {
				dateSt = sdf.parse(datStr[0]);
				dateEd = sdf.parse(datStr[1]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date now = new Date(); 
			//时间没过期,过期的不显示
			if(now.before(dateEd) && now.after(dateSt)) {
				if(underTime == null) {
					underTime = "-1";
				}else {
					poolRes.put("underItemName", itemJs.getString(underItem.split("\\|")[0]).split("\\|")[0]);
					poolRes.put("underItemImg", itemJs.getString(underItem.split("\\|")[0]).split("\\|")[1]);
				}
				String leftTime = "";
				try {
					leftTime = timeLeft(dateEd,now);
				} catch (ParseException e) {
					e.printStackTrace();
				}
//				lst.add(mes+"|"+(String) entry.getKey()+"|"+datStr[0]+";"+datStr[1]+"|"+imagePath+"|"+underTime+"|"+precentRes.toString());
				poolRes.put("leftTime", leftTime);	
				poolRes.put("mes", mes);				
				poolRes.put("dateSt", datStr[0]);
				poolRes.put("dateEd", datStr[1]);
				poolRes.put("imagePath", imagePath);
				poolRes.put("underTime", underTime);
				poolRes.put("precentRes", precentRes);
				poolRes.put("cost", itemJs.getString(costItem).split("\\|")[0]);
				poolRes.put("costImg", itemJs.getString(costItem).split("\\|")[1]);
				poolRes.put("costItemCount",costItem);
				poolRes.put("poolName",poolName);
				poolRes.put("pool",entry.getKey());
				mapRes.put(String.valueOf(index),poolRes);
				index ++;
			}			
		}
		String hasPath = "userItem.txt";
		JSONObject  hasItemJs = JSONObject.parseObject(getText(hasPath));
//		String itemName = "";
		String itemCountStr = "";
		JSONObject poorJs = new JSONObject();
		//TODO格式转成jsonobject
		for (Map.Entry<String,Object> map : mapRes.entrySet()) {
			poorJs = (JSONObject)map.getValue();
			JSONObject userJs = new JSONObject();
			userJs = hasItemJs.getJSONObject(account.toString());
			itemCountStr = userJs.getString(poorJs.getString("costItemCount"));
			poorJs.put("costItemCount",itemCountStr);
//			poorJs.put("itemName",itemName);
			mapRes.put(map.getKey(),poorJs);
		}
		return mapRes;
	}
	//TODO关联账号
	@Override
	public JSONObject getPoolTimes(Integer account) {
		String hasPath = "userItem.txt";
		JSONObject  hasItemJs = new JSONObject();
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(getText(hasPath), new TypeReference<LinkedHashMap<String, Object>>() {});	
		for (Map.Entry<String,Object> map : jsonMap.entrySet()) {
			if(Integer.parseInt(map.getKey()) == account) {
//				userAccount = map.getKey();
				hasItemJs = (JSONObject)map.getValue();
			}
		}
		JSONObject poolTimes = hasItemJs.getJSONObject("baodi");
		return poolTimes;
	}
	public String getResult(Map<String,Integer> map,int allprecent) {
		Random random =new Random();
		double precent = random.nextDouble()*1;
		//叠加
		double added = 0;
		double onePrecent = 0;
		for (Entry<String, Integer> one : map.entrySet()) {
			onePrecent = (double)one.getValue()/allprecent;
			if(precent < added + onePrecent) {
				return one.getKey();
			}else {
				added += onePrecent;
			}
		}
		return null;
	}
	@Override
	public JSONObject getPoolHis(String pool,String account) {
		String text = getAndChangItemText(""+pool+"Record.txt","</br>",account);
		JSONObject resJs = new JSONObject();
		
		resJs.put("0",text);
		return resJs;
		
	}
	public String timeLeft(Date t1,Date t2) throws ParseException {
		long l=t1.getTime()-t2.getTime();
		long day=l/(24*60*60*1000);
		long hour=(l/(60*60*1000)-day*24);
	//	long min=((l/(60*1000))-day*24*60-hour*60);
	//	long s=(l/1000-day*24*60*60-hour*60*60-min*60);
		return ""+day+"天"+hour+"小时";	
	//	return ""+day+"天"+hour+"小时"+min+"分"+s+"秒";	
	}
//TODO兑换拆分类
	@Override
	public JSONObject getExchange() {
		String exchangItemStr = getText("exchangeList.txt");
		String itemListStr = getText("itemList.txt");
		JSONObject  itemListJs = JSONObject.parseObject(itemListStr);
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(exchangItemStr, new TypeReference<LinkedHashMap<String, Object>>() {});		
		JSONObject resJs = new JSONObject();
		int index = 0;
		String exchangName = "";
		String costName = "";
		String exchangNumStr = "";
		String costNumStr = "";
		for (Map.Entry<String,Object> map : jsonMap.entrySet()) {
			exchangName = map.getKey();
			costName = ((String) map.getValue()).split("\\|")[0];
			exchangNumStr = ((String) map.getValue()).split("\\|")[2];
			costNumStr = ((String) map.getValue()).split("\\|")[1];
			resJs.put(String.valueOf(index),itemListJs.getString(exchangName)+"|"+exchangNumStr+";"+itemListJs.getString(costName)+"|"+costNumStr);
			index ++;
		}	
		return resJs;
	}
	
	
	@Override
	public JSONObject exchangeItem(String info,String account) {
		String itemNameStr = info.split("\\|")[0];
		Integer itemNum = Integer.parseInt(info.split("\\|")[1]);
		//重新加载到map写法不太好
//		Map itemMap = new HashMap();
		String exchangItemStr = getText("exchangeList.txt");
		String itemListStr = getText("itemList.txt");
//		String exchangeHistory = getText("exchangeRecord.txt");
		String hasPath = "userItem.txt";	
		JSONObject  hasItemJs = JSONObject.parseObject(getText(hasPath));
		JSONObject  exchangeItemJs = JSONObject.parseObject(exchangItemStr);
		JSONObject  itemListJs = JSONObject.parseObject(itemListStr);
		String itemStr = "";
		for (Map.Entry<String,Object> map : itemListJs.entrySet()) {
//			itemMap.put(map.getKey(),((String) map.getValue()).split("\\|")[0]);
			if(((String) map.getValue()).split("\\|")[0].equals(itemNameStr)) {
				itemStr = map.getKey();
				break;
			}
		}
		if(itemStr.equals("")) {
			throw new ServerException("没有该道具配置");
		}
		String needToCostItem = "";
		String exchangItem = "";
		Integer needTocostCount = 0;
		for(Map.Entry<String,Object> map : exchangeItemJs.entrySet()) {
			if (map.getKey().equals(itemStr)) {
				needToCostItem = ((String) map.getValue()).split("\\|")[0];
				needTocostCount = Integer.parseInt((((String) (map.getValue())).split("\\|")[1]));
//				exchangItemCount = Integer.parseInt((((String) (map.getValue())).split("\\|")[2]));
				exchangItem = ((String) map.getKey());
			};
		}
		JSONObject tempJs = new JSONObject();
		tempJs = (JSONObject) hasItemJs.get(account);
		Integer hasInt = tempJs.getInteger(needToCostItem);
		Integer needSum = itemNum * needTocostCount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		Date dateNow = new Date();
		String dateStr = sdf.format(dateNow);
		//TODO 兑换后数量还没有加上去
		if (hasInt > needSum) {
			tempJs.put(needToCostItem,String.valueOf(hasInt-needSum));
			hasItemJs.put(account,tempJs);
			Integer hasCount = ((JSONObject)hasItemJs.get(account)).getInteger(exchangItem);
			if(hasCount == null) {
				tempJs.put(exchangItem,String.valueOf(itemNum));
				hasItemJs.put(account,tempJs);
				addFile(dateStr+"--"+exchangItem+"|"+String.valueOf(itemNum),"exchangeRecord.txt");
			}else {
				tempJs.put(exchangItem,String.valueOf(itemNum+hasCount));
				hasItemJs.put(account,tempJs);
				addFile(account+"=="+dateStr+"--"+exchangItem+"|"+String.valueOf(itemNum),"exchangeRecord.txt");
			}
		}else {
//			resJs.put("0","道具数量不足");
//			return resJs;
			throw new ServerException("道具数量不足");
		}
		
		editFile(hasItemJs.toJSONString(),hasPath);
		//itemMap.get(itemStr);
//		resJs.put("0","Success");
		return null;
	}
	@Override
	public JSONObject exchangeHistory(String account) {
		String text = getAndChangItemText("exchangeRecord.txt","</br>",account);
		JSONObject resJs = new JSONObject();
		resJs.put("0",text);
		return resJs;
	}
	@Override	
	public String login_handle(Integer account, String pwd) {
		String accountText = getText("account.txt");
//		JSONObject  accountJs = JSONObject.parseObject(accountText);
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(accountText, new TypeReference<LinkedHashMap<String, Object>>() {});	
		String accountStr = "";
		String accountNameStr = "";
		String dataPwd = "";
		for (Map.Entry<String,Object> map : jsonMap.entrySet()) {
			accountStr = map.getKey();
			if (Integer.parseInt(accountStr) == account){
				JSONArray valJa = (JSONArray) map.getValue();
				JSONObject valJs = valJa.getJSONObject(0);
				accountNameStr = (String) valJs.get("accountName");
				dataPwd = (String) valJs.get("pwd");
				if (dataPwd.equals(pwd)) {
					return accountNameStr;
				}else {
					//密码不正确
					return null;
				}
			}			
		}
		//账号不存在
		return null;
	}
	@Override
	public JSONObject getQueryItem(String account) {
		String itemPath = "itemList.txt";
		String hasPath = "userItem.txt";		
//		String userAccount = "";
		JSONObject  hasItemJs = new JSONObject();
//		JSONObject resJs = JSON.parseObject(getText(hasPath));
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(getText(hasPath), new TypeReference<LinkedHashMap<String, Object>>() {});	
		for (Map.Entry<String,Object> map : jsonMap.entrySet()) {
			Integer intKey = Integer.parseInt(map.getKey());
			if(intKey.toString().equals(account)) {
//				userAccount = map.getKey();
				hasItemJs = (JSONObject)map.getValue();
			}
		}
		LinkedHashMap<String, Object> hasMap = JSON.parseObject(hasItemJs.toJSONString(), new TypeReference<LinkedHashMap<String, Object>>() {});
		LinkedHashMap<String, Object> itemMap = JSON.parseObject(getText(itemPath), new TypeReference<LinkedHashMap<String, Object>>() {});
		JSONObject resJs = new JSONObject();
		JSONObject resultJs = new JSONObject();
		for (Map.Entry<String,Object> oneHasMap : hasMap.entrySet()) {
			for (Map.Entry<String,Object> oneItemMap : itemMap.entrySet()) {
				if(oneItemMap.getKey() == oneHasMap.getKey()) {
					resJs.put(((String) oneItemMap.getValue()).split("\\|")[0], oneHasMap.getValue());
				}
			}
		}
		resultJs.put("0",resJs);
		return resultJs;
	}
	
}
