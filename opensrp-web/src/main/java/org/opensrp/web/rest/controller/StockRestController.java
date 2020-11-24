package org.opensrp.web.rest.controller;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.common.dto.InventoryDTO;
import org.opensrp.common.util.Roles;
import org.opensrp.common.util.UserColumn;
import org.opensrp.core.dto.StockAdjustDTO;
import org.opensrp.core.dto.StockDTO;
import org.opensrp.core.entity.Role;
import org.opensrp.core.entity.User;
import org.opensrp.core.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RequestMapping("rest/api/v1/stock")
@RestController
public class StockRestController {
	
	@Autowired
	private StockService stockService;
	
	@RequestMapping(value = "/save-update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> newPatient(@RequestBody StockDTO dto) throws Exception {
		
		JSONObject response = new JSONObject();
		
		try {
			
			Integer isSave = stockService.saveAll(dto);
			if (isSave != null) {
				response.put("status", "SUCCESS");
				response.put("msg", "you have created successfully");
			} else {
				response.put("status", "FAILED");
				response.put("msg", "Something went worng please contact with admin.");
			}
			
			return new ResponseEntity<>(new Gson().toJson(response.toString()), OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(new Gson().toJson(response.toString()), OK);
		}
		
	}
	
	@RequestMapping(value = "/stock-adjust-save-update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveAdjustStockDetails(@RequestBody StockAdjustDTO dto) throws Exception {
		
		JSONObject response = new JSONObject();
		try {
			
			Integer isSave = stockService.saveAdjustDetails(dto);
			if (isSave != null) {
				response.put("status", "SUCCESS");
				response.put("msg", "you have created successfully");
			} else {
				response.put("status", "FAILED");
				response.put("msg", "Something went worng please contact with admin.");
			}
			
			return new ResponseEntity<>(new Gson().toJson(response.toString()), OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(new Gson().toJson(response.toString()), OK);
		}
		
	}
	
	@RequestMapping(value = "/in-list", method = RequestMethod.GET)
	public ResponseEntity<String> allStockInList(HttpServletRequest request) throws JSONException {
		Integer start = Integer.valueOf(request.getParameter("start"));
		Integer length = Integer.valueOf(request.getParameter("length"));
		//String name = request.getParameter("search[value]");
		Integer draw = Integer.valueOf(request.getParameter("draw"));
		String orderColumn = request.getParameter("order[0][column]");
		String orderDirection = request.getParameter("order[0][dir]");
		orderColumn = UserColumn.valueOf("_" + orderColumn).getValue();
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String name = request.getParameter("search");
		int branchId = Integer.parseInt(request.getParameter("branchId"));
		String invoiceNumber = request.getParameter("invoiceNumber");
		String stockInId = request.getParameter("stockInId");
		int division = Integer.parseInt(request.getParameter("division"));
		int district = Integer.parseInt(request.getParameter("district"));
		int upazila = Integer.parseInt(request.getParameter("upazila"));
		int userId = Integer.parseInt(request.getParameter("userId"));
		
		List<InventoryDTO> stockInList = stockService.getStockInList(branchId, startDate, endDate, invoiceNumber, stockInId,
		    division, district, upazila, userId, length, start, orderColumn, orderDirection);
		
		int stockInListCount = stockService.getStockInListCount(branchId, startDate, endDate, invoiceNumber, stockInId,
		    division, district, upazila, userId);
		
		JSONObject response = stockService.getStockInListDataOfDataTable(draw, stockInListCount, stockInList);
		return new ResponseEntity<>(response.toString(), OK);
	}
	
	@RequestMapping(value = "/pass-user-list", method = RequestMethod.GET)
	public ResponseEntity<String> allStockPass(HttpServletRequest request) throws JSONException {
		Integer start = Integer.valueOf(request.getParameter("start"));
		Integer length = Integer.valueOf(request.getParameter("length"));
		//String name = request.getParameter("search[value]");
		Integer draw = Integer.valueOf(request.getParameter("draw"));
		String orderColumn = request.getParameter("order[0][column]");
		String orderDirection = request.getParameter("order[0][dir]");
		orderColumn = UserColumn.valueOf("_" + orderColumn).getValue();
		
		String search = request.getParameter("search");
		int branchId = Integer.parseInt(request.getParameter("branchId"));
		String name = request.getParameter("name");
		if (!StringUtils.isBlank(name)) {
			name = "%" + name + "%";
		}
		int roleId = Integer.parseInt(request.getParameter("roleId"));
		if (roleId == 0 && StringUtils.isBlank(name)) {
			roleId = Roles.SK.getId();
		}
		List<InventoryDTO> stockPassUserList = stockService.getPassStockUserList(branchId, name, roleId, length, start,
		    orderColumn, orderDirection);
		
		int stockPassUserListCount = stockService.getPassStockUserListCount(branchId, roleId, name);
		
		JSONObject response = stockService.getPassStockUserListDataOfDataTable(draw, stockPassUserListCount,
		    stockPassUserList, branchId);
		return new ResponseEntity<>(response.toString(), OK);
	}
	
	@RequestMapping(value = "/sell_to_ss_list", method = RequestMethod.GET)
	public ResponseEntity<String> sellToSSlist(HttpServletRequest request, HttpSession session) throws JSONException {
		Integer start = Integer.valueOf(request.getParameter("start"));
		Integer length = Integer.valueOf(request.getParameter("length"));
		//String name = request.getParameter("search[value]");
		Integer draw = Integer.valueOf(request.getParameter("draw"));
		String orderColumn = request.getParameter("order[0][column]");
		String orderDirection = request.getParameter("order[0][dir]");
		orderColumn = UserColumn.valueOf("_" + orderColumn).getValue();
		
		String name = request.getParameter("search");
		int branchId = Integer.parseInt(request.getParameter("branchId"));
		
		int division = Integer.parseInt(request.getParameter("division"));
		int district = Integer.parseInt(request.getParameter("district"));
		int upazila = Integer.parseInt(request.getParameter("upazila"));
		int skId = Integer.parseInt(request.getParameter("skId"));
		int year = Integer.parseInt(request.getParameter("year"));
		int month = Integer.parseInt(request.getParameter("month"));
		int managerId = Integer.parseInt(request.getParameter("manager"));
		
		List<InventoryDTO> stockInList = stockService.getsellToSSList(managerId, branchId, skId, division, district,
		    upazila, year, month, length, start, orderColumn, orderDirection);
		
		int stockInListCount = stockService.getsellToSSListCount(managerId, branchId, skId, division, district, upazila,
		    year, month);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		
		Set<Role> roles = user.getRoles();
		
		int roleId = 0;
		for (Role role : roles) {
			roleId = role.getId();
		}
		JSONObject response = stockService.getSellToSSListDataOfDataTable(draw, stockInListCount, stockInList, roleId,
		    branchId);
		return new ResponseEntity<>(response.toString(), OK);
	}
	
	@RequestMapping(value = "/pass-stock-or-sell-to-ss-list", method = RequestMethod.GET)
	public ResponseEntity<String> allPassOrSellToSSList(HttpServletRequest request) throws JSONException {
		Integer start = Integer.valueOf(request.getParameter("start"));
		Integer length = Integer.valueOf(request.getParameter("length"));
		//String name = request.getParameter("search[value]");
		Integer draw = Integer.valueOf(request.getParameter("draw"));
		String orderColumn = request.getParameter("order[0][column]");
		String orderDirection = request.getParameter("order[0][dir]");
		orderColumn = UserColumn.valueOf("_" + orderColumn).getValue();
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String search = request.getParameter("search");
		int branchId = Integer.parseInt(request.getParameter("branchId"));
		String productName = request.getParameter("productName");
		if (!StringUtils.isBlank(productName)) {
			productName = "%" + productName + "%";
		}
		String type = request.getParameter("type");
		int userId = Integer.parseInt(request.getParameter("userId"));
		
		List<InventoryDTO> stockPassUserList = stockService.getStockPassOrSellToSSByUserId(branchId, userId, type,
		    productName, startDate, endDate, length, start);
		
		int stockPassUserListCount = stockService.getStockPassOrSellToSSByUserIdCount(branchId, userId, type, productName,
		    startDate, endDate);
		
		JSONObject response = stockService.getStockPassorSellToSSListDataOfDataTable(draw, stockPassUserListCount,
		    stockPassUserList);
		return new ResponseEntity<>(response.toString(), OK);
	}
	
	@RequestMapping(value = "/stock-adjust-history-list", method = RequestMethod.GET)
	public ResponseEntity<String> getStockAdjustHistoryList(HttpServletRequest request) throws JSONException {
		Integer start = Integer.valueOf(request.getParameter("start"));
		Integer length = Integer.valueOf(request.getParameter("length"));
		//String name = request.getParameter("search[value]");
		Integer draw = Integer.valueOf(request.getParameter("draw"));
		String orderColumn = request.getParameter("order[0][column]");
		String orderDirection = request.getParameter("order[0][dir]");
		orderColumn = UserColumn.valueOf("_" + orderColumn).getValue();
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String search = request.getParameter("search");
		int branchId = Integer.parseInt(request.getParameter("branchId"));
		
		List<StockAdjustDTO> stockAdjustList = stockService.getAdjustHistoryList(0, branchId, startDate, endDate, start,
		    length);
		
		int stockAdjustListCount = stockService.getAdjustStockListCount(branchId, startDate, endDate);
		
		JSONObject response = stockService.getAdjustStockListDataOfDataTable(draw, stockAdjustListCount, stockAdjustList);
		return new ResponseEntity<>(response.toString(), OK);
	}
}
