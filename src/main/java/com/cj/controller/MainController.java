package com.cj.controller;


import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cj.service.*;
import com.cj.util.*;
import com.alibaba.fastjson.JSONObject;
import com.cj.util.JsonResult;
@Controller
@Scope(value = "prototype")
@RequestMapping("/card")
public class MainController{
	@Autowired
	private MainService mainService;
	@ResponseBody
	@PostMapping("/allCardResult")
	public JsonResult getCard(HttpSession session, Model model,@RequestParam("datas")String datas) {
		Integer accountInt = (Integer) session.getAttribute("account");
		if (accountInt == null) {
			return new JsonResult(ResultCode.NEED_LOGIN, "请登录");
		}
		String account = accountInt.toString();
		String data[] = datas.replace("\"","").split("\\|");
		Integer num = Integer.parseInt(data[1]);
		String pool = data[0];
		JSONObject res = mainService.allCardResult(num,pool,account);
		if (res.toString().contains("-1")) {
			return new JsonResult(ResultCode.FAIL,"", res);
		}
		return new JsonResult(ResultCode.SUCCESS,"", res);
	}
//	@ResponseBody
	@RequestMapping("/CardPage")
	public String data_page(HttpSession session, Model model) {
		Integer account = (Integer)session.getAttribute("account");
		if (session.getAttribute("account") == null) {
			return "user/loginPage";
		}
		//拥有多少数量,物品与pool关系{pool|timest;timeed:Name|xx次，pool|timest;timeed：Name|xx次}
		JSONObject pool = mainService.getPool(account);
		JSONObject poolTimes = mainService.getPoolTimes(account);
		model.addAttribute("getPool",pool);
		model.addAttribute("poolTimes",poolTimes);
//		System.out.println(pool);
//		System.out.println(poolTimes);
		//TODO图片路径
		return "card/CardPage";
		}
	@ResponseBody
	@RequestMapping("/queryOwnItem")
	public JsonResult queryOwnItem(HttpSession session, Model model) {
		if (session.getAttribute("account") == null) {
			return new JsonResult(ResultCode.NEED_LOGIN, "请登录");
		}
		String account = session.getAttribute("account").toString();
		JSONObject resJs = mainService.getQueryItem(account);
		return new JsonResult(ResultCode.SUCCESS,"", resJs);
	}
	@ResponseBody
	@RequestMapping("/getExchange")
	public JsonResult getExchange(HttpSession session, Model model) {
		if (session.getAttribute("account") == null) {
			return new JsonResult(ResultCode.NEED_LOGIN, "请登录");
		}
		JSONObject res = mainService.getExchange();
		return new JsonResult(ResultCode.SUCCESS,"",res);
	}
	@ResponseBody
	@PostMapping("/poolResult")
	public JsonResult getPoolHis(HttpSession session, Model model,@RequestParam("datas")String datas) {
		if (session.getAttribute("account") == null) {
			return new JsonResult(ResultCode.NEED_LOGIN, "请登录");
		}
		String account = session.getAttribute("account").toString();
		String pool = datas.replace("\"", "");
		JSONObject res = mainService.getPoolHis(pool,account);

		return new JsonResult(ResultCode.SUCCESS,"", res);
	}
	@ResponseBody
	@PostMapping("/exchangeHistory")
	public JsonResult exchangeHistory(HttpSession session, Model model) {
		if (session.getAttribute("account") == null) {
			return new JsonResult(ResultCode.NEED_LOGIN, "请登录");
		}
		String account = session.getAttribute("account").toString();
		JSONObject res = mainService.exchangeHistory(account);
		return new JsonResult(ResultCode.SUCCESS,"", res);
	}
	@ResponseBody
	@PostMapping("/exchangeItem")
	public JsonResult exchangeItem(HttpSession session, Model model,@RequestParam("datas")String datas) {
		if (session.getAttribute("account") == null) {
			return new JsonResult(ResultCode.NEED_LOGIN, "请登录");
		}
		String account = session.getAttribute("account").toString();
		datas = datas.replace("\"", "");
		JSONObject res = mainService.exchangeItem(datas,account);
		return new JsonResult(ResultCode.SUCCESS,"", res);
	}
	@RequestMapping("/LoginPage")
	public String login_page(HttpSession session, Model model) {

		return "user/LoginPage";
		}
	@ResponseBody
	@PostMapping("/login_handle")
	public JsonResult login_handle(HttpSession session, Model model,@RequestBody Map<String, String> map) {
//		String data[] = datas.replace("\"","").split("\\|");
//		Integer account = Integer.parseInt(data[0]);
//		String pwd = data[1];
//		String account = datas.getString("userName");
		String pwd = "";
		Integer account = 0;
	    if(map.containsKey("userName")){
	         account = Integer.parseInt(map.get("userName"));
	    }
	    if(map.containsKey("password")){
	         pwd = map.get("password").toString();
	    }
//		Integer accountInt = Integer.parseInt(account);
//		String pwd = datas.getString("password");
		String accountName = mainService.login_handle(account,pwd);
		String res = "";
		if (accountName != null) {
			session.setAttribute("account",account);
			session.setAttribute("accountName",accountName);
			res = "登录成功";
		}else {
			res = "登录失败";
			return new JsonResult(ResultCode.FAIL,"", res);
		}
		return new JsonResult(ResultCode.SUCCESS,"", res);
	}
	@ResponseBody
	@PostMapping("/quitLogin")
	public JsonResult quitLogin(HttpSession session, Model model) {
		session.setAttribute("account",null);
		session.setAttribute("accountName",null);
		return new JsonResult(ResultCode.SUCCESS,"", "");
	}
    @ResponseBody
    @PostMapping("/underTime")
    public JsonResult checkUnderTime(HttpSession session, Model model,@RequestParam("datas")String pool) {
//        session.setAttribute("account",null);
        Integer accountInt = (Integer)session.getAttribute("account");
        if (accountInt == null) {
            return new JsonResult(ResultCode.NEED_LOGIN, "请登录");
        }
        JSONObject res = mainService.getUnderTime(accountInt.toString(),pool);
        return new JsonResult(ResultCode.SUCCESS,"", res);
    }

}
