/**
 * 
 */
package org.opensrp.web.controller;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.opensrp.common.dto.ReportDTO;
import org.opensrp.common.service.impl.DatabaseServiceImpl;
import org.opensrp.common.util.DateUtil;
import org.opensrp.common.util.SearchBuilder;
import org.opensrp.core.entity.Branch;
import org.opensrp.core.entity.Facility;
import org.opensrp.core.entity.User;
import org.opensrp.core.service.BranchService;
import org.opensrp.core.service.FacilityService;
import org.opensrp.core.service.UserService;
import org.opensrp.web.nutrition.service.ChildGrowthService;
import org.opensrp.web.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author proshanto
 */
@Controller
@RequestMapping(value = "report")
public class ReportController {

	@Autowired
	private PaginationHelperUtil paginationHelperUtil;

	@Autowired
	private UserService userService;

	@Autowired
	private ChildGrowthService childGrowthServiceImpl;

	@Autowired
	private SearchBuilder searchBuilder;

	@Autowired
	private SearchUtil searchUtil;

	@Autowired
	private DatabaseServiceImpl databaseServiceImpl;

	@Autowired
	private FacilityService facilityService;

    @Autowired
    private PaginationUtil paginationUtil;
    @Autowired
    private BranchService branchService;
	@PostAuthorize("hasPermission(returnObject, 'CHILD_GROWTH_REPORT')")
	@RequestMapping(value = "/child-growth.html", method = RequestMethod.GET)
	public String childGrowthReport(HttpServletRequest request, HttpSession session, Model model, Locale locale) {
		model.addAttribute("locale", locale);
		searchUtil.setDivisionAttribute(session);
		searchBuilder.clear();
		List<Object[]> data = childGrowthServiceImpl.getChildFalteredData(searchBuilder);
		session.setAttribute("data", data);

		return "/report/child-growth";
	}

	@RequestMapping(value = "/child-growth-ajax.html", method = RequestMethod.GET)
	public String childGrowthReportAjax(HttpServletRequest request, HttpSession session, Model model, Locale locale) {
		searchBuilder = paginationHelperUtil.setParams(request, session);
		List<Object[]> data = childGrowthServiceImpl.getChildFalteredData(searchBuilder);
		session.setAttribute("data", data);
		return "/report/child-growth-ajax";
	}

	@PostAuthorize("hasPermission(returnObject, 'CHILD_GROWTH_SUMMARY_REPORT')")
	@RequestMapping(value = "/summary.html", method = RequestMethod.GET)
	public String summaryReport(HttpServletRequest request, HttpSession session, Model model, Locale locale) {
		model.addAttribute("locale", locale);
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

	@PostAuthorize("hasPermission(returnObject, 'PERM_READ_AGGREGATED_REPORT')")
	@RequestMapping(value = "/householdDataReport.html", method = RequestMethod.GET)
	public String showFormWiseReport(HttpServletRequest request,
	                                 HttpSession session,
	                                 Model model,
	                                 Locale locale,
	                                 @RequestParam("address_field") String address_value,
	                                 @RequestParam("searched_value") String searched_value,
	                                 @RequestParam("searched_value_id") Integer searchedValueId) throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		model.addAttribute("locale", locale);
		String branchId = request.getParameterMap().containsKey("branch")?request.getParameter("branch") : "";
		System.err.println("branchId"+branchId);
		User user = userService.getLoggedInUser();
		if (AuthenticationManagerUtil.isAM()) {
			List<Object[]> branches = new ArrayList<>();
			for (Branch branch: user.getBranches()) {
				Object[] obj = new Object[10];
				obj[0] = branch.getId();
				obj[1] = branch.getName();
				branches.add(obj);
			}
		}
		session.setAttribute("branchList",new ArrayList<>(user.getBranches()));
		List<Object[]> allSKs = new ArrayList<>();
		if (AuthenticationManagerUtil.isAM()) {
			List<Object[]> branches = new ArrayList<>();
			if(!branchId.isEmpty() ){
				Branch branch = branchService.findById(Integer.parseInt(branchId), "id", Branch.class);
				
				Object[] obj = new Object[10];
				obj[0] = branch.getId();
				obj[1] = branch.getName();
				branches.add(obj);
			}else {
				
				for (Branch branch: user.getBranches()) {
					Object[] obj = new Object[10];
					obj[0] = branch.getId();
					obj[1] = branch.getName();
					branches.add(obj);
				}
			}
			allSKs = databaseServiceImpl.getAllSks(branches);
		} else if (AuthenticationManagerUtil.isAdmin()){
			allSKs =  new ArrayList<Object[]>();
		}
		
		// List<Object[]> skLists = databaseServiceImpl.getAllSks();
		String startDate = formatter.format(DateUtil.getFirstDayOfMonth(new Date()));
		String endDate = formatter.format(new Date());
		System.err.println("address_value:"+address_value);
		System.err.println("searched_value:"+searched_value);
		String endDateValue = formatter.format(DateUtils.addDays(formatter.parse(endDate), 1));
		List<Object[]> reports = databaseServiceImpl.getHouseHoldReports(startDate, endDateValue, address_value, searched_value,allSKs, searchedValueId);
		session.setAttribute("formWiseAggregatedList", reports);
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("startDate", startDate);
		session.setAttribute("endDate", endDate);
		session.setAttribute("startDate", startDate);
		return "report/householdDataReport";
	}

