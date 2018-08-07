/**
 * 
 */
package org.opensrp.web.controller;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.opensrp.common.util.SearchBuilder;
import org.opensrp.web.nutrition.service.impl.ChildGrowthServiceImpl;
import org.opensrp.web.util.PaginationHelperUtil;
import org.opensrp.web.util.SearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author proshanto
 */
@Controller
@RequestMapping(value = "report")
public class ReportController {
	
	@Autowired
	private PaginationHelperUtil paginationHelperUtil;
	
	@Autowired
	private ChildGrowthServiceImpl childGrowthServiceImpl;
	
	@Autowired
	private SearchBuilder searchBuilder;
	
	@Autowired
	private SearchUtil searchUtil;
	
	@RequestMapping(value = "/child-growth.html", method = RequestMethod.GET)
	public String childGrowthReport(HttpServletRequest request, HttpSession session, Model model) {
		searchUtil.setDivisionAttribute(session);
		searchBuilder.clear();
		List<Object[]> data = childGrowthServiceImpl.getChildFalteredData(searchBuilder);
		session.setAttribute("data", data);
		return "/report/child-growth";
	}
	
	@RequestMapping(value = "/child-growth-ajax.html", method = RequestMethod.GET)
	public String childGrowthReportAjax(HttpServletRequest request, HttpSession session, Model model) {
		searchBuilder = paginationHelperUtil.setParams(request, session);
		List<Object[]> data = childGrowthServiceImpl.getChildFalteredData(searchBuilder);
		session.setAttribute("data", data);
		return "/report/child-growth-ajax";
	}
	
	@RequestMapping(value = "/summary.html", method = RequestMethod.GET)
	public String summaryReport(HttpServletRequest request, HttpSession session, Model model) {
		searchUtil.setDivisionAttribute(session);
		searchBuilder.clear();
		List<Object[]> data = childGrowthServiceImpl.getSummaryData(searchBuilder);
		session.setAttribute("data", data);
		return "/report/sumamry";
	}
	
	@RequestMapping(value = "/summary-ajax.html", method = RequestMethod.GET)
	public String sumamryReportAjax(HttpServletRequest request, HttpSession session, Model model) {
		searchBuilder = paginationHelperUtil.setParams(request, session);
		List<Object[]> data = childGrowthServiceImpl.getSummaryData(searchBuilder);
		session.setAttribute("data", data);
		return "/report/sumamry-ajax";
	}
	
	@RequestMapping(value = "/analytics.html", method = RequestMethod.GET)
	public String analytics(HttpServletRequest request, HttpSession session, Model model) {
		return "/report/analytics";
	}
	
	@RequestMapping(value = "/analytics-ajax.html", method = RequestMethod.GET)
	public String analyticsAjax(HttpServletRequest request, HttpSession session, Model model) {
		searchBuilder.clear();
		List<Object[]> viewRefresh = childGrowthServiceImpl.refreshView(searchBuilder);
		int refreshCount = 0;
		Iterator obArrIterator = viewRefresh.iterator();
		if (obArrIterator.hasNext()) {
			refreshCount = (Integer) obArrIterator.next();
		}
		session.setAttribute("refreshCount", refreshCount);
		return "/report/analytics-ajax";
	}
}
