/**
 * 
 */
package org.opensrp.web.nutrition.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.opensrp.common.entity.Marker;
import org.opensrp.common.repository.impl.DatabaseRepositoryImpl;
import org.opensrp.common.service.impl.MarkerServiceImpl;
import org.opensrp.common.util.AllConstant;
import org.opensrp.common.util.DateUtil;
import org.opensrp.common.util.SearchBuilder;
import org.opensrp.common.util.SearchCriteria;
import org.opensrp.web.nutrition.entity.ChildGrowth;
import org.opensrp.web.nutrition.service.NutritionService;
import org.opensrp.web.nutrition.utils.Age;
import org.opensrp.web.nutrition.utils.GrowthValocityChart;
import org.opensrp.web.nutrition.utils.Interval;
import org.opensrp.web.nutrition.utils.Weight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author proshanto
 */

@Service
public class ChildGrowthServiceImpl implements NutritionService {
	
	private static final Logger logger = Logger.getLogger(ChildGrowthServiceImpl.class);
	
	@Autowired
	private MarkerServiceImpl markerServiceImpl;
	
	@Autowired
	private Marker marker;
	
	@Autowired
	private DatabaseRepositoryImpl databaseRepositoryImpl;
	
	@Autowired
	private SearchBuilder searchBuilder;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	@Override
	public <T> long save(T t) throws Exception {
		
		return databaseRepositoryImpl.save(t);
	}
	
	@Transactional
	@Override
	public <T> int update(T t) throws Exception {
		
		return databaseRepositoryImpl.update(t);
	}
	
	@Transactional
	@Override
	public <T> boolean delete(T t) {
		
		return databaseRepositoryImpl.delete(t);
	}
	
	@Transactional
	@Override
	public <T> T findById(int id, String fieldName, Class<?> className) {
		
		return databaseRepositoryImpl.findById(id, fieldName, className);
	}
	
	@Transactional
	@Override
	public <T> T findByKey(String value, String fieldName, Class<?> className) {
		
		return databaseRepositoryImpl.findByKey(value, fieldName, className);
	}
	
	@Transactional
	@Override
	public <T> List<T> findAll(String tableClass) {
		// TODO Auto-generated method stub
		return databaseRepositoryImpl.findAll(tableClass);
	}
	
	@Transactional
	public void startCalculateChildGrowth() throws Exception {
		marker = markerServiceImpl.findByName(AllConstant.MRAKER_NAME);
		searchBuilder.clear();
		searchBuilder.setServerVersionn(marker.getTimeStamp());
		List<Object[]> childWeights = databaseRepositoryImpl.getDataFromView(searchBuilder, -1, -1,
		    "viewJsonDataConversionOfWeight", "weight");
		
		List<Map<String, Integer>> growthValocityChart = GrowthValocityChart.getGrowthValocityChart();
		
		Map<String, Integer> maleGrowthValocityChart = growthValocityChart.get(0);
		
		Map<String, Integer> femaleGrowthValocityChart = growthValocityChart.get(1);
		
		for (Object[] row : childWeights) {
			
			double currentWeight = 0;
			int weight = 0;
			boolean growthStatus = false;
			int interval = 0;
			String provider = "";
			int age = 0;
			String key = "";
			String gender = "";
			int expectedGrowthWeight = 0;
			double zScore = 0;
			String gps = "";
			long currentDocumentTimeStamp = Long.parseLong(String.valueOf(row[4]));
			try {
				zScore = Double.parseDouble(String.valueOf(row[6]));
				gps = String.valueOf(row[11]);
				String[] latlon = gps.split(" ");
				String baseEntityId = String.valueOf(row[0]);
				Date dob = DateUtil.parseDate(String.valueOf(row[7]));
				
				Date currentEventDate = DateUtil.parseDate(String.valueOf(row[1]));
				currentWeight = Double.parseDouble(String.valueOf(row[5]));
				provider = String.valueOf(row[3]);
				double lastWeight = 0;
				String lastEventDate = String.valueOf(row[9]);
				if (!String.valueOf(row[8]).equalsIgnoreCase("null")) {
					lastWeight = Double.parseDouble(String.valueOf(row[8]));
				}
				
				weight = (int) Weight.getWeightInGram(lastWeight, currentWeight);
				
				interval = Interval.getInterval(DateUtil.parseDate(lastEventDate), currentEventDate);
				
				age = Age.getApproximateAge(dob, currentEventDate);
				
				key = String.valueOf(interval) + String.valueOf(age);
				
				gender = String.valueOf(row[10]);
				if ("Male".equalsIgnoreCase(gender)) {
					if (maleGrowthValocityChart.containsKey(key)) {
						expectedGrowthWeight = (int) maleGrowthValocityChart.get(key);
					}
					
				} else {
					if (femaleGrowthValocityChart.containsKey(key)) {
						expectedGrowthWeight = (int) femaleGrowthValocityChart.get(key);
					}
					
				}
				
				if (weight >= expectedGrowthWeight) {
					growthStatus = true;
				}
				
				Map<String, Object> fielaValues = new HashMap<String, Object>();
				fielaValues.put("lastEventDate", currentEventDate);
				fielaValues.put("baseEntityId", baseEntityId);
				
				ChildGrowth findChildGrowth = databaseRepositoryImpl.findByKeys(fielaValues, ChildGrowth.class);
				
				if (findChildGrowth != null) {
					findChildGrowth.setAge(age);
					findChildGrowth.setBaseEntityId(baseEntityId);
					findChildGrowth.setGrowthStatus(growthStatus);
					findChildGrowth.setzScore(zScore);
					findChildGrowth.setWeight(weight);
					findChildGrowth.setLastEventDate(currentEventDate);
					findChildGrowth.setGender(gender);
					findChildGrowth.setInterval(interval);
					findChildGrowth.setProvider(provider);
					findChildGrowth.setLastEvent(false);
					if (!gps.equalsIgnoreCase("null")) {
						
						findChildGrowth.setLat(Double.parseDouble(latlon[0]));
						findChildGrowth.setLon(Double.parseDouble(latlon[1]));
						
					}
					update(findChildGrowth);
				} else {
					ChildGrowth childGrowth = new ChildGrowth();
					childGrowth.setAge(age);
					childGrowth.setBaseEntityId(baseEntityId);
					childGrowth.setGrowthStatus(growthStatus);
					childGrowth.setzScore(zScore);
					childGrowth.setWeight(weight);
					childGrowth.setLastEventDate(currentEventDate);
					childGrowth.setGender(gender);
					childGrowth.setInterval(interval);
					childGrowth.setProvider(provider);
					childGrowth.setLastEvent(false);
					if (!gps.equalsIgnoreCase("null")) {
						
						childGrowth.setLat(Double.parseDouble(latlon[0]));
						childGrowth.setLon(Double.parseDouble(latlon[1]));
						
					}
					save(childGrowth);
				}
				isLastEventUpdate(baseEntityId, provider, fielaValues);
				if (marker.getTimeStamp() < currentDocumentTimeStamp) {
					marker.setTimeStamp(currentDocumentTimeStamp);
					markerServiceImpl.update(marker);
				}
				
				//System.err.println("weight:" + weight);
				
				//System.err.println(row[0] + ",Interval:" + Interval.getInterval(dob, eventDate));
			}
			catch (Exception e) {
				logger.error("Error:" + e.getMessage());
			}
		}
		
	}
	