	@PostAuthorize("hasPermission(returnObject, 'PERM_READ_AGGREGATED_REPORT')")
	@RequestMapping(value = "/individual-mhv-works.html", method = RequestMethod.GET)
	public String getIndividualMHVData(HttpServletRequest request,
									   HttpSession session,
									   @RequestParam("mhvUsername") String mhvUsername) {

		User user = userService.findByKey(mhvUsername, "username", User.class);
		Facility facility = facilityService.findById(Integer.valueOf(user.getChcp()), "id", Facility.class);
		request.setAttribute("user", user);
		request.setAttribute("facility", facility);
		List<Object[]> householdList = databaseServiceImpl.getHouseholdListByMHV(mhvUsername, session);
		session.setAttribute("householdList", householdList);
		return "report/individual-mhv-works";
	}

	@PostAuthorize("hasPermission(returnObject, 'PERM_READ_AGGREGATED_REPORT')")
	@RequestMapping(value = "/household-member-list.html", method = RequestMethod.GET)
	public String getHouseholdMemberList(HttpServletRequest request,
										 HttpSession session,
										 Model model,
										 @RequestParam("householdBaseId") String householdBaseId,
										 @RequestParam("mhvId") String mhvId) {
		List<Object[]> householdMemberList = databaseServiceImpl.getMemberListByHousehold(householdBaseId, mhvId);
		session.setAttribute("memberList", householdMemberList);
		return "report/household-member-list";
	}

	@RequestMapping(value = "/clientDataReport.html", method = RequestMethod.GET)
	public String getClientDataReportPage(HttpServletRequest request,
										  HttpSession session,
										  Model model,
                                          Locale locale){
		model.addAttribute("formNameList", ModelConverter.mapLoad());
		List<Object[]> allSKs = new ArrayList<>();
		List<Object[]> branches = new ArrayList<>();
		User user = userService.getLoggedInUser();
		if (AuthenticationManagerUtil.isAM()) {
			for (Branch branch: user.getBranches()) {
				Object[] obj = new Object[10];
				obj[0] = branch.getId();
				obj[1] = branch.getName();
				branches.add(obj);
			}
			allSKs = databaseServiceImpl.getAllSks(branches);
		} else if (AuthenticationManagerUtil.isAdmin()){
			allSKs =  databaseServiceImpl.getAllSks(null);
		}

		session.setAttribute("skList",allSKs);
		session.setAttribute("branchList",new ArrayList<>(user.getBranches()));

//        paginationUtil.createPagination(request, session, "viewJsonDataConversionOfClient", "ec_family");
        model.addAttribute("locale", locale);

		return "report/client-data-report";
	}


