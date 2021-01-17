/**
 *
 */
package org.opensrp.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.common.dto.AggregatedBiometricDTO;
import org.opensrp.common.dto.AggregatedReportDTO;
import org.opensrp.common.dto.COVID19ReportDTO;
import org.opensrp.common.dto.ChildNutritionReportDTO;
import org.opensrp.common.dto.ElcoReportDTO;
import org.opensrp.common.dto.ForumIndividualReportDTO;
import org.opensrp.common.dto.ForumReportDTO;
import org.opensrp.common.dto.HrReportDTO;
import org.opensrp.common.dto.IndividualBiometricReportDTO;
import org.opensrp.common.dto.PregnancyReportDTO;
import org.opensrp.common.dto.ReferralFollowupReportDTO;
import org.opensrp.common.dto.ReferralReportDTO;
import org.opensrp.common.dto.UserDTO;
import org.opensrp.common.service.impl.DatabaseServiceImpl;
import org.opensrp.common.util.DateUtil;
import org.opensrp.common.util.Roles;
import org.opensrp.common.util.SearchBuilder;
import org.opensrp.core.entity.Branch;
import org.opensrp.core.entity.Facility;
import org.opensrp.core.entity.Location;
import org.opensrp.core.entity.User;
import org.opensrp.core.service.BranchService;
import org.opensrp.core.service.FacilityService;
import org.opensrp.core.service.LocationService;
import org.opensrp.core.service.ReportService;
import org.opensrp.core.service.TargetService;
import org.opensrp.core.service.UserService;
import org.opensrp.web.nutrition.service.ChildGrowthService;
import org.opensrp.web.util.AuthenticationManagerUtil;
import org.opensrp.web.util.ModelConverter;
import org.opensrp.web.util.PaginationHelperUtil;
import org.opensrp.web.util.PaginationUtil;
import org.opensrp.web.util.SearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

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
	private ReportService reportService;
	
	@Autowired
	private PaginationUtil paginationUtil;
	
	@Autowired
	private BranchService branchService;
	
	@Autowired
	private LocationService locationService;
	
	@Value("#{opensrp['division.tag.id']}")
	private int divisionTagId;
	
	@Value("#{opensrp['divm.role.id']}")
	private String divMRoleId;
	
	@Autowired
	private TargetService targetService;
	
	@Value("#{opensrp['submenu.selected.color']}")
	private String submenuSelectedColor;
	
	@PostAuthorize("hasPermission(returnObject, 'CHILD_GROWTH_REPORT')")
	@RequestMapping(value = "/child-growth.html", method = RequestMethod.GET)
	public String childGrowthReport(HttpServletRequest request, HttpSession session, Model model, Locale locale) {
		model.addAttribute("locale", locale);
		searchUtil.setDivisionAttribute(session);
		searchBuilder.clear();
		List<Object[]> data = childGrowthServiceImpl.getChildFalteredData(searchBuilder);
		session.setAttribute("data", data);
		model.addAttribute("report", "block");
		return "/report/child-growth";
	}
	
	@RequestMapping(value = "/child-growth-ajax.html", method = RequestMethod.GET)
	public String childGrowthReportAjax(HttpServletRequest request, HttpSession session, Model model, Locale locale) {
		searchBuilder = paginationHelperUtil.setParams(request, session);
		List<Object[]> data = childGrowthServiceImpl.getChildFalteredData(searchBuilder);
		session.setAttribute("data", data);
		model.addAttribute("report", "block");
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
		model.addAttribute("report", "block");
		return "/report/sumamry";
	}
	
	@RequestMapping(value = "/summary-ajax.html", method = RequestMethod.GET)
	public String sumamryReportAjax(HttpServletRequest request, HttpSession session, Model model) {
		searchBuilder = paginationHelperUtil.setParams(request, session);
		List<Object[]> data = childGrowthServiceImpl.getSummaryData(searchBuilder);
		session.setAttribute("data", data);
		model.addAttribute("report", "block");
		return "/report/sumamry-ajax";
	}
	
	@PostAuthorize("hasPermission(returnObject, 'PERM_READ_AGGREGATED_REPORT')")
	@RequestMapping(value = "/householdDataReport.html", method = RequestMethod.GET)
	public String showFormWiseReport(HttpServletRequest request, HttpSession session, Model model, Locale locale,
	                                 @RequestParam("address_field") String address_value,
	                                 @RequestParam("searched_value") String searched_value,
	                                 @RequestParam("searched_value_id") Integer searchedValueId) throws ParseException {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		model.addAttribute("locale", locale);
		String branchId = request.getParameterMap().containsKey("branch") ? request.getParameter("branch") : "";
		System.err.println("branchId" + branchId);
		User user = userService.getLoggedInUser();
		if (AuthenticationManagerUtil.isAM()) {
			List<Object[]> branches = new ArrayList<>();
			for (Branch branch : user.getBranches()) {
				Object[] obj = new Object[10];
				obj[0] = branch.getId();
				obj[1] = branch.getName();
				branches.add(obj);
			}
		}
		session.setAttribute("branchList", branchService.getBranchByUser(user.getId()));
		for (Branch b : branchService.getBranchByUser(user.getId())) {
			System.out.println(" branch-name: " + b.getName());
		}
		List<Object[]> allSKs = new ArrayList<>();
		if (AuthenticationManagerUtil.isAM()) {
			List<Object[]> branches = new ArrayList<>();
			if (!branchId.isEmpty()) {
				Branch branch = branchService.findById(Integer.parseInt(branchId), "id", Branch.class);
				
				Object[] obj = new Object[10];
				obj[0] = branch.getId();
				obj[1] = branch.getName();
				branches.add(obj);
			} else {
				
				for (Branch branch : user.getBranches()) {
					Object[] obj = new Object[10];
					obj[0] = branch.getId();
					obj[1] = branch.getName();
					branches.add(obj);
				}
			}
			allSKs = databaseServiceImpl.getAllSks(branches);
		} else if (AuthenticationManagerUtil.isAdmin()) {
			allSKs = new ArrayList<Object[]>();
		}
		
		// List<Object[]> skLists = databaseServiceImpl.getAllSks();
		String startDate = formatter.format(DateUtil.getFirstDayOfMonth(new Date()));
		String endDate = formatter.format(new Date());
		
		String endDateValue = formatter.format(DateUtils.addDays(formatter.parse(endDate), 1));
		if (AuthenticationManagerUtil.isAM())
			address_value = "sk_id"; // for AM role
		List<Object[]> reports = databaseServiceImpl.getHouseHoldReports(startDate, endDateValue, address_value,
		    searched_value, allSKs, searchedValueId);
		session.setAttribute("formWiseAggregatedList", reports);
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("startDate", startDate);
		session.setAttribute("endDate", endDate);
		session.setAttribute("startDate", startDate);
		model.addAttribute("report", "block");
		return "report/householdDataReport";
	}
	
	@PostAuthorize("hasPermission(returnObject, 'PERM_READ_AGGREGATED_REPORT')")
	@RequestMapping(value = "/individual-mhv-works.html", method = RequestMethod.GET)
	public String getIndividualMHVData(Model model, HttpServletRequest request, HttpSession session,
	                                   @RequestParam("mhvUsername") String mhvUsername) {
		
		User user = userService.findByKey(mhvUsername, "username", User.class);
		Facility facility = facilityService.findById(Integer.valueOf(user.getChcp()), "id", Facility.class);
		request.setAttribute("user", user);
		request.setAttribute("facility", facility);
		List<Object[]> householdList = databaseServiceImpl.getHouseholdListByMHV(mhvUsername, session);
		session.setAttribute("householdList", householdList);
		model.addAttribute("report", "block");
		return "report/individual-mhv-works";
	}
	
	@PostAuthorize("hasPermission(returnObject, 'PERM_READ_AGGREGATED_REPORT')")
	@RequestMapping(value = "/household-member-list.html", method = RequestMethod.GET)
	public String getHouseholdMemberList(HttpServletRequest request, HttpSession session, Model model,
	                                     @RequestParam("householdBaseId") String householdBaseId,
	                                     @RequestParam("mhvId") String mhvId) {
		List<Object[]> householdMemberList = databaseServiceImpl.getMemberListByHousehold(householdBaseId, mhvId);
		session.setAttribute("memberList", householdMemberList);
		model.addAttribute("report", "block");
		return "report/household-member-list";
	}
	
	@RequestMapping(value = "/clientDataReport.html", method = RequestMethod.GET)
	public String getClientDataReportPage(HttpServletRequest request, HttpSession session, Model model, Locale locale) {
		model.addAttribute("formNameList", ModelConverter.mapLoad());
		List<Object[]> allSKs = new ArrayList<>();
		List<Object[]> branches = new ArrayList<>();
		User user = userService.getLoggedInUser();
		if (AuthenticationManagerUtil.isAM()) {
			for (Branch branch : user.getBranches()) {
				Object[] obj = new Object[10];
				obj[0] = branch.getId();
				obj[1] = branch.getName();
				branches.add(obj);
			}
			allSKs = databaseServiceImpl.getAllSks(branches);
		} else if (AuthenticationManagerUtil.isAdmin()) {
			allSKs = databaseServiceImpl.getAllSks(null);
		}
		
		session.setAttribute("skList", allSKs);
		session.setAttribute("branchList", new ArrayList<>(user.getBranches()));
		
		//        paginationUtil.createPagination(request, session, "viewJsonDataConversionOfClient", "ec_family");
		model.addAttribute("locale", locale);
		model.addAttribute("report", "block");
		model.addAttribute("selectrChildReportSubMenu", submenuSelectedColor);
		return "report/client-data-report";
	}
	
	@RequestMapping(value = "/aggregatedReport.html", method = RequestMethod.GET)
	public String getAggregatedReportPage(HttpSession session, Model model, Locale locale) {
		model.addAttribute("locale", locale);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(DateUtil.getPreviousDay(new Date()));
		String endDate = formatter.format(new Date());
		User user = AuthenticationManagerUtil.getLoggedInUser();
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("branchList", branchService.getBranchByUser(user.getId()));
		session.setAttribute("startDate", startDate);
		session.setAttribute("endDate", endDate);
		model.addAttribute("report", "block");
		
		model.addAttribute("selectrAggregateReportSubMenu", submenuSelectedColor);
		return "report/aggregated-report";
	}
	
	@RequestMapping(value = "/aggregated-report", method = RequestMethod.GET)
	public String generateAggregatedReport(Model model,
	                                       HttpServletRequest request,
	                                       HttpSession session,
	                                       @RequestParam(value = "address_field", required = false, defaultValue = "division") String addressValue,
	                                       @RequestParam(value = "searched_value", required = false) String searchedValue,
	                                       @RequestParam(value = "searched_value_id", required = false, defaultValue = "9265") Integer searchedValueId,
	                                       @RequestParam(value = "startDate", required = false) String startDate,
	                                       @RequestParam(value = "endDate", required = false) String endDate) {
		
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		List<AggregatedReportDTO> aggregatedReports = new ArrayList<>();
		String skIds = "";
		String locationValue = request.getParameter("locationValue");
		if (AuthenticationManagerUtil.isAM() && locationValue.equalsIgnoreCase("catchmentArea")) {
			String branchId = request.getParameter("branch");
			if (StringUtils.isBlank(branchId)) {
				String branches = branchService.commaSeparatedBranch(new ArrayList<>(loggedInUser.getBranches()));
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branches + "}'");
			} else {
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branchId + "}'");
			}
			aggregatedReports = reportService.getAggregatedReportBySK(startDate, endDate, skIds);
			session.setAttribute("isSKList", true);
		} else {
			Location parentLocation = locationService.findById(searchedValueId, "id", Location.class);
			String parentLocationTag = parentLocation.getLocationTag().getName().toLowerCase();
			String parentLocationName = parentLocation.getName().split(":")[0];
			System.out.println("");
			if (addressValue.equalsIgnoreCase("sk_id")) {
				skIds = userService.findSKByLocationSeparatedByComma(searchedValueId, Roles.SK.getId());
				aggregatedReports = reportService.getAggregatedReportBySK(startDate, endDate, skIds);
				session.setAttribute("isSKList", true);
			} else {
				aggregatedReports = reportService.getAggregatedReportByLocation(startDate, endDate, searchedValueId,
				    addressValue, parentLocationTag, parentLocationName);
				session.setAttribute("isSKList", false);
			}
		}
		session.setAttribute("aggregatedReports", aggregatedReports);
		model.addAttribute("report", "block");
		return "report/aggregated-report-table";
	}
	
	@RequestMapping(value = "/familyPlanningReport.html", method = RequestMethod.GET)
	public String getFamilyPlanningReportPage(HttpSession session, Model model, Locale locale) {
		model.addAttribute("locale", locale);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(DateUtil.getPreviousDay(new Date()));
		String endDate = formatter.format(new Date());
		User user = AuthenticationManagerUtil.getLoggedInUser();
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("branchList", new ArrayList<>(user.getBranches()));
		session.setAttribute("startDate", startDate);
		session.setAttribute("endDate", endDate);
		model.addAttribute("report", "block");
		model.addAttribute("selectrFamilyPlaningReportSubMenu", submenuSelectedColor);
		return "report/family-planning-report";
	}
	
	@RequestMapping(value = "/family-planning-report", method = RequestMethod.GET)
	public String generateFamilyPlanningReport(Model model,
	                                           HttpServletRequest request,
	                                           HttpSession session,
	                                           @RequestParam(value = "address_field", required = false, defaultValue = "division") String addressValue,
	                                           @RequestParam(value = "searched_value", required = false) String searchedValue,
	                                           @RequestParam(value = "searched_value_id", required = false, defaultValue = "9265") Integer searchedValueId,
	                                           @RequestParam(value = "startDate", required = false) String startDate,
	                                           @RequestParam(value = "endDate", required = false) String endDate) {
		
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		List<ElcoReportDTO> elcoReports = new ArrayList<>();
		String skIds = "";
		String locationValue = request.getParameter("locationValue");
		if (AuthenticationManagerUtil.isAM() && locationValue.equalsIgnoreCase("catchmentArea")) {
			String branchId = request.getParameter("branch");
			if (StringUtils.isBlank(branchId)) {
				String branches = branchService.commaSeparatedBranch(new ArrayList<>(loggedInUser.getBranches()));
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branches + "}'");
			} else {
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branchId + "}'");
			}
			elcoReports = reportService.getElcoReportBySK(startDate, endDate, skIds);
		} else {
			Location parentLocation = locationService.findById(searchedValueId, "id", Location.class);
			String parentLocationTag = parentLocation.getLocationTag().getName().toLowerCase();
			String parentLocationName = parentLocation.getName().split(":")[0];
			if (addressValue.equalsIgnoreCase("sk_id")) {
				skIds = userService.findSKByLocationSeparatedByComma(searchedValueId, Roles.SK.getId());
				elcoReports = reportService.getElcoReportBySK(startDate, endDate, skIds);
			} else {
				elcoReports = reportService.getElcoReportByLocation(startDate, endDate, searchedValueId, addressValue,
				    parentLocationTag, parentLocationName);
			}
		}
		session.setAttribute("elcoReports", elcoReports);
		model.addAttribute("report", "block");
		return "report/family-planning-report-table";
	}
	
	@RequestMapping(value = "/pregnancyReport.html", method = RequestMethod.GET)
	public String getPregnancyReportPage(HttpSession session, Model model, Locale locale) {
		model.addAttribute("locale", locale);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(DateUtil.getPreviousDay(new Date()));
		String endDate = formatter.format(new Date());
		User user = AuthenticationManagerUtil.getLoggedInUser();
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("branchList", new ArrayList<>(user.getBranches()));
		session.setAttribute("startDate", startDate);
		session.setAttribute("endDate", endDate);
		model.addAttribute("report", "block");
		model.addAttribute("selectrPregnancyReportSubMenu", submenuSelectedColor);
		return "report/pregnancy-report";
	}
	
	@RequestMapping(value = "/pregnancy-report", method = RequestMethod.GET)
	public String generatePregnancyReport(Model model,
	                                      HttpServletRequest request,
	                                      HttpSession session,
	                                      @RequestParam(value = "address_field", required = false, defaultValue = "division") String addressValue,
	                                      @RequestParam(value = "searched_value", required = false) String searchedValue,
	                                      @RequestParam(value = "searched_value_id", required = false, defaultValue = "9265") Integer searchedValueId,
	                                      @RequestParam(value = "startDate", required = false) String startDate,
	                                      @RequestParam(value = "endDate", required = false) String endDate) {
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		List<PregnancyReportDTO> pregnancyReports = new ArrayList<>();
		String skIds = "";
		String locationValue = request.getParameter("locationValue");
		if (AuthenticationManagerUtil.isAM() && locationValue.equalsIgnoreCase("catchmentArea")) {
			String branchId = request.getParameter("branch");
			if (StringUtils.isBlank(branchId)) {
				String branches = branchService.commaSeparatedBranch(new ArrayList<>(loggedInUser.getBranches()));
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branches + "}'");
			} else {
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branchId + "}'");
			}
			pregnancyReports = reportService.getPregnancyReportBySK(startDate, endDate, skIds);
		} else {
			Location parentLocation = locationService.findById(searchedValueId, "id", Location.class);
			String parentLocationTag = parentLocation.getLocationTag().getName().toLowerCase();
			String parentLocationName = parentLocation.getName().split(":")[0];
			if (addressValue.equalsIgnoreCase("sk_id")) {
				skIds = userService.findSKByLocationSeparatedByComma(searchedValueId, Roles.SK.getId());
				pregnancyReports = reportService.getPregnancyReportBySK(startDate, endDate, skIds);
			} else {
				pregnancyReports = reportService.getPregnancyReportByLocation(startDate, endDate, searchedValueId,
				    addressValue, parentLocationTag, parentLocationName);
			}
		}
		session.setAttribute("pregnancyReports", pregnancyReports);
		model.addAttribute("report", "block");
		return "report/pregnancy-report-table";
	}
	
	@RequestMapping(value = "/miscellaneousReport.html", method = RequestMethod.GET)
	public String getChildNutritionReportPage(HttpSession session, Model model, Locale locale) {
		model.addAttribute("locale", locale);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(DateUtil.getPreviousDay(new Date()));
		String endDate = formatter.format(new Date());
		User user = AuthenticationManagerUtil.getLoggedInUser();
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("branchList", new ArrayList<>(user.getBranches()));
		session.setAttribute("startDate", startDate);
		session.setAttribute("endDate", endDate);
		model.addAttribute("report", "block");
		model.addAttribute("selectrMiscReportSubMenu", submenuSelectedColor);
		return "report/child-nutrition-report";
	}
	
	@RequestMapping(value = "/child-nutrition-report", method = RequestMethod.GET)
	public String generateChildNutritionReport(Model model,
	                                           HttpServletRequest request,
	                                           HttpSession session,
	                                           @RequestParam(value = "address_field", required = false, defaultValue = "division") String addressValue,
	                                           @RequestParam(value = "searched_value", required = false) String searchedValue,
	                                           @RequestParam(value = "searched_value_id", required = false, defaultValue = "9265") Integer searchedValueId,
	                                           @RequestParam(value = "startDate", required = false) String startDate,
	                                           @RequestParam(value = "endDate", required = false) String endDate) {
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		List<ChildNutritionReportDTO> childNutritionReports = new ArrayList<>();
		String skIds = "";
		String locationValue = request.getParameter("locationValue");
		if (AuthenticationManagerUtil.isAM() && locationValue.equalsIgnoreCase("catchmentArea")) {
			String branchId = request.getParameter("branch");
			if (StringUtils.isBlank(branchId)) {
				String branches = branchService.commaSeparatedBranch(new ArrayList<>(loggedInUser.getBranches()));
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branches + "}'");
			} else {
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branchId + "}'");
			}
			childNutritionReports = reportService.getChildNutritionReportBySK(startDate, endDate, skIds);
		} else {
			Location parentLocation = locationService.findById(searchedValueId, "id", Location.class);
			String parentLocationTag = parentLocation.getLocationTag().getName().toLowerCase();
			String parentLocationName = parentLocation.getName().split(":")[0];
			if (addressValue.equalsIgnoreCase("sk_id")) {
				skIds = userService.findSKByLocationSeparatedByComma(searchedValueId, Roles.SK.getId());
				childNutritionReports = reportService.getChildNutritionReportBySK(startDate, endDate, skIds);
			} else {
				childNutritionReports = reportService.getChildNutritionReportByLocation(startDate, endDate, searchedValueId,
				    addressValue, parentLocationTag, parentLocationName);
			}
		}
		session.setAttribute("childNutritionReports", childNutritionReports);
		model.addAttribute("report", "block");
		return "report/child-nutrition-report-table";
	}
	
	@RequestMapping(value = "/covid-19.html", method = RequestMethod.GET)
	public String getCOVID19ReportPage(HttpServletRequest request, HttpSession session, Model model, Locale locale) {
		model.addAttribute("locale", locale);
		String branchId = request.getParameterMap().containsKey("branch") ? request.getParameter("branch") : "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(DateUtil.getPreviousDay(new Date()));
		String endDate = formatter.format(new Date());
		User user = AuthenticationManagerUtil.getLoggedInUser();
		List<Object[]> skList = new ArrayList<>();
		List<Branch> branchList = new ArrayList<>();
		if (AuthenticationManagerUtil.isAM()) {
			branchList = new ArrayList<>(user.getBranches());
			List<Object[]> branches = branchService.getBranchByUser(branchId, user);
			skList = databaseServiceImpl.getAllSks(branches);
		} else {
			branchList = branchService.findAll("Branch");
			skList = databaseServiceImpl.getAllSks(new ArrayList<Object[]>());
		}
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("branchList", branchList);
		session.setAttribute("skList", skList);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("report", "block");
		model.addAttribute("selectrCovid19portSubMenu", submenuSelectedColor);
		return "report/covid-19-report";
	}
	
	@RequestMapping(value = "/covid-19-report", method = RequestMethod.GET)
	public String generateCOVID19Report(Model model, HttpServletRequest request, HttpSession session,
	                                    @RequestParam(value = "startDate", required = false) String startDate,
	                                    @RequestParam(value = "endDate", required = false) String endDate) {
		String branchId = request.getParameterMap().containsKey("branch") ? request.getParameter("branch") : "";
		String skIds = request.getParameterMap().containsKey("sk") ? request.getParameter("sk") : "";
		
		List<COVID19ReportDTO> covid19Reports = new ArrayList<>();
		User user = AuthenticationManagerUtil.getLoggedInUser();
		if (!StringUtils.isBlank(skIds) && !skIds.equals("0")) {
			covid19Reports = reportService.getCOVID19ReportBySK(startDate, endDate, skIds, 0, 10);
		} else {
			if (StringUtils.isBlank(branchId) || branchId.equals("0")) {
				if (AuthenticationManagerUtil.isAdmin()) {
					covid19Reports = reportService.getCOVID19Report(startDate, endDate, 0, 10);
				} else {
					String branches = branchService.commaSeparatedBranch(new ArrayList<>(user.getBranches()));
					skIds = userService.findSKByBranchSeparatedByComma("'{" + branches + "}'");
					covid19Reports = reportService.getCOVID19ReportBySK(startDate, endDate, skIds, 0, 10);
				}
			} else {
				Branch branch = branchService.findById(Integer.valueOf(branchId), "id", Branch.class);
				String branches = branchService.commaSeparatedBranch(Arrays.asList(branch));
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branches + "}'");
				covid19Reports = reportService.getCOVID19ReportBySK(startDate, endDate, skIds, 0, 10);
			}
		}
		session.setAttribute("covid19Reports", covid19Reports);
		model.addAttribute("report", "block");
		return "report/covid-19-report-table";
	}
	
	@RequestMapping(value = "/clientDataReportTable", method = RequestMethod.GET)
	public String getClientDataReportTable(HttpServletRequest request,
	                                       HttpSession session,
	                                       Model model,
	                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer RESULT_SIZE)
	    throws ParseException {
		
		// Request Parameters
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		String startTime = request.getParameterMap().containsKey("startDate") ? request.getParameter("startDate")
		        : formatter.format(DateUtils.addMonths(new Date(), -3));
		
		String endTime = request.getParameterMap().containsKey("endDate") ? request.getParameter("endDate") : formatter
		        .format(new Date());
		//		endTime = formatter.format(DateUtils.addDays(formatter.parse(endTime), 1));
		
		String formName = request.getParameterMap().containsKey("formName") ? request.getParameter("formName") : "ec_family";
		
		String branchId = request.getParameterMap().containsKey("branch") ? request.getParameter("branch") : "";
		
		String sk = request.getParameterMap().containsKey("sk") ? request.getParameter("sk") : "";
		
		Integer pageNumber = Integer.parseInt(request.getParameter("pageNo"));
		List<Object[]> allSKs = new ArrayList<>();
		
		User user = userService.getLoggedInUser();
		if (AuthenticationManagerUtil.isAM()) {
			List<Object[]> branches = new ArrayList<>();
			if (!branchId.isEmpty() && !branchId.equals("0")) {
				Branch branch = branchService.findById(Integer.parseInt(branchId), "id", Branch.class);
				
				Object[] obj = new Object[10];
				obj[0] = branch.getId();
				obj[1] = branch.getName();
				branches.add(obj);
			} else {
				
				for (Branch branch : user.getBranches()) {
					Object[] obj = new Object[10];
					obj[0] = branch.getId();
					obj[1] = branch.getName();
					branches.add(obj);
				}
			}
			if (StringUtils.isBlank(sk)) {
				allSKs = databaseServiceImpl.getAllSks(branches);
				sk = userService.commaSeparatedSK(allSKs);
			}
		}
		
		List<Object[]> tempClientInfo = databaseServiceImpl.getClientInfoFilter(startTime, endTime,
		    formName.replaceAll("\\_", " "), sk, allSKs, pageNumber);
		List allClientInfo = ModelConverter.modelConverterForClientData(formName, tempClientInfo);
		
		System.out.println("SIZE:: " + tempClientInfo.size());
		
		Integer size = 0;
		if (pageNumber == 0) {
			size = databaseServiceImpl.getClientInfoFilterCount(startTime, endTime, formName.replaceAll("\\_", " "), sk,
			    allSKs);
			if ((size % RESULT_SIZE) == 0) {
				session.setAttribute("size", (size / RESULT_SIZE) - 1);
			} else {
				session.setAttribute("size", size / RESULT_SIZE);
			}
			session.setAttribute("recordSize", size);
		}
		
		new PaginationUtil().createPageList(session, pageNumber.toString());
		
		session.setAttribute("clientInfoList", allClientInfo);
		session.setAttribute("headerList", ModelConverter.headerListForClientData(formName));
		session.setAttribute("emptyFlag", 1);
		session.setAttribute("pageNumber", pageNumber);
		session.setAttribute("startTime", startTime);
		session.setAttribute("endTime", endTime);
		session.setAttribute("formName", formName);
		model.addAttribute("report", "block");
		return "report/client-data-report-table";
	}
	
	@RequestMapping(value = "/forum-report.html", method = RequestMethod.GET)
	public ModelAndView getForumReport(Model model, ModelAndView modelAndView, HttpSession session) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		modelAndView.setViewName("report/forum-report/report");
		User user = AuthenticationManagerUtil.getLoggedInUser();
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("branchList", new ArrayList<>(user.getBranches()));
		session.setAttribute("startDate", formatter.format(DateUtil.getPreviousDay(new Date())));
		session.setAttribute("endDate", formatter.format(new Date()));
		model.addAttribute("selectForumportSubMenu", submenuSelectedColor);
		model.addAttribute("report", "block");
		return modelAndView;
	}
	
	@RequestMapping(value = "/forum-report", method = RequestMethod.GET)
	public String getForumReportTable(Model model,
	                                  HttpSession session,
	                                  @RequestParam(value = "startDate", required = false) String startDate,
	                                  @RequestParam(value = "endDate", required = false) String endDate,
	                                  @RequestParam(value = "address_field", required = false, defaultValue = "division") String locationTag,
	                                  @RequestParam(value = "searched_value_id", required = false, defaultValue = "9265") Integer searchedValueId,
	                                  @RequestParam(value = "branch", required = false, defaultValue = "") String branchId,
	                                  @RequestParam(value = "locationValue", required = false, defaultValue = "") String locationValue,
	                                  @RequestParam(value = "designation", required = false, defaultValue = "") String designation,
	                                  @RequestParam(value = "forumType", required = false, defaultValue = "") String forumType) {
		
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		List<ForumReportDTO> forumReport = new ArrayList<>();
		List<ForumIndividualReportDTO> forumIndividualReport = new ArrayList<>();
		String skIds = "";
		
		if (AuthenticationManagerUtil.isAM() && locationValue.equalsIgnoreCase("catchmentArea")) {
			
			if (StringUtils.isBlank(branchId)) {
				String branches = branchService.commaSeparatedBranch(new ArrayList<>(loggedInUser.getBranches()));
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branches + "}'");
			} else {
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branchId + "}'");
			}
			
			if (StringUtils.isBlank(forumType))
				forumReport = reportService.getForumReportBySK(startDate, endDate, skIds, designation);
			else
				forumIndividualReport = reportService.getForumIndividualReportBySk(startDate, endDate, forumType, skIds,
				    designation);
			
		} else {
			Location parentLocation = locationService.findById(searchedValueId, "id", Location.class);
			String parentLocationTag = parentLocation.getLocationTag().getName().toLowerCase();
			String parentLocationName = parentLocation.getName().split(":")[0];
			
			if (locationTag.equalsIgnoreCase("sk_id")) {
				skIds = userService.findSKByLocationSeparatedByComma(searchedValueId, Roles.SK.getId());
				if (StringUtils.isBlank(forumType))
					forumReport = reportService.getForumReportBySK(startDate, endDate, skIds, designation);
				else
					forumIndividualReport = reportService.getForumIndividualReportBySk(startDate, endDate, forumType, skIds,
					    designation);
				
			} else {
				// '1991-01-01', '2021-12-01', 'division', 9266 , 'DHAKA' , 'district');
				if (StringUtils.isBlank(forumType)) {
					forumReport = reportService.getForumReportByLocation(startDate, endDate, parentLocationTag,
					    searchedValueId, parentLocationName, locationTag, designation);
				} else {
					forumIndividualReport = reportService.getForumIndividualReportByLocation(startDate, endDate,
					    parentLocationTag, searchedValueId, parentLocationName, locationTag, forumType, designation);
				}
			}
		}
		session.setAttribute("forumReport", forumReport);
		session.setAttribute("forumIndividualReport", forumIndividualReport);
		session.setAttribute("forumType", forumType);
		model.addAttribute("report", "block");
		return StringUtils.isBlank(forumType) ? "report/forum-report/report-table"
		        : "report/forum-report/individual-report-table";
	}
	
	@RequestMapping(value = "/aggregated-biometric-report.html", method = RequestMethod.GET)
	public String getAggregatedBiometricReport(Model model, HttpSession session) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(DateUtil.getPreviousDay(new Date()));
		String endDate = formatter.format(new Date());
		User user = AuthenticationManagerUtil.getLoggedInUser();
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("branchList", new ArrayList<>(user.getBranches()));
		session.setAttribute("startDate", startDate);
		session.setAttribute("endDate", endDate);
		model.addAttribute("report", "block");
		model.addAttribute("selectBiometricSubMenu", submenuSelectedColor);
		return "report/aggregated-biometric-report";
	}
	
	@RequestMapping(value = "/aggregated-biometric-table", method = RequestMethod.GET)
	public String getAggregatedBiometricTable(Model model,
	                                          HttpSession session,
	                                          @RequestParam(value = "startDate", required = false) String startDate,
	                                          @RequestParam(value = "endDate", required = false) String endDate,
	                                          @RequestParam(value = "address_field", required = false, defaultValue = "division") String locationTag,
	                                          @RequestParam(value = "searched_value_id", required = false, defaultValue = "9265") Integer searchedValueId,
	                                          @RequestParam(value = "branch", required = false, defaultValue = "") String branchId,
	                                          @RequestParam(value = "locationValue", required = false, defaultValue = "") String locationValue) {
		
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		List<AggregatedBiometricDTO> report;
		String skIds = "";
		
		if (AuthenticationManagerUtil.isAM() && locationValue.equalsIgnoreCase("catchmentArea")) {
			
			if (StringUtils.isBlank(branchId)) {
				String branches = branchService.commaSeparatedBranch(new ArrayList<>(loggedInUser.getBranches()));
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branches + "}'");
			} else {
				skIds = userService.findSKByBranchSeparatedByComma("'{" + branchId + "}'");
			}
			
			report = reportService.getAggregatedBiometricReportBySK(startDate, endDate, skIds);
			
		} else {
			Location parentLocation = locationService.findById(searchedValueId, "id", Location.class);
			String parentLocationTag = parentLocation.getLocationTag().getName().toLowerCase();
			String parentLocationName = parentLocation.getName().split(":")[0];
			
			if (locationTag.equalsIgnoreCase("sk_id")) {
				skIds = userService.findSKByLocationSeparatedByComma(searchedValueId, Roles.SK.getId());
				report = reportService.getAggregatedBiometricReportBySK(startDate, endDate, skIds);
			} else {
				// '1991-01-01', '2021-12-01', 'division', 9266 , 'DHAKA' , 'district');
				report = reportService.getAggregatedBiometricReport(startDate, endDate, parentLocationTag, searchedValueId,
				    parentLocationName, locationTag);
			}
		}
		
		session.setAttribute("aggregatedBiometricReport", report);
		model.addAttribute("report", "block");
		return "report/aggregated-biometric-table";
	}
	
	@RequestMapping(value = "/individual-biometric-report.html", method = RequestMethod.GET)
	public String getIndividualBiometricReport(Model model, HttpSession session) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(DateUtil.getPreviousDay(new Date()));
		String endDate = formatter.format(new Date());
		User user = AuthenticationManagerUtil.getLoggedInUser();
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("branchList", new ArrayList<>(user.getBranches()));
		session.setAttribute("startDate", startDate);
		session.setAttribute("endDate", endDate);
		model.addAttribute("report", "block");
		model.addAttribute("selectIndvidualBiometricSubMenu", submenuSelectedColor);
		return "report/individual-biometric-report";
	}
	
	@RequestMapping(value = "/individual-biometric-table", method = RequestMethod.GET)
	public String getIndividualBiometricTable(Model model,
	                                          HttpSession session,
	                                          @RequestParam(value = "address_field", required = false, defaultValue = "") String locationTag,
	                                          @RequestParam(value = "searched_value_id", required = false, defaultValue = "9265") Integer searchedValueId,
	                                          @RequestParam(value = "startDate", required = false) String startDate,
	                                          @RequestParam(value = "endDate", required = false) String endDate,
	                                          @RequestParam(value = "searched_value", required = false) String searchedValue,
	                                          @RequestParam(value = "branch", required = false, defaultValue = "-1") Integer branch,
	                                          @RequestParam(value = "serviceName", required = false, defaultValue = "") String serviceName,
	                                          @RequestParam(value = "locationValue", required = false, defaultValue = "catchmentArea") String locationValue) {
		
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		List<IndividualBiometricReportDTO> report;
		
		if (AuthenticationManagerUtil.isAM() && locationValue.equalsIgnoreCase("catchmentArea")) {
			String branchIds;
			branchIds = (branch == -1) ? branchService.commaSeparatedBranch(new ArrayList<>(loggedInUser.getBranches()))
			        : branch.toString();
			report = reportService.getIndividualBiometricReport(startDate, endDate, serviceName, "branch", "", branchIds);
		} else {
			
			Location parentLocation = locationService.findById(searchedValueId, "id", Location.class);
			String parentLocationTag = parentLocation.getLocationTag().getName().toLowerCase();
			
			String searchValue = searchedValue.equalsIgnoreCase("bangladesh") ? "" : searchedValue.split("=")[1].replace(
			    "'", "").trim();
			System.out.println("==========>>" + searchValue);
			report = reportService.getIndividualBiometricReport(startDate, endDate, serviceName, parentLocationTag,
			    searchValue, "");
		}
		
		session.setAttribute("individualBiometricReport", report);
		model.addAttribute("report", "block");
		return "report/individual-biometric-table";
	}
	
	@RequestMapping(value = "/pm-hr-report", method = RequestMethod.GET)
	public String hrReportForPm(Model model, Locale locale) {
		
		model.addAttribute("locale", locale);
		model.addAttribute("divisions", targetService.getLocationByTagId(divisionTagId));
		List<Branch> branches = branchService.findAll("Branch");
		model.addAttribute("divms", targetService.getUserByRoles(divMRoleId));
		model.addAttribute("branches", branches);
		model.addAttribute("report", "block");
		model.addAttribute("selectHRReportPMSubMenu", submenuSelectedColor);
		return "report/hr-report/by-dm-for-pm";
	}
	
	@RequestMapping(value = "/dm-table", method = RequestMethod.POST)
	public String hrReportDmTable(@RequestBody String dto, Model model, Locale locale) throws JSONException {
		
		JSONObject params = new JSONObject(dto);
		
		List<HrReportDTO> totalList = new ArrayList<>();
		
		totalList = targetService.getHRReportDMWise(params);
		
		model.addAttribute("reportDatas", totalList);
		model.addAttribute("jsonReportData", getHrReportAsJson(totalList).toString());
		model.addAttribute("report", "block");
		return "report/hr-report/dm-table.js";
	}
	
	@RequestMapping(value = "/dm-hr-report", method = RequestMethod.GET)
	public String hrReportForDm(Model model, Locale locale) {
		model.addAttribute("locale", locale);
		
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		String userIds = loggedInUser.getId() + "";
		model.addAttribute("userIds", userIds);
		List<UserDTO> users = targetService.getUserByUserIds(userIds, 32);
		model.addAttribute("users", users);
		model.addAttribute("report", "block");
		
		model.addAttribute("selectHRReportDMSubMenu", submenuSelectedColor);
		return "report/hr-report/by-am-for-dm";
	}
	
	@RequestMapping(value = "/am-table", method = RequestMethod.POST)
	public String hrReportAmTable(@RequestBody String dto, Model model, Locale locale) throws JSONException {
		
		JSONObject params = new JSONObject(dto);
		
		List<HrReportDTO> totalList = new ArrayList<>();
		
		totalList = targetService.getHRReportAMWise(params);
		
		model.addAttribute("reportDatas", totalList);
		model.addAttribute("jsonReportData", getHrReportAsJson(totalList).toString());
		return "report/hr-report/am-table";
	}
	
	@RequestMapping(value = "/am-hr-report", method = RequestMethod.GET)
	public String hrReportForAm(Model model, Locale locale) {
		model.addAttribute("locale", locale);
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		String userIds = loggedInUser.getId() + "";
		model.addAttribute("userIds", userIds);
		model.addAttribute("report", "block");
		model.addAttribute("selectHRReportAMSubMenu", submenuSelectedColor);
		return "report/hr-report/by-branch-for-am";
	}
	
	@RequestMapping(value = "/branch-table", method = RequestMethod.POST)
	public String hrReportBranchTable(@RequestBody String dto, Model model, Locale locale) throws JSONException {
		
		JSONObject params = new JSONObject(dto);
		
		List<HrReportDTO> totalList = new ArrayList<>();
		
		totalList = targetService.getHRReportBranchWise(params);
		
		model.addAttribute("reportDatas", totalList);
		model.addAttribute("jsonReportData", getHrReportAsJson(totalList).toString());
		
		return "report/hr-report/branch-table";
	}
	
	private JsonArray getHrReportAsJson(List<HrReportDTO> targetList) {
		
		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(targetList, new TypeToken<List<HrReportDTO>>() {}.getType());
		System.out.println(element.getAsJsonArray());
		return element.getAsJsonArray();
		
	}
	
	@RequestMapping(value = "/referral-report", method = RequestMethod.GET)
	public String referralReport(HttpSession session, Model model) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(DateUtil.getFirstDayOfMonth(new Date()));
		String endDate = formatter.format(new Date());
		User user = AuthenticationManagerUtil.getLoggedInUser();
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("branchList", new ArrayList<>(user.getBranches()));
		session.setAttribute("startDate", startDate);
		session.setAttribute("endDate", endDate);
		model.addAttribute("report", "block");
		model.addAttribute("selectReferralReportSubMenu", submenuSelectedColor);
		return "/report/referral";
	}
	
	@RequestMapping(value = "/referral-report-table", method = RequestMethod.GET)
	public String referralReportTable(HttpSession session,
	                                  @RequestParam(value = "startDate", required = false) String startDate,
	                                  @RequestParam(value = "endDate", required = false) String endDate,
	                                  @RequestParam(value = "address_field", required = false, defaultValue = "division") String locationTag,
	                                  @RequestParam(value = "searched_value_id", required = false, defaultValue = "9265") Integer searchedValueId,
	                                  @RequestParam(value = "branch", required = false, defaultValue = "") String branchId,
	                                  @RequestParam(value = "locationValue", required = false, defaultValue = "") String locationValue) {
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		List<ReferralReportDTO> report;
		
		Location parentLocation = locationService.findById(searchedValueId, "id", Location.class);
		String parentLocationTag = parentLocation.getLocationTag().getName().toLowerCase();
		String parentLocationName = parentLocation.getName().split(":")[0];
		
		report = targetService.getReferralReport(startDate, endDate, parentLocationTag.replaceAll(" ", "_"),
		    searchedValueId, parentLocationName, locationTag);
		
		session.setAttribute("referralReport", report);
		return "report/referral-table";
	}
	
	@RequestMapping(value = "/referral-followup-report", method = RequestMethod.GET)
	public String referralFollowupReport(Model model, HttpSession session) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(DateUtil.getFirstDayOfMonth(new Date()));
		String endDate = formatter.format(new Date());
		User user = AuthenticationManagerUtil.getLoggedInUser();
		searchUtil.setDivisionAttribute(session);
		session.setAttribute("branchList", new ArrayList<>(user.getBranches()));
		session.setAttribute("startDate", startDate);
		session.setAttribute("endDate", endDate);
		model.addAttribute("report", "block");
		model.addAttribute("selectReferralFollowUpSubMenu", submenuSelectedColor);
		return "/report/referral-followup-report";
	}
	
	@RequestMapping(value = "/referral-followup-report-table", method = RequestMethod.GET)
	public String referralFollowupReportTable(HttpSession session,
	                                          @RequestParam(value = "startDate", required = false) String startDate,
	                                          @RequestParam(value = "endDate", required = false) String endDate,
	                                          @RequestParam(value = "address_field", required = false, defaultValue = "division") String locationTag,
	                                          @RequestParam(value = "searched_value_id", required = false, defaultValue = "9265") Integer searchedValueId,
	                                          @RequestParam(value = "branch", required = false, defaultValue = "") String branchId,
	                                          @RequestParam(value = "referralReason", required = false, defaultValue = "all") String referralReason,
	                                          @RequestParam(value = "locationValue", required = false, defaultValue = "") String locationValue) {
		List<ReferralFollowupReportDTO> report;
		Gson gson = new Gson();
		
		Location parentLocation = locationService.findById(searchedValueId, "id", Location.class);
		String parentLocationTag = parentLocation.getLocationTag().getName().toLowerCase();
		String parentLocationName = parentLocation.getName().split(":")[0];
		
		report = targetService.getReferralFollowupReport(startDate, endDate, parentLocationTag.replaceAll(" ", "_"),
		    searchedValueId, parentLocationName, locationTag, referralReason);
		
		JsonElement element = gson.toJsonTree(report, new TypeToken<List<ReferralFollowupReportDTO>>() {}.getType());
		session.setAttribute("referralFollowupReport", report);
		session.setAttribute("jsonReportData", element.getAsJsonArray());
		return "report/referral-followup-table";
	}
	
}
