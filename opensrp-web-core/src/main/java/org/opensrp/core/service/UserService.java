/**
 * @author proshanto
 * */

package org.opensrp.core.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.common.dto.UserDTO;
import org.opensrp.common.interfaces.DatabaseRepository;
import org.opensrp.core.dto.UserLocationDTO;
import org.opensrp.core.dto.WorkerIdDTO;
import org.opensrp.core.entity.*;
import org.opensrp.core.openmrs.service.OpenMRSServiceFactory;
import org.opensrp.core.openmrs.service.impl.OpenMRSUserAPIService;
import org.opensrp.core.service.mapper.UsersCatchmentAreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

@Service
public class UserService {

	private static final Logger logger = Logger.getLogger(UserService.class);

	@Autowired
	private RoleService roleService;

	@Autowired
	private TeamService teamService;

	@Autowired
	private TeamMemberService teamMemberServiceImpl;

	@Autowired
	private FacilityWorkerTypeService facilityWorkerTypeService;

	@Autowired
	private DatabaseRepository repository;

	@Autowired
	private OpenMRSServiceFactory openMRSServiceFactory;

	@Autowired
	private RoleService roleServiceImpl;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	private FacilityService facilityService;

	@Autowired
	private UsersCatchmentAreaService usersCatchmentAreaService;

	@Autowired
	private UsersCatchmentAreaMapper usersCatchmentAreaMapper;

	@Autowired
	private LocationService locationServiceImpl;

	@Transactional
	public <T> long save(T t, boolean isUpdate) throws Exception {
		User user = (User) t;
		long createdUser = 0;
		Set<Role> roles = user.getRoles();
		boolean isAdmin = roleServiceImpl.isOpenMRSRole(roles);
		JSONArray existingOpenMRSUser = new JSONArray();
		String query = "";
		String existingUserUUid = "";
		String existingUserPersonUUid = "";
		query = "v=full&username=" + user.getUsername();
		if (!isAdmin) {
			existingOpenMRSUser = openMRSServiceFactory.getOpenMRSConnector("user").getByQuery(query);
			if (existingOpenMRSUser.length() == 0) {
				logger.info(" \nUserBeforeSendingToOpenMRS : "+ user.toString() + "\n");
				user = (User) openMRSServiceFactory.getOpenMRSConnector("user").add(user);
				logger.info(" \nUserFromOpenMRS : "+ user.toString() + "\n");
				if (!user.getUuid().isEmpty()) {
					user.setPassword(passwordEncoder.encode(user.getPassword()));
					user.setProvider(true);
					createdUser = repository.save(user);
				} else {
					logger.error("No uuid found for user:" + user.getUsername());
				}
			} else {
				JSONObject userOb = new JSONObject();
				userOb = (JSONObject) existingOpenMRSUser.get(0);
				existingUserUUid = (String) userOb.get("uuid");
				JSONObject person = new JSONObject();
				person = (JSONObject) userOb.get("person");
				existingUserPersonUUid = (String) person.get("uuid");
				user.setProvider(true);
				user.setUuid(existingUserUUid);
				user.setPersonUUid(existingUserPersonUUid);
				openMRSServiceFactory.getOpenMRSConnector("user").update(user, existingUserUUid, userOb);
				//password is once encoded during save. No need to encode it again during update.
				//updatePassword() - is the dedicated function to reset password
				if (!isUpdate) {
					user.setPassword(passwordEncoder.encode(user.getPassword()));
				}
				createdUser = repository.save(user);
			}

		} else {
			user.setProvider(false);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			createdUser = repository.save(user);
		}

		return createdUser;
	}

	@Transactional
	public <T> int update(T t) throws Exception {
		User user = (User) t;
		boolean isProvider = roleServiceImpl.isOpenMRSRole(user.getRoles());
		if (isProvider) {
			user.setProvider(true);
			save(user, true);
		} else {
			user.setProvider(false);
		}
		return repository.update(user);
	}

	@Transactional
	public <T> boolean delete(T t) {
		return repository.delete(t);
	}

	@Transactional
	public <T> T findById(int id, String fieldName, Class<?> className) {
		return repository.findById(id, fieldName, className);

	}

	@Transactional
	public <T> T findByKey(String value, String fieldName, Class<?> className) {
		return repository.findByKey(value, fieldName, className);
	}

	@Transactional
	public <T> List<T> findAll(String tableClass) {
		return repository.findAll(tableClass);
	}

