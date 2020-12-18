package org.opensrp.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.opensrp.acl.permission.CustomPermissionEvaluator;
import org.opensrp.common.dto.UserDTO;
import org.opensrp.core.entity.Role;
import org.opensrp.core.entity.User;
import org.opensrp.core.service.TargetService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

public class AuthenticationManagerUtil {
	
	private static final Logger logger = Logger.getLogger(AuthenticationManagerUtil.class);
	
	public static boolean isPermitted(String permissionName) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomPermissionEvaluator customPermissionEvaluator = new CustomPermissionEvaluator();
		return customPermissionEvaluator.hasPermission(auth, "returnObject", permissionName);
	}
	
	// static method to return logged in user
	//april_17_2019
	public static User getLoggedInUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.err.println("auth:" + auth);
		User user = (User) auth.getPrincipal();
		
		logger.info("\nLogger-in User :" + user + "\n");
		return user;
	}
	
	// static method to return logged in user roles
	public static List<String> getLoggedInUserRoles() {
		User user = getLoggedInUser();
		List<String> roleList = new ArrayList<String>();
		Set<Role> roles = user.getRoles();
		for (Role role : roles) {
			logger.info("\nLogger-in User role :" + role.toString() + "\n");
			String roleName = role.getName();
			if (roleName != null && !roleName.isEmpty()) {
				roleList.add(roleName);
			}
		}
		logger.info("\nLogger-in User roleList :" + roleList.toString() + "\n");
		return roleList;
	}
	
	// static method to return if logged in user is Admin or not
	public static boolean isAdmin() {
		List<String> roleList = getLoggedInUserRoles();
		if (roleList.contains("Admin")) {
			logger.info("\nIsAdmin :" + "True\n");
			return true;
		}
		logger.info("\nIsAdmin :" + "False\n");
		return false;
	}
	
	// static method to return if logged in user is CHCP or not
	public static boolean isSK() {
		List<String> roleList = getLoggedInUserRoles();
		if (roleList.contains("SK")) {
			logger.info("\nIsSK :" + "True\n");
			return true;
		}
		logger.info("\nIsSK :" + "False\n");
		return false;
	}
	
	// static method to return if logged in user is CHCP or not
	public static boolean isAM() {
		List<String> roleList = getLoggedInUserRoles();
		if (roleList.contains("AM")) {
			logger.info("\nIsAM :" + "True\n");
			return true;
		}
		logger.info("\nIsAM :" + "False\n");
		return false;
	}
	
	public static boolean isDivM() {
		List<String> roleList = getLoggedInUserRoles();
		if (roleList.contains("DivM")) {
			logger.info("\nIs DivM :" + "True\n");
			return true;
		}
		logger.info("\nIsDivM :" + "False\n");
		return false;
	}
	
	public static void showRoleAndStatus() {
		logger.info("\nIsAdmin : " + isAdmin() + "\nIsCHCP : " + isAM() + "\nIsCHCP : " + isSK() + "\nIsCHCP : " + isSS()
		        + "\n");
	}
	
	public static boolean isSS() {
		List<String> roleList = getLoggedInUserRoles();
		if (roleList.contains("SS")) {
			logger.info("\nIsSS:" + "True\n");
			return true;
		}
		logger.info("\nIsSS :" + "False\n");
		return false;
	}
	
	public static Model setLoggedInUserRoleNameAtModel(Model model, TargetService targetService, String divMRoleId) {
		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		String roleName = "";
		for (Role role : loggedInUser.getRoles()) {
			roleName = role.getName();
			
		}
		
		if (roleName.equalsIgnoreCase("Admin")) {
			model.addAttribute("divms", targetService.getUserByRoles(divMRoleId));
			
		} else if (roleName.equalsIgnoreCase("DivM")) {
			List<UserDTO> users = targetService.getUserByUserIds(loggedInUser.getId() + "", 32);
			model.addAttribute("users", users);
		} else if (roleName.equalsIgnoreCase("AM")) {
			
		}
		
		model.addAttribute("roleName", roleName);
		return model;
	}
}