	private void isLastEventUpdate(String baseEntityId, String provider, Map<String, Object> fielaValues) throws Exception {
		/**
		 * make false all true entity
		 */
		fielaValues.clear();
		fielaValues.put("isLastEvent", true);
		fielaValues.put("baseEntityId", baseEntityId);
		fielaValues.put("provider", provider);
		List<ChildGrowth> childGrowths = databaseRepositoryImpl.findAllByKeys(fielaValues, ChildGrowth.class);
		//System.err.println("childGrowths:" + childGrowths);
		if (childGrowths != null) {
			for (ChildGrowth childGrowth : childGrowths) {
				childGrowth.setLastEvent(false);
				System.err.println("childGrowth all:" + childGrowth.toString());
				update(childGrowth);
			}
		}
		
		fielaValues.clear();
		fielaValues.put("provider", provider);
		fielaValues.put("baseEntityId", baseEntityId);
		System.err.println("fielaValues:" + fielaValues);
		ChildGrowth childGrowth = databaseRepositoryImpl.findLastByKey(fielaValues, "lastEventDate", ChildGrowth.class);
		System.err.println("childGrowth Lstststs:" + childGrowth);
		childGrowth.setLastEvent(true);
		System.err.println("childGrowth last:" + childGrowth.toString());
		update(childGrowth);
	}
	
	@Transactional
	public List<Object[]> getChildFalteredData(SearchBuilder searchBuilder) {
		Session session = sessionFactory.openSession();
		searchBuilder.clear();
		String procedureName = "core.child_growth_report";
		String hql = "select * from " + procedureName + "(array[:division,:district,:upazila"
		        + ",:union,:ward,:subunit,:mauzapara,:provider,:start_date,:end_date])";
		
		Query query = session.createSQLQuery(hql);
		setParameter(searchBuilder, query);
		
		return databaseRepositoryImpl.getDataFromSQLFunction(searchBuilder, query, session);
	}
	
	private void setParameter(SearchBuilder searchBuilder, Query query) {
		if (searchBuilder.getDivision() != null && !searchBuilder.getDivision().isEmpty()) {
			query.setParameter("division", searchBuilder.getDivision());
		} else {
			query.setParameter("division", "");
		}
		if (searchBuilder.getDistrict() != null && !searchBuilder.getDistrict().isEmpty()) {
			query.setParameter("district", searchBuilder.getDistrict());
		} else {
			query.setParameter("district", "");
		}
		
		if (searchBuilder.getUpazila() != null && !searchBuilder.getUpazila().isEmpty()) {
			query.setParameter("upazila", searchBuilder.getUpazila());
		} else {
			query.setParameter("upazila", "");
		}
		
		if (searchBuilder.getUnion() != null && !searchBuilder.getUnion().isEmpty()) {
			query.setParameter("union", searchBuilder.getUnion());
		} else {
			query.setParameter("union", "");
		}
		
		if (searchBuilder.getWard() != null && !searchBuilder.getWard().isEmpty()) {
			query.setParameter("ward", searchBuilder.getWard());
		} else {
			query.setParameter("ward", "");
		}
		
		if (searchBuilder.getSubunit() != null && !searchBuilder.getSubunit().isEmpty()) {
			query.setParameter("subunit", searchBuilder.getSubunit());
		} else {
			query.setParameter("subunit", "");
		}
		
		if (searchBuilder.getMauzapara() != null && !searchBuilder.getMauzapara().isEmpty()) {
			query.setParameter("mauzapara", searchBuilder.getMauzapara());
		} else {
			query.setParameter("mauzapara", "");
		}
		
		if (searchBuilder.getProvider() != null && !searchBuilder.getProvider().isEmpty()) {
			query.setParameter("provider", searchBuilder.getProvider());
		} else {
			query.setParameter("provider", "");
		}
		
		query.setParameter("start_date", "");
		query.setParameter("end_date", "");
		
	}
}
