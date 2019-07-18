package org.opensrp.common.interfaces;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface DatabaseService {
	
	public <T> long save(T t) throws Exception;
	
	public <T> long update(T t) throws Exception;
	
	public <T> int delete(T t);
	
	public <T> T findById(int id, String fieldName, Class<?> className);
	
	public <T> T findByKey(String value, String fieldName, Class<?> className);
	
	public <T> List<T> findAll(String tableClass);

	public <T> List<T> getHouseholdListByMHV(String username, HttpSession session);

	public <T> List<T> getMemberListByHousehold(String householdBaseId);

	public <T> T getMemberByHealthId(String healthId);

	public <T> List<T> getMemberListByCC(String ccName);

}
