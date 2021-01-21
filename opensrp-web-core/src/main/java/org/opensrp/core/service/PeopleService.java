/**
 * @author proshanto
 * */

package org.opensrp.core.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.common.dto.ActivityListDTO;
import org.opensrp.common.dto.ClientListDTO;
import org.opensrp.core.mapper.TargetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PeopleService extends CommonService {
	
	private static final Logger logger = Logger.getLogger(PeopleService.class);
	
	@Autowired
	private TargetMapper targetMapper;
	
	public PeopleService() {
		
	}
	
	static final List<String> householdColumnList;
	static {
		householdColumnList = new ArrayList<String>();
		householdColumnList.add("householdId");
		householdColumnList.add("householdHead");
		householdColumnList.add("numberOfMember");
		householdColumnList.add("registrationDate");
		householdColumnList.add("lastVisitDate");
		householdColumnList.add("village");
		householdColumnList.add("branchAndCode");
		householdColumnList.add("contact");
		
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ClientListDTO> getHouseholdData(JSONObject jo) throws JSONException, JsonProcessingException {
		Session session = getSessionFactory();
		
		List<ClientListDTO> householdList = new ArrayList<ClientListDTO>();
		
		String hql = "select  id,vid,hh_id householdId,hh_name householdHead,member_count numberOfMember,reg_date registrationDate"
		        + " ,last_visit_date lastVisitDate,village,branch_name branchName,branch_code branchCode,"
		        + " contact contact,base_entity_id baseEntityId from report.get_household_list('" + jo + "')";
		
		Query query = session.createSQLQuery(hql).addScalar("id", StandardBasicTypes.LONG)
		        .addScalar("vid", StandardBasicTypes.LONG).addScalar("householdId", StandardBasicTypes.STRING)
		        .addScalar("householdHead", StandardBasicTypes.STRING)
		        .addScalar("numberOfMember", StandardBasicTypes.INTEGER)
		        .addScalar("registrationDate", StandardBasicTypes.STRING)
		        .addScalar("lastVisitDate", StandardBasicTypes.STRING).addScalar("village", StandardBasicTypes.STRING)
		        .addScalar("branchName", StandardBasicTypes.STRING).addScalar("branchCode", StandardBasicTypes.STRING)
		        .addScalar("contact", StandardBasicTypes.STRING).addScalar("baseEntityId", StandardBasicTypes.STRING)
		        .setResultTransformer(new AliasToBeanResultTransformer(ClientListDTO.class));
		householdList = query.list();
		
		//JSONObject obj = new JSONObject(json);
		/*householdList.stream().forEach(household -> {
			
			for (int i = 0; i < household.length; i++) {
				System.err.println("JJ:" + household[i]);
			}
			System.err.println("----------------------jsonObject---");
		});*/
		
		return householdList;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<String> geHHList(JSONObject jo) {
		Session session = getSessionFactory();
		
		List<String> householdList = new ArrayList<String>();
		
		String hql = "select  * from report.get_household_list('" + jo + "')";
		
		Query query = session.createSQLQuery(hql);
		householdList = query.list();
		return householdList;
	}
	
	@Transactional
	public int getHHListCount(JSONObject jo) {
		
		Session session = getSessionFactory();
		BigInteger total = null;
		
		String hql = "select  * from report.get_household_list_count('" + jo + "')";
		Query query = session.createSQLQuery(hql);
		total = (BigInteger) query.uniqueResult();
		
		return total.intValue();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<String> getMemberList(JSONObject jo, Integer startAge, Integer endAge) {
		Session session = getSessionFactory();
		
		List<String> memberList = new ArrayList<String>();
		
		String hql = "select  * from report.member_list('" + jo + "',:startAge,:endAge)";
		
		Query query = session.createSQLQuery(hql).setInteger("startAge", startAge).setInteger("endAge", endAge);
		memberList = query.list();
		return memberList;
	}
	
	@Transactional
	public int getMemberListCount(JSONObject jo, Integer startAge, Integer endAge) {
		
		Session session = getSessionFactory();
		BigInteger total = null;
		
		String hql = "select  * from report.member_list_count('" + jo + "',:startAge,:endAge)";
		Query query = session.createSQLQuery(hql).setInteger("startAge", startAge).setInteger("endAge", endAge);
		total = (BigInteger) query.uniqueResult();
		
		return total.intValue();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ClientListDTO> getServiceList(String baseEntityId, String type) {
		Session session = getSessionFactory();
		
		List<ClientListDTO> householdList = new ArrayList<ClientListDTO>();
		String hql = "";
		if (type.equalsIgnoreCase("HH")) {
			hql = "select  _id id,_form_submission_id formSubmissionId,_event_date eventDate,_service_name serviceName,_table_name tableName "
			        + "  from report.household_activity_list(:baseEntityId)";
		} else if (type.equalsIgnoreCase("Member")) {
			hql = "select  _id id,_form_submission_id formSubmissionId,_event_date eventDate,_service_name serviceName,_table_name tableName "
			        + "  from report.memebr_activity_list(:baseEntityId)";
		} else {
			
		}
		Query query = session.createSQLQuery(hql).addScalar("id", StandardBasicTypes.LONG)
		        .addScalar("formSubmissionId", StandardBasicTypes.STRING).addScalar("eventDate", StandardBasicTypes.STRING)
		        .addScalar("serviceName", StandardBasicTypes.STRING).addScalar("tableName", StandardBasicTypes.STRING)
		        .setString("baseEntityId", baseEntityId)
		        .setResultTransformer(new AliasToBeanResultTransformer(ClientListDTO.class));
		householdList = query.list();
		return householdList;
	}
	
	public JSONObject drawHouseholdDataTable(Integer draw, int total, List<String> lists, int start) throws JSONException {
		JSONObject response = new JSONObject();
		response.put("draw", draw + 1);
		response.put("recordsTotal", total);
		response.put("recordsFiltered", total);
		JSONArray array = new JSONArray();
		int i = 1;
		for (String string : lists) {
			JSONObject row = new JSONObject(string);
			
			String baseEntityId = row.getString("base_entity_id");
			String id = row.getString("hid");
			JSONArray tableData = new JSONArray();
			tableData.put(start + i);
			tableData.put(row.get("unique_id"));
			tableData.put(row.get("first_name"));
			tableData.put(row.get("member_count"));
			tableData.put(row.get("event_date"));
			tableData.put(row.get("last_visit_date"));
			tableData.put(row.get("village"));
			tableData.put(row.get("branch_name") + " " + row.getString("code"));
			tableData.put(row.get("contact_phone_number"));
			
			String view = "<div class='col-sm-12 form-group'><a class='text-primary' \" href=\"household-details/"
			        + baseEntityId + "/" + id + ".html" + "\">Details</a> </div>";
			tableData.put(view);
			array.put(tableData);
			i++;
		}
		
		response.put("data", array);
		return response;
	}
	
	public JSONObject drawMemberDataTable(Integer draw, int total, List<String> dtos, int start) throws JSONException {
		JSONObject response = new JSONObject();
		response.put("draw", draw + 1);
		response.put("recordsTotal", total);
		response.put("recordsFiltered", total);
		JSONArray array = new JSONArray();
		int i = 1;
		for (String string : dtos) {
			JSONArray tableData = new JSONArray();
			
			JSONObject json = new JSONObject(string);
			tableData.put(start + i);
			tableData.put(json.get("first_name"));
			tableData.put(json.get("member_id"));
			tableData.put(json.get("relation_with_household_head"));
			tableData.put(json.get("birthdate"));
			tableData.put(json.get("member_age_year"));
			tableData.put(json.get("gender"));
			tableData.put(json.get("village"));
			tableData.put(json.get("branch_name") + " " + json.getString("code"));
			String view = "<div class='col-sm-12 form-group'><a class='text-primary' \" href=\"member-details/"
			        + json.getString("base_entity_id") + "/" + json.getString("id") + ".html" + "\">Details</a> </div>";
			tableData.put(view);
			array.put(tableData);
			i++;
		}
		
		response.put("data", array);
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ActivityListDTO> getServiceInfo(String baseEntityId, Long id, String tableName) throws JSONException {
		
		Session session = getSessionFactory();
		List<ActivityListDTO> activityList = new ArrayList<ActivityListDTO>();
		String hql = "select  r_no serialNo,_qs question,_val answer from report.get_details_data(:id,:tableName)";
		
		/* Query query = session.createSQLQuery(hql).setLong("id", id).setString("tableName", tableName);
		String rs = (String) query.uniqueResult();*/
		Query query = session.createSQLQuery(hql).addScalar("serialNo", StandardBasicTypes.INTEGER)
		        .addScalar("question", StandardBasicTypes.STRING).addScalar("answer", StandardBasicTypes.STRING)
		        .setLong("id", id).setString("tableName", tableName)
		        .setResultTransformer(new AliasToBeanResultTransformer(ActivityListDTO.class));
		activityList = query.list();
		
		return activityList;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ActivityListDTO> getMemberInfo(String baseEntityId) throws JSONException {
		
		Session session = getSessionFactory();
		List<ActivityListDTO> activityList = new ArrayList<ActivityListDTO>();
		String hql = "select  r_no serialNo,_qs question,_val answer  from report.member_info(:baseEntityId)";
		
		//Query query = session.createSQLQuery(hql).setString("baseEntityId", baseEntityId);
		Query query = session.createSQLQuery(hql).addScalar("serialNo", StandardBasicTypes.INTEGER)
		        .addScalar("question", StandardBasicTypes.STRING).addScalar("answer", StandardBasicTypes.STRING)
		        .setString("baseEntityId", baseEntityId)
		        .setResultTransformer(new AliasToBeanResultTransformer(ActivityListDTO.class));
		activityList = query.list();
		
		return activityList;
	}
	
	public List<ClientListDTO> getServiceList(String baseEntityId) {
		List<ClientListDTO> dtos = new ArrayList<ClientListDTO>();
		for (int i = 0; i < 50; i++) {
			
			ClientListDTO dto = new ClientListDTO();
			dto.setEventDate("2020-03-20");
			dto.setId(Long.parseLong(i + ""));
			dto.setServiceName("HH_Visit" + 1);
			dtos.add(dto);
		}
		
		return dtos;
	}
	
	public JSONObject getServiceDetails(String formName, long id) throws JSONException {
		String response = "{\"form_name\":\"HH_Registration\",\"data\":[{\"value\":2,\"key\":\"id\"},{\"value\":\"json roy\",\"key\":\"name\"},{\"value\":\"naNDID GR\",\"key\":\"village\"},{\"value\":\"23453453535\",\"key\":\"householdId\"},{\"value\":\"VO\",\"key\":\"khnanaType\"},{\"value\":\"Farmer\",\"key\":\"occupation\"},{\"value\":\"2345\",\"key\":\"montlyIncome\"},{\"value\":\"TV\",\"key\":\"asset\"}]}";
		JSONObject responseObj = new JSONObject(response);
		return responseObj;
	}
}
