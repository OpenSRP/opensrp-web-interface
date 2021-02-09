/**
 * 
 */
package org.opensrp.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.core.service.MigrationService;
import org.opensrp.core.service.TargetService;
import org.opensrp.web.util.AuthenticationManagerUtil;
import org.opensrp.web.util.BranchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author proshanto
 */
@Controller
@RequestMapping(value = "migration")
public class MigrationController {
	
	@Autowired
	private MigrationService migrationService;
	
	@Autowired
	private TargetService targetService;
	
	@Autowired
	public BranchUtil branchUtil;
	
	@Value("#{opensrp['divm.role.id']}")
	private String divMRoleId;
	
	@Value("#{opensrp['division.tag.id']}")
	private int divisionTagId;
	
	@Value("#{opensrp['submenu.selected.color']}")
	private String submenuSelectedColor;
	
	@RequestMapping(value = "/households-in.html", method = RequestMethod.GET)
	public String householdListIn(HttpServletRequest request, HttpSession session, Model model, Locale locale)
	    throws JSONException {
		model.addAttribute("locale", locale);
		model.addAttribute("branches", branchUtil.getBranches());
		model.addAttribute("isHousehold", true);
		model.addAttribute("hhType", "in");
		AuthenticationManagerUtil.setLoggedInUserRoleNameAtModel(model, targetService, divMRoleId);
		model.addAttribute("migration", "block");
		model.addAttribute("selectHHINSubMenu", submenuSelectedColor);
		return "migration/households-in";
	}
	
	@RequestMapping(value = "/households-out.html", method = RequestMethod.GET)
	public String householdListOut(HttpServletRequest request, HttpSession session, Model model, Locale locale)
	    throws JSONException {
		model.addAttribute("locale", locale);
		model.addAttribute("branches", branchUtil.getBranches());
		model.addAttribute("isHousehold", true);
		AuthenticationManagerUtil.setLoggedInUserRoleNameAtModel(model, targetService, divMRoleId);
		model.addAttribute("migration", "block");
		model.addAttribute("hhType", "out");
		model.addAttribute("selectHHOutSubMenu", submenuSelectedColor);
		return "migration/households-out";
	}
	
	@RequestMapping(value = "/members-in.html", method = RequestMethod.GET)
	public String memberListIn(HttpServletRequest request, HttpSession session, Model model, Locale locale)
	    throws JSONException {
		model.addAttribute("locale", locale);
		model.addAttribute("branches", branchUtil.getBranches());
		model.addAttribute("isHousehold", true);
		AuthenticationManagerUtil.setLoggedInUserRoleNameAtModel(model, targetService, divMRoleId);
		model.addAttribute("migration", "block");
		model.addAttribute("memberType", "in");
		
		model.addAttribute("selectMemberINSubMenu", submenuSelectedColor);
		return "migration/member-in";
	}
	
	@RequestMapping(value = "/members-out.html", method = RequestMethod.GET)
	public String memberListOut(HttpServletRequest request, HttpSession session, Model model, Locale locale)
	    throws JSONException {
		model.addAttribute("locale", locale);
		model.addAttribute("branches", branchUtil.getBranches());
		model.addAttribute("isHousehold", true);
		AuthenticationManagerUtil.setLoggedInUserRoleNameAtModel(model, targetService, divMRoleId);
		model.addAttribute("migration", "block");
		model.addAttribute("selectMemberOutSubMenu", submenuSelectedColor);
		model.addAttribute("memberType", "out");
		return "migration/member-out";
	}
	
	@RequestMapping(value = "/details-data/{id}", method = RequestMethod.GET)
	public String migrationDetails(HttpServletRequest request, @PathVariable("id") long id, HttpSession session,
	                               Model model, Locale locale, @RequestParam("migratedType") String migratedType)
	    throws JSONException, JsonProcessingException {
		model.addAttribute("locale", locale);
		
		JSONObject data = migrationService.getMigratedData(id);
		
		model.addAttribute("data", data);
		model.addAttribute("migratedType", migratedType);
		model.addAttribute("migration", "block");
		return "migration/details";
	}
	
	@RequestMapping(value = "/member-details-data/{id}", method = RequestMethod.GET)
	public String migratedMemberDetails(HttpServletRequest request, @PathVariable("id") long id, HttpSession session,
	                                    Model model, Locale locale, @RequestParam("migratedType") String migratedType)
	    throws JSONException, JsonProcessingException {
		model.addAttribute("locale", locale);
		
		JSONObject data = migrationService.getMigratedData(id);
		
		model.addAttribute("data", data);
		
		model.addAttribute("migratedType", migratedType);
		model.addAttribute("migration", "block");
		return "migration/member-details";
	}
}