	@Transactional
	public <T> T findOneByKeys(Map<String, Object> fielaValues, Class<?> className) {
		return repository.findByKeys(fielaValues, className);
	}

	@Transactional
	public Set<Role> setRoles(String[] selectedRoles) {
		Set<Role> roles = new HashSet<Role>();
		if (selectedRoles != null) {
			for (String roleId : selectedRoles) {
				Role role = repository.findById(Integer.parseInt(roleId), "id", Role.class);
				roles.add(role);
			}
		}
		return roles;
	}

	@Transactional
	public Set<Branch> setBranches(String[] selectedBranches) {
		Set<Branch> branches = new HashSet<>();
		if (selectedBranches != null) {
			for (String branchId : selectedBranches) {
				Branch branch = repository.findById(Integer.parseInt(branchId), "id", Branch.class);
				branches.add(branch);
			}
		}
		return branches;
	}

	public boolean isPasswordMatched(User account) {
		return passwordEncoder.matches(account.getRetypePassword(), passwordEncoder.encode(account.getPassword()));
	}

	public boolean isUserExist(String userName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", userName);
		return repository.isExists(params, User.class);
	}

	public User convert(UserDTO userDTO) {
		User user = new User();
		String[] roles = userDTO.getRoles().split(",");
		user.setUsername(userDTO.getUsername());
		user.setEmail(userDTO.getEmail());
		user.setEnabled(true);
		user.setFirstName(userDTO.getFirstName());
		user.setGender("");
		user.setIdetifier(userDTO.getIdetifier());
		user.setLastName(userDTO.getLastName());
		user.setMobile(userDTO.getMobile());
		user.setPassword(userDTO.getPassword());
		user.setRoles(setRoles(roles));
		User parentUser = findById(userDTO.getParentUser(), "id", User.class);
		user.setParentUser(parentUser);

		return user;

	}

	// for setting user attributes from jsonObject -- April 10, 2019
	public User setUserInfoFromJSONObject(String username, JSONObject inputJSONObject,
										  String password, Facility facility) throws Exception {
		String facilityHeadDesignation = "Community Health Care Provider";
		logger.info("\nfacilityHeadDesignation : "+ facilityHeadDesignation + "\n");
		User user = null;
		user = new User();
		Role roleOfCHCP = roleService.findByKey("CHCP", "name", Role.class);
		logger.info("\n Role Of CHCP : "+ roleOfCHCP.toString() + "\n");
		String roleId = roleOfCHCP.getId()+"";
		//String[] roles = { "7" };
		String[] roles = { roleId };
		user.setUsername(username);
		if (username != null && !username.isEmpty()) {
			user.setEmail(username);
		}
		user.setEnabled(true);
		String facilityHeadIdentifier = inputJSONObject.getString("facility_head_provider_id");
		String facilityHeadName = inputJSONObject.getString("facility_head_provider_name");
		String[] nameArray = facilityHeadName.split("\\s+");
		String firstName = nameArray[0];
		String lastName = nameArray[nameArray.length - 1];
		if (firstName != null && !firstName.isEmpty()) {
			user.setFirstName(firstName);
		}
		if (lastName != null && !lastName.isEmpty()) {
			user.setLastName(lastName);
		}
		user.setGender("");
		user.setIdetifier("");
		String mobileNumber = inputJSONObject.getString("mobile1");
		if (mobileNumber != null && !mobileNumber.isEmpty()) {
			user.setMobile(mobileNumber);
		}else{
			user.setMobile("");
		}
		user.setPassword(password);
		user.setRoles(setRoles(roles));
		// User parentUser = findById(userDTO.getParentUser(), "id",User.class);
		// user.setParentUser("");

		// from user rest controller -- April 11, 2019
		user.setChcp(facility.getId() + "");
		logger.info(" \nUser : "+ user.toString() + "\n");
		int numberOfUserSaved = (int) save(user, false);
		logger.info("\nNumUSER: "+numberOfUserSaved +" \nUser : "+ user.toString() + "\n");

		// get facility by name from team table and then add it to team member
		Team team = new Team();
		TeamMember teamMember = new TeamMember();
		team = teamService.findByKey(facility.getName(), "name", Team.class);
		logger.info(" \nTeam : "+ team.toString() + "\n");

		int[] locations = new int[5];
		locations[0] = team.getLocation().getId();
		user = findById(user.getId(), "id", User.class);
		logger.info(" \nUser(find by id from DB) : "+ user.toString() + "\n");
		teamMember = teamMemberServiceImpl.setLocationAndPersonAndTeamAttributeInLocation(
				teamMember, user.getId(), team, locations);
		teamMember.setIdentifier(facilityHeadIdentifier);
		logger.info(" \nTeamMember : "+ teamMember.toString() + "\n");
		teamMemberServiceImpl.save(teamMember);

		FacilityWorker facilityWorker = new FacilityWorker();
		facilityWorker.setName(user.getFullName());
		facilityWorker.setIdentifier(user.getMobile());
		facilityWorker.setOrganization("Community Clinic");
		FacilityWorkerType facilityWorkerType = facilityWorkerTypeService
				.findByKey("CHCP", "name", FacilityWorkerType.class);
		facilityWorker.setFacility(facility);
		facilityWorker.setFacilityWorkerType(facilityWorkerType);
		logger.info(" \nFacilityWorkerType : "+ facilityWorkerType.toString() + "\n");
		facilityWorkerTypeService.save(facilityWorker);

		String mailBody = "Dear "
				+ user.getFullName()
				+ ",\n\nYour login credentials for CBHC are given below -\nusername : "
				+ user.getUsername() + "\npassword : " + password;
		if (numberOfUserSaved > 0) {
			logger.info("<><><><><> in user rest controller before sending mail to-"
					+ user.getEmail());
			emailService.sendSimpleMessage(user.getEmail(),
					"Login credentials for CBHC", mailBody);

		}
		return user;
	}

