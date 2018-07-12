package org.opensrp.connector.openmrs.service.impl;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:test-applicationContext.xml" })
public class OpenmrsLocationServiceTest {
	
	@Autowired
	private OpenMRSAPIServiceImpl openMRSAPIService;
	
	final String LOCATION_URL = "ws/rest/v1/location";
	
	final String LOCATION_TAG_URL = "ws/rest/v1/locationtag";
	
	public final static String nameKey = "name";
	
	public final static String tagsKey = "tags";
	
	public final static String locationName = "bdDB";
	
	public final static String tagName = "TGG";
	
	public static String tags = "Country";
	
	public final static String parentLocationKey = "parentLocation";
	
	public static String parentLocation = "44221e79-b3f0-496f-9d3c-467216fa1d53";
	
	@Before
	public void setup() throws IOException {
		
	}
	
	public JSONObject makeLocationObject(String name, String tags, String parentLocation) throws JSONException {
		JSONObject location = new JSONObject();
		
		JSONArray tagsArray = new JSONArray();
		JSONObject tagsObject = new JSONObject();
		
		tagsObject.put("tag", tags);
		tagsArray.put(tagsObject);
		
		location.put(nameKey, name);
		location.put(tagsKey, tagsArray);
		location.put(parentLocationKey, "");
		
		return location;
	}
	
	public JSONObject makeTagsObject(String name) throws JSONException {
		JSONObject tag = new JSONObject();
		tag.put(nameKey, name);
		return tag;
	}
	
	public JSONObject createTag() throws JSONException {
		JSONObject tag = openMRSAPIService.add("", makeTagsObject(tagName), LOCATION_TAG_URL);
		return tag;
	}
	
	public JSONObject createLocation(String tags) throws JSONException {
		return openMRSAPIService.add("", makeLocationObject(locationName, tags, parentLocation), LOCATION_URL);
		
	}
	
	public JSONObject updateLocation(String uuid) throws JSONException {
		tags = "District";
		parentLocation = "b585f3e0-273b-4fb2-b273-e4d5b08eda22";
		return openMRSAPIService.update("", makeLocationObject(locationName, tags, parentLocation), uuid, LOCATION_URL);
		
	}
	
	@Test
	public void createUpdateGetAndDeleteLocation() throws JSONException {
		
		JSONObject createdLocationTag = createTag();
		System.err.println(createdLocationTag);
		String tagUuid = (String) createdLocationTag.get("uuid");
		
		/**
		 * create location information
		 */
		
		JSONObject createdLocation = createLocation(tagName);
		
		String locationUuid = (String) createdLocation.get("uuid");
		String createdLocationName = (String) createdLocation.get("name");
		Assert.assertEquals(locationName, createdLocationName);
		
		/**
		 * update location information
		 */
		JSONObject updatedLocation = updateLocation(locationUuid);
		String updatedLocationUuid = (String) updatedLocation.get("uuid");
		Assert.assertEquals(locationUuid, updatedLocationUuid);
		
		/**
		 * get location information
		 */
		JSONObject getLocationObject = openMRSAPIService.get("v=full", locationUuid, LOCATION_URL);
		String getLocationObjectUuid = (String) getLocationObject.get("uuid");
		Assert.assertEquals(locationUuid, getLocationObjectUuid);
		
		/**
		 * delete location information
		 */
		openMRSAPIService.delete("", locationUuid, LOCATION_URL);
		openMRSAPIService.delete("", tagUuid, LOCATION_TAG_URL);
	}
}
