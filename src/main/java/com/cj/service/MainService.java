package com.cj.service;


import com.alibaba.fastjson.JSONObject;

public interface MainService {







	JSONObject allCardResult(int num, String req,String account);

	JSONObject getCard(String req,String account);

	String getCount();

	JSONObject getExchange();

	String  login_handle(Integer account, String pwd);

	JSONObject getPoolTimes(Integer account);

	JSONObject getPool(Integer account);

	JSONObject getPoolHis(String pool, String account);

	JSONObject exchangeHistory(String account);

	JSONObject exchangeItem(String info, String account);

	JSONObject getQueryItem(String account);




}
