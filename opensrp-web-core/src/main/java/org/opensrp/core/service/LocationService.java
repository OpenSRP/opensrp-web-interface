/**
 * @author proshanto
 * */

package org.opensrp.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.common.dto.LocationTreeDTO;
import org.opensrp.common.interfaces.DatabaseRepository;
import org.opensrp.common.util.TreeNode;
import org.opensrp.core.entity.Location;
import org.opensrp.core.entity.LocationTag;
import org.opensrp.core.entity.User;
import org.opensrp.core.openmrs.service.OpenMRSServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class LocationService {
	
	private static final Logger logger = Logger.getLogger(LocationService.class);
	
	@Autowired
	private DatabaseRepository repository;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private OpenMRSServiceFactory openMRSServiceFactory;
	
	@Autowired
	private LocationTagService locationTagServiceImpl;
	
	public LocationService() {
		
	}
	
	@Transactional
	public List<Object[]> getLocationByTagId(int tagId) {
		String sqlQuery = "SELECT location.name,location.id from core.location " + " WHERE location_tag_id=:location_tag_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("location_tag_id", tagId);
		return repository.executeSelectQuery(sqlQuery, params);
	}
	
	@Transactional
	public List<Object[]> getChildData(int parentId) {
		String sqlQuery = "SELECT location.name,location.id from core.location where parent_location_id=:parentId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("parentId", parentId);
		return repository.executeSelectQuery(sqlQuery, params);
	}
	
	@Transactional
	public List<Object[]> executeSelectQuery(String sqlQuery, Map<String, Object> params) {
		return repository.executeSelectQuery(sqlQuery, params);
	}
	
	@Transactional
	public <T> long save(T t) throws Exception {
		
		Location location = (Location) t;
		
		location = (Location) openMRSServiceFactory.getOpenMRSConnector("location").add(location);
		long createdLocation = 0;
		if (!location.getUuid().isEmpty()) {
			createdLocation = repository.save(location);
		} else {
			logger.error("No uuid found for location:" + location.getName());
			// TODO
		}
		return createdLocation;
	}
	
	@Transactional
	public <T> int update(T t) throws JSONException {
		Location location = (Location) t;
		int updatedLocation = 0;
		String uuid = openMRSServiceFactory.getOpenMRSConnector("location").update(location, location.getUuid(), null);
		if (!uuid.isEmpty()) {
			location.setUuid(uuid);
			updatedLocation = repository.update(location);
		} else {
			logger.error("No uuid found for user:" + location.getName());
			// TODO
		}
		return updatedLocation;
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
	
	public Location setCreatorParentLocationTagAttributeInLocation(Location location, int parentLocationId, int tagId) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User creator = (User) repository.findByKey(auth.getName(), "username", User.class);
		Location parentLocation = (Location) repository.findById(parentLocationId, "id", Location.class);
		LocationTag locationTag = (LocationTag) repository.findById(tagId, "id", LocationTag.class);
		location.setCreator(creator);
		location.setParentLocation(parentLocation);
		location.setLocationTag(locationTag);
		
		return location;
	}

	@Transactional
	public <T> List<T> getVillageIdByProvider(int memberId, int childRoleId, int locationTagId) {
		return repository.getVillageIdByProvider(memberId, childRoleId, locationTagId);
	}
	
	public Map<Integer, String> getLocationTreeAsMap() {
		List<Location> locations = findAll("Location");
		Map<Integer, String> locationTreeAsMap = new HashMap<Integer, String>();
		if (locations != null) {
			for (Location location : locations) {
				locationTreeAsMap.put(location.getId(), location.getName());
				
			}
		}
		return locationTreeAsMap;
	}
	
	public boolean locationExistsForUpdate(Location location, boolean isOpenMRSCheck) throws JSONException {
		boolean exists = false;
		boolean isExistsInOpenMRS = false;
		JSONArray existinglocation = new JSONArray();
		String query = "";
		if (location != null) {
			exists = repository.entityExistsNotEualThisId(location.getId(), location.getName(), "name", Location.class);
			
			if (isOpenMRSCheck) {
				Location findLocation = findById(location.getId(), "id", Location.class);
				if (!findLocation.getName().equalsIgnoreCase(location.getName())) {
					query = "q=" + location.getName();
					existinglocation = openMRSServiceFactory.getOpenMRSConnector("location").getByQuery(query);
					
					if (existinglocation.length() != 0) {
						isExistsInOpenMRS = true;
					}
					if (!exists) { // if false then alter 
						exists = isExistsInOpenMRS;
					}
				}
				
			}
		}
		
		return exists;
	}
	
	public void setSessionAttribute(HttpSession session, Location location, String parentLocationName) {
		
		Map<Integer, String> parentLocationMap = getLocationTreeAsMap();
		Map<Integer, String> tags = locationTagServiceImpl.getLocationTagListAsMap();
		
		session.setAttribute("parentLocation", parentLocationMap);
		if (location.getParentLocation() != null) {
			session.setAttribute("selectedParentLocation", location.getParentLocation().getId());
		} else {
			session.setAttribute("selectedParentLocation", 0);
		}
		session.setAttribute("tags", tags);
		if (location.getLocationTag() != null) {
			session.setAttribute("selectedTtag", location.getLocationTag().getId());
		} else {
			session.setAttribute("selectedTtag", 0);
		}
		
		session.setAttribute("parentLocationName", parentLocationName);
		
	}
	
	public void setModelAttribute(ModelMap model, Location location) {
		model.addAttribute("name", location.getName());
		model.addAttribute("uniqueErrorMessage", "Specified Location name already exists, please specify another");
		
	}
	
	public boolean sameEditedNameAndActualName(int id, String editedName) {
		boolean sameName = false;
		Location location = repository.findById(id, "id", Location.class);
		String actualName = location.getName();
		if (actualName.equalsIgnoreCase(editedName)) {
			sameName = true;
		}
		return sameName;
	}
	
	public static void treeTraverse(Map<String, TreeNode<String, Location>> lotree) {
		TreeNode<String, Location> treeNode = null;
		int i = 0;
		String div = "";
		for (Map.Entry<String, TreeNode<String, Location>> entry : lotree.entrySet()) {
			i++;
			treeNode = entry.getValue();
			Map<String, TreeNode<String, Location>> children = treeNode.getChildren();
			div = "</div>" + treeNode.getNode().getName() + "</div>";
			//System.err.println("Parent" + treeNode.getParent() + "child: " + i + "->" + treeNode.getNode().getName());
			System.err.println("I;" + div);
			if (children != null) {
				treeTraverse(children);
			} else {
				i = 0;
			}
		}
		
	}
	
	public JSONArray getLocationDataAsJson(String parentIndication, String parentKey) throws JSONException {
		JSONArray dataArray = new JSONArray();
		
		List<Location> locations = findAll("Location");
		for (Location location : locations) {
			JSONObject dataObject = new JSONObject();
            Location parentLocation = location.getParentLocation();
            if (parentLocation != null) {
                dataObject.put(parentKey, parentLocation.getId());
            } else {
                dataObject.put(parentKey, parentIndication);
            }
			dataObject.put("id", location.getId());
			dataObject.put("text", location.getName());
			dataObject.put("icon", location.getLocationTag().getName());
			dataArray.put(dataObject);
		}
		
		return dataArray;
		
	}
	
	@Transactional
	public List<Location> getAllByKeysWithALlMatches(String name) {
		Map<String, String> fielaValues = new HashMap<String, String>();
		fielaValues.put("name", name);
		boolean isProvider = false;
		return repository.findAllByKeysWithALlMatches(isProvider, fielaValues, Location.class);
	}
	
	public String makeParentLocationName(Location location) {
		String parentLocationName = "";
		String tagNme = "";
		String locationName = "";
		if (location.getParentLocation() != null) {
			location = repository.findById(location.getParentLocation().getId(), "id", Location.class);
			if (location.getParentLocation() != null) {
				parentLocationName = location.getParentLocation().getName() + " -> ";
			}
			
			if (location.getLocationTag() != null) {
				tagNme = "  (" + location.getLocationTag().getName() + ")";
			}
			locationName = location.getName();
		}
		return parentLocationName + locationName + tagNme;
	}
	
	public String makeLocationName(Location location) {
		String parentLocationName = "";
		String tagNme = "";
		String locationName = "";
		if (location.getParentLocation() != null) {
			if (location.getParentLocation() != null) {
				parentLocationName = location.getParentLocation().getName() + " -> ";
			}
			
			if (location.getLocationTag() != null) {
				tagNme = "  (" + location.getLocationTag().getName() + ")";
			}
			locationName = location.getName();
		}
		return parentLocationName + locationName + tagNme;
	}
	
	public JSONArray search(String name) throws JSONException {
		JSONArray locationJsonArray = new JSONArray();
		String locationName = "";
		String parentLocationName = "";
		List<Location> locations = getAllByKeysWithALlMatches(name);
		if (locations != null) {
			for (Location location : locations) {
				
				JSONObject locationJsonObject = new JSONObject();
				if (location.getParentLocation() != null) {
					parentLocationName = location.getParentLocation().getName() + " > ";
				} else {
					parentLocationName = "";
				}
				locationName = parentLocationName + location.getName();
				locationJsonObject.put("label", locationName);
				locationJsonObject.put("id", location.getId());
				locationJsonArray.put(locationJsonObject);
			}
		}
		return locationJsonArray;
	}
	
	public JSONArray list() throws JSONException {
		JSONArray locationJsonArray = new JSONArray();
		String locationName = "";
		String parentLocationName = "";
		List<Location> locations = findAll("Location");
		if (locations != null) {
			for (Location location : locations) {
				
				JSONObject locationJsonObject = new JSONObject();
				if (location.getParentLocation() != null) {
					parentLocationName = location.getParentLocation().getName() + " > ";
				} else {
					parentLocationName = "";
				}
				locationName = parentLocationName + location.getName();
				locationJsonObject.put("value", locationName);
				locationJsonObject.put("id", location.getId());
				locationJsonArray.put(locationJsonObject);
			}
		}
		return locationJsonArray;
	}
	
	@SuppressWarnings("resource")
	public String uploadLocation(File csvFile) throws Exception {
		String msg = "";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		int position = 0;
		String[] tags = null;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String tag = "";
				String code = "";
				String name = "";
				String parent = "";
				String[] locations = line.split(cvsSplitBy);
				if (position == 0) {
					tags = locations;
				} else {
					for (int i = 0; i < locations.length; i = i + 2) {
						code = locations[i];
						name = locations[i + 1];
						if (i != 0) {
							parent = locations[i - 1];
						}
						tag = tags[i + 1];
						LocationTag locationTag = findByKey(tag, "name", LocationTag.class);
						Location parentLocation = findByKey(parent.toUpperCase().trim(), "name", Location.class);
						Location isExists = findByKey(name.toUpperCase().trim(), "name", Location.class);
						Location location = new Location();
						location.setCode(code);
						location.setName(name.toUpperCase().trim());
						location.setLocationTag(locationTag);
						location.setParentLocation(parentLocation);
						location.setDescription(name);
						location = (Location) openMRSServiceFactory.getOpenMRSConnector("location").add(location);
						if (!location.getUuid().isEmpty()) {
							if (isExists == null) {
								repository.save(location);
							} else {
								logger.info("already exists location:" + location.getName());
							}
						} else {
							logger.info("No uuid found for location:" + location.getName());
							
						}
						
					}
				}
				position++;
			}
			
		}
		catch (Exception e) {
			logger.info("Some problem occured, please contact with admin..");
			msg = "Some problem occured, please contact with admin..";
		}
		return msg;
	}

	public Set<Location> getLocationByIds(int[] locations) {
		Set<Location> locationSet = new HashSet<Location>();
		if (locations != null && locations.length != 0) {
			for (int locationId : locations) {
				Location location = repository.findById(locationId, "id", Location.class);
				if (location != null) {
					locationSet.add(location);
				}
			}
		}
		return locationSet;
	}

	public JSONArray convertLocationTreeToJSON(List<LocationTreeDTO> treeDTOS) throws JSONException {
		JSONArray locationTree = new JSONArray();

		Map<String, Boolean> mp = new HashMap<>();
		JSONObject object = new JSONObject();
		JSONArray locations = new JSONArray();
		JSONObject fullLocation = new JSONObject();

		int counter = 0;
		String username = "";

		for (LocationTreeDTO treeDTO: treeDTOS) {
			counter++;
			if (mp.get(treeDTO.getUsername()) == null || !mp.get(treeDTO.getUsername())) {
				if (counter > 1) {
					object.put("username", username);
					object.put("locations", locations);
					locationTree.put(object);
					locations = new JSONArray();
					object = new JSONObject();
				}
				mp.put(treeDTO.getUsername(), true);
			}

			username = treeDTO.getUsername();

			if (treeDTO.getLoc_tag_name().equalsIgnoreCase("country")) {
				if (counter > 1) {
					fullLocation = setEmptyValues(fullLocation);
					locations.put(fullLocation);
					fullLocation = new JSONObject();
				}
			}

			JSONObject location = new JSONObject();
			location.put("code", treeDTO.getCode());
			location.put("id", treeDTO.getId());
			location.put("name", treeDTO.getName());
			fullLocation.put(treeDTO.getLoc_tag_name().toLowerCase().replaceAll(" ", "_"), location);

			if (counter == treeDTOS.size()) {
				locations.put(fullLocation);
				object.put("username", username);
				object.put("locations", locations);
				locationTree.put(object);
				object = new JSONObject();
				locations = new JSONArray();
			}
		}
		return locationTree;
	}

	private JSONObject getLocationProperty() throws JSONException {
		JSONObject property = new JSONObject();
		property.put("name", "");
		property.put("id", 0);
		property.put("code", "00");
		return property;
	}

	private JSONObject setEmptyValues(JSONObject fullLocation) throws JSONException {
		if (!fullLocation.has("country")) {
			fullLocation.put("country", getLocationProperty());
		}
		if (!fullLocation.has("division")) {
			fullLocation.put("division", getLocationProperty());
		}
		if (!fullLocation.has("district")) {
			fullLocation.put("district", getLocationProperty());
		}
		if (!fullLocation.has("city_corporation")) {
			fullLocation.put("city_corporation", getLocationProperty());
		}
		if (!fullLocation.has("upazilla")) {
			fullLocation.put("upazilla", getLocationProperty());
		}
		if (!fullLocation.has("union")) {
			fullLocation.put("union", getLocationProperty());
		}
		if (!fullLocation.has("ward")) {
			fullLocation.put("ward", getLocationProperty());
		}
		if (!fullLocation.has("block")) {
			fullLocation.put("block", getLocationProperty());
		}
		if (!fullLocation.has("village")) {
			fullLocation.put("village", getLocationProperty());
		}
		return fullLocation;
	}
}