	@RequestMapping(value = "/aggregated", method = RequestMethod.GET)
	public String generateAggregatedReportOnSearch(HttpServletRequest request,
	                                               HttpSession session,
	                                               @RequestParam(value = "address_field", required = false) String address_value,
	                                               @RequestParam(value = "searched_value", required = false) String searched_value,
	                                               @RequestParam(value = "searched_value_id", required = false) Integer searchedValueId,
	                                               @RequestParam(value = "startDate", required = false) String startDate,
	                                               @RequestParam(value = "endDate", required = false) String endDate)
			throws ParseException {

		System.out.println("HIGH CHECK: "+ searchedValueId);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String branchId = request.getParameterMap().containsKey("branch")?request.getParameter("branch") : "";
		String locationType = request.getParameterMap().containsKey("locationValue")?request.getParameter("locationValue") : "";
		User user = userService.getLoggedInUser();
		List<Object[]> allSKs = new ArrayList<>();
		if (AuthenticationManagerUtil.isAM()) {
			List<Object[]> branches = new ArrayList<>();
			if(!branchId.isEmpty() ){
				Branch branch = branchService.findById(Integer.parseInt(branchId), "id", Branch.class);
				Object[] obj = new Object[10];
				obj[0] = branch.getId();
				obj[1] = branch.getName();
				branches.add(obj);
			}else {
				for (Branch branch: user.getBranches()) {
					Object[] obj = new Object[10];
					obj[0] = branch.getId();
					obj[1] = branch.getName();
					branches.add(obj);
				}
			}
			allSKs = databaseServiceImpl.getAllSks(branches);
		} else if (AuthenticationManagerUtil.isAdmin()){
			allSKs = new ArrayList<Object[]>();
		}
		
		if (locationType.equalsIgnoreCase("catchmentArea")) {
			address_value = "sk_id";
			searched_value = "empty";
		}
		else{
			if (searched_value == null || searched_value.equals("")) searched_value = "empty";
			if (address_value == null || address_value.equals("")) address_value = "division";
		}
		
		System.err.println("searched_value:"+searched_value+" address_value:"+address_value);
		endDate = formatter.format(DateUtils.addDays(formatter.parse(endDate), 1));
		System.out.println("END DATE: "+ endDate);
		List<Object[]> aggregatedReport = databaseServiceImpl.getHouseHoldReports(startDate, endDate, address_value, searched_value, allSKs, searchedValueId);
		System.out.println("SIZE: "+aggregatedReport.size());
		session.setAttribute("aggregatedReport", aggregatedReport);
		return "/report/aggregated-report";
	}

    @RequestMapping(value = "/clientDataReportTable", method = RequestMethod.GET)
    public String getClientDataReportTable(HttpServletRequest request,
                                          HttpSession session,
                                          Model model,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer RESULT_SIZE)
		    throws ParseException {

	    // Request Parameters
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String  startTime =
                request.getParameterMap().containsKey("startDate")?
                request.getParameter("startDate"): formatter.format(DateUtils.addMonths(new Date(), -3));

        String endTime = request.getParameterMap().containsKey("endDate")?
                request.getParameter("endDate"):formatter.format(new Date());
        endTime = formatter.format(DateUtils.addDays(formatter.parse(endTime), 1));

        String formName = request.getParameterMap().containsKey("formName")?
                request.getParameter("formName"):"ec_family";

        String branchId = request.getParameterMap().containsKey("branch")?request.getParameter("branch") : "";

        String sk = request.getParameterMap().containsKey("sk")?request.getParameter("sk"):"";

        Integer pageNumber = Integer.parseInt(request.getParameter("pageNo"));
        List<Object[]> allSKs = new ArrayList<>();

		User user = userService.getLoggedInUser();
		if (AuthenticationManagerUtil.isAM()) {
			List<Object[]> branches = new ArrayList<>();
			if(!branchId.isEmpty() ){
				Branch branch = branchService.findById(Integer.parseInt(branchId), "id", Branch.class);
				
				Object[] obj = new Object[10];
				obj[0] = branch.getId();
				obj[1] = branch.getName();
				branches.add(obj);
			}else {
				
				for (Branch branch: user.getBranches()) {
					Object[] obj = new Object[10];
					obj[0] = branch.getId();
					obj[1] = branch.getName();
					branches.add(obj);
				}
			}
			allSKs = databaseServiceImpl.getAllSks(branches);
		} else if (AuthenticationManagerUtil.isAdmin()){
			allSKs = databaseServiceImpl.getAllSks(null);
		}

	    System.out.println("START TIME: "+ startTime);

	    List<Object[]> tempClientInfo = databaseServiceImpl.getClientInfoFilter(startTime, endTime, formName.replaceAll("\\_"," ") , sk, allSKs, pageNumber);
        List allClientInfo =  ModelConverter.modelConverterForClientData(formName,tempClientInfo);


        Integer size = 0;
        if (pageNumber == 0) {
        	size = databaseServiceImpl.getClientInfoFilterCount(startTime, endTime, formName.replaceAll("\\_"," ") , sk, allSKs);
			if ((size % RESULT_SIZE) == 0) {
				session.setAttribute("size", (size / RESULT_SIZE) - 1);
			} else {
				session.setAttribute("size", size / RESULT_SIZE);
			}
			session.setAttribute("recordSize", size);
		}

        new PaginationUtil().createPageList(session, pageNumber.toString());

        System.out.println("---> debug Size: "+ size);

        session.setAttribute("clientInfoList",allClientInfo);
        session.setAttribute("headerList", ModelConverter.headerListForClientData(formName));
        session.setAttribute("emptyFlag",1);
        session.setAttribute("pageNumber", pageNumber);
        session.setAttribute("startTime", startTime);
        session.setAttribute("endTime", endTime);
        session.setAttribute("formName", formName);

	    return "report/client-data-report-table";
    }
}
