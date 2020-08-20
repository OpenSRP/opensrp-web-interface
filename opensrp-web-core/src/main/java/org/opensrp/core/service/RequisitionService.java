/**
 * @author proshanto
 * */

package org.opensrp.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.common.dto.RequisitionQueryDto;
import org.opensrp.common.dto.UserDTO;
import org.opensrp.core.dto.ProductDTO;
import org.opensrp.core.dto.RequisitionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequisitionService extends CommonService {
	
	private static final Logger logger = Logger.getLogger(RequisitionService.class);
	
	@Autowired
	private ProductService productService;
	
	public RequisitionService() {
		
	}
	
	public List<ProductDTO> productListFortRequisition(Integer branchId, Integer roleId) {
		List<ProductDTO> products = productService.productListByBranchWithCurrentStock(branchId, roleId);
		
		List<Long> pids = new ArrayList<>();
		
		for (ProductDTO productDTO : products) {
			pids.add(productDTO.getId());
		}
		products.addAll(productService.productListWithoutBranch(StringUtils.join(pids.toArray(), ", "), roleId));
		
		return products;
	}
	/*private List<Requisition> listWithPagination(int branchId){
		Session session = getSessionFactory().openSession();
		List<Requisition> result = null;
		try {
			String hql = "select  * from   core.requisition where  ";
			Query query = session.createSQLQuery(hql).addScalar("name", StandardBasicTypes.STRING)
			        .addScalar("id", StandardBasicTypes.LONG).addScalar("stock", StandardBasicTypes.INTEGER)
			        .setInteger("branchId", branchId).setInteger("roleId", roleId)
			        .setResultTransformer(new AliasToBeanResultTransformer(ProductDTO.class));
			result = query.list();
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			session.close();
		}
		
		return result;
	}*/
	
	
	@SuppressWarnings("unchecked")
	public Long getCountOfRequisiton(Integer branchId, String startDate, String endDate,int divisionId,int distirct,int upazilla,int user_id) {
		Session session = getSessionFactory().openSession();
		List<RequisitionQueryDto> result = null;
		try {
			String hql = "select * from core.requisition_count("+branchId+",'"+startDate+"','"+endDate+"',"+divisionId+","+distirct+","+upazilla+","+user_id+")";
			Query query = session.createSQLQuery(hql).addScalar("requisition_count",StandardBasicTypes.LONG)
						.setResultTransformer(new AliasToBeanResultTransformer(RequisitionQueryDto.class));
			result = query.list();
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			session.close();
		}
		
		return result.get(0).getRequisition_count();
	}
	
	@SuppressWarnings("unchecked")
	public List<RequisitionQueryDto> getRequisitonList(Integer branchId, String startDate, String endDate,int divisionId,int distirct,int upazilla,int user_id,int offSetNo, int limit) {
		Session session = getSessionFactory().openSession();
		List<RequisitionQueryDto> result = null;
		try {
			String hql = "select * from core.requisition_list("+branchId+",'"+startDate+"','"+endDate+"',"+divisionId+","+distirct+","+upazilla+","+user_id+","+offSetNo+","+limit+")";
			Query query = session.createSQLQuery(hql).addScalar("id",StandardBasicTypes.LONG)
							.addScalar("requisition_date",StandardBasicTypes.STRING).addScalar("requisition_id",StandardBasicTypes.STRING)
							.addScalar("branch_name",StandardBasicTypes.STRING).addScalar("branch_code",StandardBasicTypes.STRING)
							.addScalar("username",StandardBasicTypes.STRING).addScalar("first_name",StandardBasicTypes.STRING)
							.addScalar("last_name",StandardBasicTypes.STRING)
						.setResultTransformer(new AliasToBeanResultTransformer(RequisitionQueryDto.class));
			result = query.list();
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			session.close();
		}
		
		return result;
	}
	
	public JSONObject getRequisitionDataOfDataTable(Integer draw, Long requisitionCount, List<RequisitionQueryDto> dtos)
		    throws JSONException {
			JSONObject response = new JSONObject();
			response.put("draw", draw + 1);
			response.put("recordsTotal", requisitionCount);
			response.put("recordsFiltered", requisitionCount);
			JSONArray array = new JSONArray();
			for (RequisitionQueryDto dto : dtos) {
				JSONArray requisition = new JSONArray();
				requisition.put(dto.getId());
				requisition.put(dto.getRequisition_date());
				requisition.put(dto.getRequisition_id());
				requisition.put(dto.getBranch_name() + "("+dto.getBranch_code()+")");
				requisition.put(dto.getFirst_name() + dto.getLast_name());
				String view = "<div class='col-sm-12 form-group'><a id=\"viewDetails\" class=\"bt btn btn-success col-sm-12 form-group sm\" href=\"javascript:;\" onclick='navigateTodetails("+dto.getId()+",\""+dto.getBranch_name()+"\",\""+dto.getBranch_code()+"\")'>View details</a> </div>";
				requisition.put(view);
				array.put(requisition);
			}
			response.put("data", array);
			return response;
		}
	
	@SuppressWarnings("unchecked")
	public List<UserDTO> getUserListByBranch(Integer branchId) {
		Session session = getSessionFactory().openSession();
		List<UserDTO> result = null;
		try {
			String userSql = ""
					+ "SELECT u.id, "
					+ "       u.first_name AS firstName, "
					+ "       u.last_name  AS lastName "
					+ "FROM   core.branch AS b "
					+ "       JOIN core.user_branch AS ub "
					+ "         ON b.id = ub.branch_id "
					+ "       JOIN core.users u "
					+ "         ON u.id = ub.user_id "
					+ "WHERE  b.id = "+branchId+"";
			Query query = session.createSQLQuery(userSql).addScalar("id",StandardBasicTypes.INTEGER)
							.addScalar("firstName",StandardBasicTypes.STRING).addScalar("lastName",StandardBasicTypes.STRING)
						.setResultTransformer(new AliasToBeanResultTransformer(UserDTO.class));
			result = query.list();
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			session.close();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<RequisitionQueryDto> getRequistionDetailsById(Integer requisitionId) {
		Session session = getSessionFactory().openSession();
		List<RequisitionQueryDto> result = null;
		try {
			String rawSql = ""
					+ "SELECT req.requisition_id, "
					+ "       rd.qunatity     AS quantity, "
					+ "       p.\"name\"        AS product_name, "
					+ "       p.description, "
					+ "       p.selling_price AS buying_price, "
					+ "       req.\"date\" AS requisition_date "
					+ "FROM   core.requisition req "
					+ "       JOIN core.requisition_details rd "
					+ "         ON rd.requisition_id = req.id "
					+ "       JOIN core.product p "
					+ "         ON p.id = rd.product_id "
					+ "WHERE  req.id = "+requisitionId+" order by p.id";
			Query query = session.createSQLQuery(rawSql).addScalar("requisition_id",StandardBasicTypes.STRING)
							.addScalar("quantity",StandardBasicTypes.INTEGER)
							.addScalar("product_name",StandardBasicTypes.STRING)
							.addScalar("description",StandardBasicTypes.STRING)
							.addScalar("buying_price",StandardBasicTypes.FLOAT)
							.addScalar("requisition_date",StandardBasicTypes.STRING)
							.setResultTransformer(new AliasToBeanResultTransformer(RequisitionQueryDto.class));
			result = query.list();
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			session.close();
		}
		
		return result;
	}
}
