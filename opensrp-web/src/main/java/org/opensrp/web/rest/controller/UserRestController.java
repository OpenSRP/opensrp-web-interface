package org.opensrp.web.rest.controller;

import static org.springframework.http.HttpStatus.OK;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.common.dto.*;
import org.opensrp.core.dto.UserLocationDTO;
import org.opensrp.core.entity.*;
import org.opensrp.core.service.*;
import org.opensrp.core.service.mapper.FacilityWorkerMapper;
import org.opensrp.core.service.mapper.UserMapper;
import org.opensrp.web.util.AuthenticationManagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import javax.servlet.http.HttpSession;

import java.util.*;

@RequestMapping("rest/api/v1/user")
@RestController
public class UserRestController {
	
	@Autowired
	private UserService userServiceImpl;
	
	@Autowired
	private TeamMemberService teamMemberServiceImpl;
	
	@Autowired
	private TeamService teamService;

	@Autowired
	private FacilityService facilityService;
	
	@Autowired
	private RoleService roleService;

	@Autowired
	private FacilityWorkerTypeService facilityWorkerTypeService;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private EmailService emailService;

	@Autowired
	private FacilityWorkerMapper facilityWorkerMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UsersCatchmentAreaService usersCatchmentAreaService;

	private static final Logger logger = Logger.getLogger(UserRestController.class);
	private static final Integer SK_ROLE_ID = 28;
	private static final Integer SS_ROLE_ID = 29;

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResponseEntity<String> saveUser(@RequestBody UserDTO userDTO,
	                                       ModelMap model) throws Exception {
		String userNameUniqueError = "";
		try {
			User user = userMapper.map(userDTO);
			User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
			user.setCreator(loggedInUser);
			boolean isExists = userServiceImpl.isUserExist(user.getUsername());
			if (!isExists) {
				User createdUser = userServiceImpl.saveNew(user, false);
				//				String mailBody = "Dear " + user.getFullName()
				//						+ ",\n\nYour login credentials for HNPP are given below -\nusername : " + user.getUsername()
				//						+ "\npassword : " + userDTO.getPassword();
				//				if (numberOfUserSaved > 0) {
				//					logger.info("<><><><><> in user rest controller before sending mail to-" + user.getEmail());
				//					emailService.sendSimpleMessage(user.getEmail(), "Login credentials for CBHC", mailBody);
				//				}
				userNameUniqueError = String.valueOf(createdUser.getId());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new Gson().toJson(userNameUniqueError), OK);
	}

	@RequestMapping(value = "/change-password", method = RequestMethod.POST)
	public ResponseEntity<String> changeUserPassword(HttpSession session, @RequestBody ChangePasswordDTO dto, ModelMap model) {
		try {
			userServiceImpl.changePassword(session, dto);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new Gson().toJson(""), OK);
	}
	
	@RequestMapping(value = "/{id}/mhv", method = RequestMethod.POST)
	public ResponseEntity<String> saveUserMHV(@PathVariable("id") int facilityId, @RequestBody UserDTO userDTO,
	                                          ModelMap model) throws Exception {
		TeamMember teamMember = new TeamMember();
		String userNameUniqueError = "";
		Team team = new Team();
		Facility facility = new Facility();
		User user = new User();
		String firstName = "";
		String lastName = "";
		try {
			boolean isExists = userServiceImpl.isUserExist(userDTO.getUsername());
			Role role = roleService.findByKey("Provider", "name", Role.class);
			userDTO.setRoles("" + role.getId());
			facility = (Facility) facilityService.findById(facilityId, "id", Facility.class);
			Location location = locationService.findByKey(facility.getWard(), "name", Location.class);
			team = teamService.findByKey(location.getUuid(), "locationUuid", Team.class);
			
			if (!isExists) {
				user = userServiceImpl.convert(userDTO);
				user.setChcp(facility.getId() + "");
				firstName = user.getFirstName();
				lastName = user.getLastName();
//				int numberOfUserSaved = (int) userServiceImpl.save(user, false);
				String[] locations = userDTO.getLocationList().split(",");
				int[] locationList = new int[locations.length];
				for (int i = 0; i < locations.length; i++) {
					locationList[i] = Integer.parseInt(locations[i]);
				}
				user = userServiceImpl.findById(user.getId(), "id", User.class);
				teamMember = teamMemberServiceImpl.setCreatorLocationAndPersonAndTeamAttributeInLocation(teamMember,
				    user.getId(), team, locationList);
				teamMember.setIdentifier(userDTO.getIdetifier());
				teamMemberServiceImpl.save(teamMember);

				FacilityWorkerType facilityWorkerType = facilityWorkerTypeService.findByKey("MULTIPURPOSE HEALTH VOLUNTEER",
				    "name", FacilityWorkerType.class);

				FacilityWorker facilityWorker = facilityWorkerMapper.map(user, facility, facilityWorkerType);

				facilityWorkerTypeService.save(facilityWorker);

				String mailBody = "Dear " + user.getFullName()
				        + ",\n\nYour login credentials for CBHC are given below -\nusername : " + user.getUsername()
				        + "\npassword : " + userDTO.getPassword();

//				if (numberOfUserSaved > 0) {
//					logger.info("<><><><><> in user rest controller before sending mail to-" + user.getEmail());
//					emailService.sendSimpleMessage(user.getEmail(), "Login credentials for CBHC", mailBody);
//				}
				
			} else {
				userNameUniqueError = "User name already taken.";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			String errorMessage = "";
			if (user.getUuid() == null && user.getFirstName().equals("error")) {
				errorMessage = user.getLastName();
				userNameUniqueError = errorMessage;
				user.setFirstName(firstName);
				user.setLastName(lastName);
			} else {
				userNameUniqueError = "Some problem occurred please contact with Admin";
			}
		}
		return new ResponseEntity<>(new Gson().toJson(userNameUniqueError), OK);
	}
	
	@RequestMapping(value = "/password/edit", method = RequestMethod.POST)
	public ResponseEntity<String> editPassword(@RequestBody UserDTO userDTO) throws Exception {
		boolean isExists = userServiceImpl.isUserExist(userDTO.getUsername());
		String userNameUniqueError = "";
		User user = new User();
		if (!isExists) {
			user = userServiceImpl.convert(userDTO);
			userServiceImpl.save(user, false);
		} else {
			userNameUniqueError = "User name alreday taken.";
		}
		return new ResponseEntity<>(new Gson().toJson(userNameUniqueError), OK);
	}

	@RequestMapping(value = "/catchment-area/save", method = RequestMethod.POST)
	public ResponseEntity<String> saveUsersCatchmentArea(HttpSession session, @RequestBody UserLocationDTO userLocationDTO) throws Exception {
		String errorMessage = "";
		userServiceImpl.saveTeamMemberAndCatchmentAreas(session, userLocationDTO);
		List<Object[]> catchmentAreaTable = userServiceImpl.getCatchmentAreaTableForUser(userLocationDTO.getUserId());
		JSONObject response = new JSONObject();
		response.put("catchmentAreaTable", catchmentAreaTable);
		return new ResponseEntity<>(response.toString(), OK);
	}

	@RequestMapping(value = "/catchment-area/update", method = RequestMethod.POST)
	public ResponseEntity<String> updateUsersCatchmentArea(HttpSession session, @RequestBody UserLocationDTO userLocationDTO) throws Exception {
		String errorMessage = "";
		userServiceImpl.updateTeamMemberAndCatchmentAreas(session, userLocationDTO);
		List<Object[]> catchmentAreaTable = userServiceImpl.getCatchmentAreaTableForUser(userLocationDTO.getUserId());
		JSONObject response = new JSONObject();
		response.put("catchmentAreaTable", catchmentAreaTable);
		return new ResponseEntity<>(response.toString(), OK);
	}

	@RequestMapping(value = "/{id}/catchment-area", method = RequestMethod.GET)
	public ResponseEntity<String> catchmentArea(Model model, HttpSession session, @PathVariable("id") int id, Locale locale)
			throws JSONException {

		String role = "Admin";
		if (AuthenticationManagerUtil.isAM())
			role = "AM";

		String parentIndication = "#";
		String parentKey = "parent";
		List<UsersCatchmentArea> usersCatchmentAreas = usersCatchmentAreaService.findAllByForeignKey(id, "user_id",
				"UsersCatchmentArea");
		User user = userServiceImpl.findById(id, "id", User.class);
		List<Role> roles = new ArrayList<>(user.getRoles());

		TeamMember member = teamMemberServiceImpl.findByForeignKey(id, "person_id", "TeamMember");
		boolean isTeamMember = member != null ? true : false;

		Integer roleId = roles.get(0).getId();
		List<UserAssignedLocationDTO> userAssignedLocationDTOS = userServiceImpl.assignedLocationByRole(roleId);

		User loggedInUser = AuthenticationManagerUtil.getLoggedInUser();
		Integer userLocationId = 0;
		if (user.getParentUser() != null) {
			userLocationId = user.getParentUser().getId();
		}
		JSONArray locationTree = locationService.getLocationWithDisableFacility(session, parentIndication, parentKey,
				userAssignedLocationDTOS, id, role, userLocationId!=0?userLocationId:loggedInUser.getId(), roleId);

		List<Object[]> catchmentAreaTable = userServiceImpl.getCatchmentAreaTableForUser(id);

		JSONArray assignedLocations = new JSONArray();
		for (UserAssignedLocationDTO dto: userAssignedLocationDTOS) {
			JSONObject jOb = new JSONObject();
			jOb.put("userId", dto.getId());
			jOb.put("locationId", dto.getLocationId());
			assignedLocations.put(jOb);
		}

		JSONArray catchmentAreas = new JSONArray();
		if (usersCatchmentAreas != null)
		for (UsersCatchmentArea area: usersCatchmentAreas) {
			catchmentAreas.put(area.getLocationId());
		}

		JSONObject finalResponse = new JSONObject();
		finalResponse.put("locationTree", locationTree);
		finalResponse.put("assignedLocation", assignedLocations);
		finalResponse.put("catchmentAreas", catchmentAreas);
		finalResponse.put("isTeamMember", isTeamMember);
		finalResponse.put("catchmentAreaTable", catchmentAreaTable);

		return new ResponseEntity<>(finalResponse.toString(), OK);
	}

	@RequestMapping(value = "/ss-by-location", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public List<SSWithUCAIdDTO> getSSListByLocation(@RequestParam("locationId") Integer locationId) {
		List<SSWithUCAIdDTO> ssList = userServiceImpl.getSSListByLocation(locationId, SS_ROLE_ID);
		return ssList;
	}

	@RequestMapping(value = "/delete-sk-location", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteLocationFromSKAndRelatedSS(@RequestBody SKWithLocationDTO dto) {
		String responseMessage = "DELETED";
		try {
			Map<String, Object> mp = new HashMap<>();
			mp.put("locationId", dto.getSkLocationId());
			mp.put("userId", dto.getSkId());
			List<Integer> catchmentAreaIds = new ArrayList<>();
			if (dto.getSsWithUCAIdDTOList()!= null) {
				for (SSWithUCAIdDTO ssWithUCAIdDTO: dto.getSsWithUCAIdDTOList()) {
					catchmentAreaIds.add(ssWithUCAIdDTO.getUcaId());
				}
			}
			List<UsersCatchmentArea> usersCatchmentAreas = usersCatchmentAreaService.findAllByKeys(mp);
			for (UsersCatchmentArea area: usersCatchmentAreas) {
				catchmentAreaIds.add(area.getId());
			}

			int response = usersCatchmentAreaService.deleteCatchmentAreas(catchmentAreaIds);
			System.out.println("is deleted: "+response);
		} catch (Exception e) {
			e.printStackTrace();
			responseMessage = e.getMessage();
		}
		return new ResponseEntity<>(new Gson().toJson(responseMessage), OK);
	}

	@RequestMapping(value = "/delete-ss-location", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteLocationFromSKAndRelatedSS(@RequestBody SSWithLocationDTO dto) {
		String responseMessage = "DELETED";
		try {
			Map<String, Object> mp = new HashMap<>();
			mp.put("locationId", dto.getSsLocationId());
			mp.put("userId", dto.getSsId());
			List<Integer> catchmentAreaIds = new ArrayList<>();
			List<UsersCatchmentArea> usersCatchmentAreas = usersCatchmentAreaService.findAllByKeys(mp);
			for (UsersCatchmentArea area: usersCatchmentAreas) {
				catchmentAreaIds.add(area.getId());
			}

			int response = usersCatchmentAreaService.deleteCatchmentAreas(catchmentAreaIds);
			System.out.println("is deleted: "+response);
		} catch (Exception e) {
			e.printStackTrace();
			responseMessage = e.getMessage();
		}
		return new ResponseEntity<>(new Gson().toJson(responseMessage), OK);
	}

	@RequestMapping(value = "/update/ss-parent", method = RequestMethod.GET)
	public ResponseEntity<String> updateParentForSS(@RequestParam("ssId") Integer ssId, @RequestParam("parentUsername") String parentUsername)
			throws Exception {
		Integer response = userServiceImpl.updateParentForSS(ssId, parentUsername);
		return new ResponseEntity<>(response==1?"updated":"not updated", OK);
	}

	@RequestMapping(value = "/branch/sk")
	public ResponseEntity<String> getSKByBranch(@RequestParam("branchId") Integer branchId) {
		JSONObject res = new JSONObject();
		return new ResponseEntity<>(res.toString(), OK);
	}
	
	
	@RequestMapping(value = "/update-sk", method = RequestMethod.POST)
	public ResponseEntity<String> editSK(@RequestBody UserDTO userDTO,
	                                       ModelMap model) throws Exception {		
		
		String msg = "";
		try{						
			User account = userServiceImpl.findById(userDTO.getId(), "id", User.class);
			account.setFirstName(userDTO.getFirstName());
			account.setLastName(userDTO.getLastName());
			account.setMobile(userDTO.getMobile());
			account.setEmail(userDTO.getEmail());
			account.setEnableSimPrint(userDTO.getEnableSimPrint());
			account.setEnabled(userDTO.isStatus());
			account.setBranches(userServiceImpl.setBranch(userDTO.getBranches()));	
			userServiceImpl.update(account);
		}catch(Exception e){
			msg = "Some problem occurred please contact with Admin";
		}
		return new ResponseEntity<>(new Gson().toJson(msg), OK);
	}

	@RequestMapping(value = "/update-ss", method = RequestMethod.POST)
	public ResponseEntity<String> editSS(@RequestBody UserDTO userDTO,
	                                     ModelMap model) throws Exception {

		String msg = "";
		try{
			User account = userServiceImpl.findById(userDTO.getId(), "id", User.class);
			account.setFirstName(userDTO.getFirstName());
			account.setLastName(userDTO.getLastName());
			account.setMobile(userDTO.getMobile());
			userServiceImpl.update(account);
		} catch(Exception e) {
			msg = "Some problem occurred please contact with Admin";
		}
		return new ResponseEntity<>(new Gson().toJson(msg), OK);
	}
}
