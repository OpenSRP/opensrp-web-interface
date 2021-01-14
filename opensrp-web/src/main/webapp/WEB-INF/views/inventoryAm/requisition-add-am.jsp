<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
		   uri="http://www.springframework.org/security/tags"%>
<%@page import="org.opensrp.web.util.AuthenticationManagerUtil"%>

<title>Add Requisition</title>
	
	
<c:url var="backUrl" value="/inventoryam/requisition-list/${branchInfo[0][0]}.html" />
<c:url var="save_url" value="/rest/api/v1/requisition/save-update" />


<jsp:include page="/WEB-INF/views/header.jsp" />
<jsp:include page="/WEB-INF/views/dataTablecss.jsp" />
	

<div class="page-content-wrapper">
		<div class="page-content">
		<ul class="page-breadcrumb breadcrumb">
				<li>
					<i class="fa fa-star" id="size_star" aria-hidden="true"></i> <span class="sub-menu-title"><strong>Requisition</strong> </span>  <a  href="<c:url value="/"/>">Home</a>
					 
				</li>
				<li>
					/ Inventory  / Requisition list / <b>Add Requisition </b> / 
				</li>
				<li>
					<a  href="${backUrl }">Back</a>
				</li>
		</ul>
		<div class="portlet box blue-madison">
					<div class="portlet-title">
						<div class="center-caption">
							${branchInfo[0][1]} - <span id="branchCode">${branchInfo[0][2]} </span>
						</div>
						<p style="display: none;" id="branchId">${branchInfo[0][0]}</p>
						
					</div>

					<div class="portlet-body">
				<div style="display: none;" class="alert alert-success" id="serverResponseMessage" role="alert"></div>
				<div class="card-body">
					<div id="loading"
						style="display: none; position: absolute; z-index: 1000; margin-left: 35%">
						<img width="50px" height="50px"
							src="<c:url value="/resources/images/ajax-loading.gif"/>">
					</div>

				</div>
				<table class="table table-striped table-bordered" id="requisitionAddList">
							<thead>
								<tr>
									<th><spring:message code="lbl.serialNo"></spring:message></th>
									<th><spring:message code="lbl.productName"></spring:message></th>
									<th><spring:message code="lbl.currentStock"></spring:message></th>
									<th><spring:message code="lbl.requisitionAmount"></spring:message></th>
								</tr>
							</thead>
							<tbody>
							<c:forEach var="product" items="${ productList }">
									<tr>
										<td>${ product.id }</td>
										<td>${ product.name }</td>
										<td>${ product.stock }</td>
										<td><input type="number"  min="1" oninput="this.value = Math.abs(this.value)" id="requisitionAmount" name ="requisitionAmount"><span class="text-danger" id="amountSelection"></span></td>
									</tr>
							</c:forEach>
								</tbody>
						</table>
						<div class=row>
							<div class="col-md-12 form-group text-right">
						    		<div class="row">
								     	<div class="col-lg-12 ">
								     	 <a class="btn btn-primary" href="${backUrl}">Cancel</a>
											 <button  onclick="submitRequisition()" class="btn btn-primary" value="confirm">Confirm All</button>
										</div>
						            </div>
						      </div>
				
						</div>
					</div>
				</div>
				<!-- <div class="col-lg-12 form-group text-right">
				<button onclick="submitRequisition()" class="btn btn-primary" value="confirm">Confirm All</button>
			
	            </div> -->
		</br>
		<jsp:include page="/WEB-INF/views/footer.jsp" />
		</div>
	</div>
	<!-- END CONTENT -->
<jsp:include page="/WEB-INF/views/dataTablejs.jsp" />

<script src="<c:url value='/resources/assets/admin/js/table-advanced.js'/>"></script>

<script>
var requisitionTable;
jQuery(document).ready(function() {       
	 Metronic.init(); // init metronic core components
		Layout.init(); // init current layout
   //TableAdvanced.init();
		requisitionTable = $('#requisitionAddList').DataTable({
			  "pageLength": 25
		});
});