	// end: setting user attributes from jsonObject

	public int[] getSelectedRoles(User account) {
		int[] selectedRoles = new int[200];
		Set<Role> getRoles = account.getRoles();
		int i = 0;
		for (Role role : getRoles) {
			selectedRoles[i] = role.getId();
			i++;
		}
		return selectedRoles;
	}

	public User getLoggedInUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = repository.findByKey(auth.getName(), "username", User.class);
		return user;
	}

	@Transactional
	public <T> int updatePassword(T t) throws Exception {
		int updatedUser = 0;
		User user = (User) t;
		Set<Role> roles = user.getRoles();

		boolean isProvider = roleServiceImpl.isOpenMRSRole(roles);
		if (isProvider) {
			String uuid = openMRSServiceFactory.getOpenMRSConnector("user").update(user, user.getUuid(), null);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setProvider(true);
			updatedUser = repository.update(user);
		} else {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			updatedUser = repository.update(user);
			user.setProvider(false);
		}
		return updatedUser;
	}

	public Map<Integer, String> getUserListAsMap() {
		List<User> users = findAll("User");
		Map<Integer, String> usersMap = new HashMap<Integer, String>();
		for (User user : users) {
			usersMap.put(user.getId(), user.getUsername());

		}
		return usersMap;
	}

	@Transactional
	public List<User> findAllByKeysWithALlMatches(String name, boolean isProvider) {
		Map<String, String> fielaValues = new HashMap<String, String>();
		fielaValues.put("username", name);
		return repository.findAllByKeysWithALlMatches(isProvider, fielaValues, User.class);
	}

	@Transactional
	public Map<Integer, String> getProviderListAsMap() {
		Map<String, String> fielaValues = new HashMap<String, String>();
		boolean isProvider = true;
		List<User> users = repository.findAllByKeysWithALlMatches(isProvider, fielaValues, User.class);
		Map<Integer, String> usersMap = new HashMap<Integer, String>();
		if (users != null) {
			for (User user : users) {
				usersMap.put(user.getId(), user.getUsername());

			}
		}
		return usersMap;
	}

	/**
	 * <p>
	 * This method set roles attribute to session, all roles and selected roles.
	 * </p>
	 *
	 * @param roles list of selected roles.
	 * @param session is an argument to the HttpSession's session .
	 */
	@Transactional
	public void setRolesAttributes(int[] roles, HttpSession session) {
		//session.setAttribute("roles", repository.findAll("Role"));
		//fetch active roles to show on user edit view
		Map<String, Object> findCriteriaMap = new HashMap<String, Object>();
		findCriteriaMap.put("active", true);
		session.setAttribute("roles", repository.findAllByKeys(findCriteriaMap, Role.class));
		session.setAttribute("selectedRoles", roles);
	}

	public JSONArray getUserDataAsJson(String parentIndication, String parentKey) throws JSONException {
		JSONArray dataArray = new JSONArray();

		List<User> users = findAll("User");
		for (User user : users) {
			JSONObject dataObject = new JSONObject();
			dataObject.put("id", user.getId());
			User parentUser = user.getParentUser();
			if (parentUser != null) {
				dataObject.put(parentKey, parentUser.getId());
			} else {
				dataObject.put(parentKey, parentIndication);
			}
			dataObject.put("text", user.getFullName());
			dataArray.put(dataObject);
		}

		return dataArray;

	}

	public boolean deleteMHV(WorkerIdDTO workerIdDTO) throws JSONException {
		FacilityWorker facilityWorker = facilityWorkerTypeService.findById(workerIdDTO.getWorkerId(),"id",FacilityWorker.class);
		Facility facility = new Facility();
		if (facilityWorker != null)
			facilityService.findById(facilityWorker.getFacility().getId(), "id", Facility.class);
		User user = new User();
		if (facility != null)
			findByKey(String.valueOf(facility.getId()), "chcp", User.class);
		TeamMember teamMember = new TeamMember();
		if (user != null)
			teamMemberServiceImpl.findByKey(String.valueOf(user.getId()), "person_id", TeamMember.class);

		String teamMemberDeleteStatus = openMRSServiceFactory.getOpenMRSConnector("member").delete(teamMember.getUuid());

		if (user != null)
			openMRSServiceFactory.getOpenMRSConnector("user").delete(user.getUuid());

		return true;
	}

	@Transactional
	public String updateTeamMemberAndCatchmentAreas(UserLocationDTO userLocationDTO) throws Exception {
		int parentId = 0;
		String errorMessage = "";
		TeamMember teamMember = teamMemberServiceImpl.findByForeignKey(userLocationDTO.getUserId(), "person_id", "TeamMember");

		try {
			if (userLocationDTO.getLocations().length > 0) {
				int locationId = userLocationDTO.getLocations()[0];
				Location location = locationServiceImpl.findById(locationId, "id", Location.class);
				if (location != null) {
					parentId = location.getParentLocation().getId();

					Set<Location> locationSet = new HashSet<>();

					for (Location teamMemberLocation : teamMember.getLocations()) {
						if (teamMemberLocation.getParentLocation().getId() != parentId) {
							locationSet.add(teamMemberLocation);
						}
					}

					for (int newLocationId: userLocationDTO.getLocations()) {
						Location newLocation = locationServiceImpl.findById(newLocationId, "id", Location.class);
						locationSet.add(newLocation);
					}
					teamMember.setLocations(locationSet);

					List<UsersCatchmentArea> usersCatchmentAreas = usersCatchmentAreaService.findAllByParentAndUser(
							parentId,
							userLocationDTO.getUserId());

					for (UsersCatchmentArea usersCatchmentArea : usersCatchmentAreas) {
						usersCatchmentAreaService.delete(usersCatchmentArea);
					}
				}
			}
			teamMemberServiceImpl.save(teamMember);
			List<UsersCatchmentArea> usersCatchmentAreas = usersCatchmentAreaMapper.map(
					userLocationDTO.getLocations(),
					userLocationDTO.getUserId());
			usersCatchmentAreaService.saveAll(usersCatchmentAreas);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "something went wrong";
		}
		return errorMessage;
	}

	@Transactional
	public String saveTeamMemberAndCatchmentAreas(UserLocationDTO userLocationDTO) throws Exception {

		String teamName = "HNPP-BRAC";
		String errorMessage = "";
		Team team = teamService.findByKey(teamName, "name", Team.class);
		TeamMember teamMember = new TeamMember();
		try {
			teamMember = teamMemberServiceImpl.setLocationAndPersonAndTeamAttributeInLocation(
					teamMember,
					userLocationDTO.getUserId(),
					team,
					userLocationDTO.getLocations());
			teamMemberServiceImpl.save(teamMember);
			List<UsersCatchmentArea> usersCatchmentAreas = usersCatchmentAreaMapper.map(
					userLocationDTO.getLocations(),
					userLocationDTO.getUserId());
			usersCatchmentAreaService.saveAll(usersCatchmentAreas);
		} catch (Exception e) {
			errorMessage = "something went wrong";
			e.printStackTrace();
		}
		return errorMessage;
	}

	@Transactional
	public List<Object[]> getUsersCatchmentAreaTableAsJson(int userId) {
		return usersCatchmentAreaService.getUsersCatchmentAreaTableAsJson(userId);
	}
}
