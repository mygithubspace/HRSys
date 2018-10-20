package top.goingtop.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import top.goingtop.pojo.Check;
import top.goingtop.pojo.Employer;
import top.goingtop.service.CheckService;
import top.goingtop.service.EmployerService;
import top.goingtop.util.DateJsonValueProcessor;
import top.goingtop.util.PageBean;
import top.goingtop.util.ResponseUtil;
import top.goingtop.util.StringUtil;

@Controller
@RequestMapping("/check")
public class CheckController {

	@Resource
	private CheckService checkService;
	@Resource
	private EmployerService employerService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));   //true:允许输入空值，false:不能为空值
	}
	
	@RequestMapping("/list")
	public String list(@RequestParam(value="page",required=false)String page,@RequestParam(value="rows",required=false)String rows,Check check,HttpServletResponse response)throws Exception {
		PageBean pageBean=new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
		Map<String, Object> map=new HashMap<>();
		JsonConfig jsonConfig=new JsonConfig();
		jsonConfig.setExcludes(new String[]{"resources"});
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		JSONObject result=new JSONObject();
		String employerInfo = check.getEmployerId();
		if (employerInfo==null || employerInfo.equals("")) {
			map.put("employerId", StringUtil.formatLike(""));
		}else {
			Employer employer = employerService.findById(employerInfo);
			if (employer==null) {
				Employer emp = employerService.findIdByName(employerInfo);
				if (emp==null) {
					result.put("rows", 0);
					result.put("total", 0);
					ResponseUtil.write(response, result);
					return null;
				}
				map.put("employerId", StringUtil.formatLike(emp.getId()));
			}else {
				map.put("employerId", StringUtil.formatLike(employer.getId()));
			}
		}
		map.put("startSearch", check.getStartSearch());
		map.put("endSearch", check.getEndSearch());
		map.put("type", check.getType());
		map.put("start", pageBean.getStart());
		map.put("size", pageBean.getPageSize());
		List<Check> checkList = checkService.find(map);
		Long total = checkService.getTotal(map);
		
		JSONArray jsonArray = JSONArray.fromObject(checkList,jsonConfig);
		result.put("rows", jsonArray);
		result.put("total", total);
		ResponseUtil.write(response, result);
		return null;
	}
	@RequestMapping("/listByUserId")
	public String listByUserId(@RequestParam(value="page",required=false)String page,@RequestParam(value="rows",required=false)String rows,Check check,String userId,HttpServletResponse response)throws Exception {
		PageBean pageBean=new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
		Map<String, Object> map=new HashMap<>();
		JsonConfig jsonConfig=new JsonConfig();
		jsonConfig.setExcludes(new String[]{"resources"});
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		JSONObject result=new JSONObject();
		map.put("startSearch", check.getStartSearch());
		map.put("endSearch", check.getEndSearch());
		map.put("employerId", userId);
		map.put("type", check.getType());
		map.put("start", pageBean.getStart());
		map.put("size", pageBean.getPageSize());
		List<Check> checkList = checkService.find(map);
		Long total = checkService.getTotal(map);
		
		JSONArray jsonArray = JSONArray.fromObject(checkList,jsonConfig);
		result.put("rows", jsonArray);
		result.put("total", total);
		ResponseUtil.write(response, result);
		return null;
	}
	/**
	 * 添加或者修改用户
	 * @param check
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/save")
	public String save(Check check,HttpServletResponse response)throws Exception {
		int resultTotal=0;//操作记录数
		if(checkService.findById(check.getId())==null){
			resultTotal=checkService.add(check);
		}else{
			resultTotal=checkService.update(check);
		}
		JSONObject result=new JSONObject();
		if(resultTotal>0){
			result.put("success", true);
		}else{
			result.put("success", false);
		}
		ResponseUtil.write(response, result);
		return null;
	}
	/**
	 * 删除用户
	 * @param ids
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/delete")
	public String delete(@RequestParam(value="ids",required=false)String ids,HttpServletResponse response)throws Exception {
		JSONObject result=new JSONObject();
		String[] idsStr = ids.split(",");
		for(int i=0;i<idsStr.length;i++){
			checkService.delete(Integer.parseInt(idsStr[i]));
		}
		result.put("success", true);
		ResponseUtil.write(response, result);
		return null;
	}
}