/* $('.identifier').change(function() {
	var $row = $(this).closest("tr");
	var quantity = +$(this).val();
	if(quantity < 1) {
		$(this).val('');
		$row.find('span:first').html("<strong>* Quantity Can not be less than 1</strong>");
	}
	else $row.find('span:first').html("");
}); */

function mapRowData() {
	var requisitionDetails = [];
	 /* var data = requisitionTable.row( element.parentNode ).data();
	 data.each(function (value, index) {
		 var productObject = {};
		 productObject["productId"] = parseInt(value[0]);
		 productObject["currentStock"] = parseInt(value[2]);

		 var quantity = requisitionTable.cell(index,3).nodes().to$().find('input').val();
		 var cell = requisitionTable.cell({ row: index, column: 3 }).node();
		 var vaueTest = $('input', cell).val()
		 productObject["qunatity"] = parseInt(quantity);
		 if(!isNaN(productObject["qunatity"])) {
			 requisitionDetails.push(productObject);
		 }
	 }); */
	 $('#requisitionAddList > tbody > tr').each(function (index, tr) {
		    
			var productObject = {};
		    //get td of each row and insert it into cols array
		    $(this).find('td').each(function (colIndex, row) {
		    	if(colIndex == 0) {
		    		productObject['productId'] = parseInt(row.textContent);
		    	}
		    	if(colIndex == 2) {
		    		productObject['currentStock'] = parseInt(row.textContent);
		    	}
		    	if(colIndex == 3) {
		    	 $(this).find('input').each(function() {
		    		 	if(parseInt($(this).val()) == 0) {
		    		 		$(this).val('');
		    		 	}
		    		     productObject['qunatity'] = parseInt($(this).val());
		    		   })
		    	}
		    });
		    if(!isNaN(productObject["qunatity"])) {
		    	 if(productObject["qunatity"] > 0) {
		    		 requisitionDetails.push(productObject);
		    	 }
			 }
		  }); 
		  
	 
	 return requisitionDetails;
}
	/* $('#requisitionAddList > tbody > tr').each(function (index, tr) {
	    
		var productObject = {};
	    //get td of each row and insert it into cols array
	    $(this).find('td').each(function (colIndex, row) {
	    	if(colIndex == 0) {
	    		productObject['productId'] = parseInt(row.textContent);
	    	}
	    	if(colIndex == 2) {
	    		productObject['currentStock'] = parseInt(row.textContent);
	    	}
	    	if(colIndex == 3) {
	    	 $(this).find('input').each(function() {
	    		     productObject['qunatity'] = +$(this).val();
	    		   })
	    	}
	    });
	    requisitionDetails.push(productObject);
	  });  */
	  
	  function submitRequisition() { 
			var requisionDetailsArray = mapRowData();
			if(requisionDetailsArray.length < 1) {
				 $("#amountSelection").html("<strong>* Atleast one field need to be selected</strong>");
				 $(window).scrollTop(0);
				 return;
			}
			 $("#loading").show();
			 $("#amountSelection").html("");
			var requisitionId = $("#branchCode").text().trim();
			var branchId = +$("#branchId").text();
			var url = '${save_url}';			
			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");
			var formData;
				formData = {
			            'branchId': branchId,
			            'status': "ACTIVE",
			            'id': 0,
			            'requisitionId': requisitionId,
			            'requisitionDetails': requisionDetailsArray
			        };
			console.log(formData)
			$(window).scrollTop(0);
			event.preventDefault();
			$.ajax({
				contentType : "application/json",
				type: "POST",
		        url: url,
		        data: JSON.stringify(formData), 
		        dataType : 'json',
		        
		        timeout : 300000,
				beforeSend: function(xhr) {				    
					 xhr.setRequestHeader(header, token);
				},
				success : function(data) {
				   var response = JSON.parse(data);
				   $("#loading").hide();
				   $("#serverResponseMessage").show();
				   $("#serverResponseMessage").html(response.msg);
				   
 				   if(response.status == "SUCCESS"){
 		            	setTimeout(function(){
 		            		window.location.replace("${backUrl}");
 		                 }, 1000);
				   }
				   
				},
				error : function(e) {
				   
				},
				done : function(e) {				    
				    console.log("DONE");				    
				}
			});
		};	
</script>



















