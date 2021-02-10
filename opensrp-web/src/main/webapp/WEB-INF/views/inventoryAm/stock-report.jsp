<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
		   uri="http://www.springframework.org/security/tags"%>
<%@page import="org.opensrp.web.util.AuthenticationManagerUtil"%>
<%@ page import="org.opensrp.core.entity.Branch" %>
<%@ page import="java.util.List" %>


<style>
	.select2-results__option .wrap:before {
		font-family: fontAwesome;
		color: #999;
		content: "\f096";
		width: 25px;
		height: 25px;
		padding-right: 10px;
	}

	.select2-results__option[aria-selected=true] .wrap:before {
		content: "\f14a";
	}


	/* not required css */

	.row {
		padding: 10px;
	}

	.select2-multiple,
	.select2-multiple2 {
		width: 50%
	}

	.select2-results__group .wrap:before {
		display: none;
	}

</style>
<title>Stock Report</title>
	
	


<jsp:include page="/WEB-INF/views/header.jsp" />
<jsp:include page="/WEB-INF/views/dataTablecss.jsp" />
	

<div class="page-content-wrapper">
		<div class="page-content">
		<ul class="page-breadcrumb breadcrumb">
				<li>
					<i class="fa fa-star" id="size_star" aria-hidden="true"></i> <span class="sub-menu-title"><strong>Stock report </strong> </span>  <a  href="<c:url value="/"/>">Home</a>
					 
				</li>				
				
				<li>
					/ Inventory  
				</li>
				
		</ul>
		<div class="row">
			<div class="col-md-12">

				<!-- BEGIN EXAMPLE TABLE PORTLET-->
				<div class="portlet box blue-madison">
					<div class="portlet-title">
						
					</div>					
					<div class="portlet-body">
						<div class="form-group">
							<div class="row">
								<div class="col-lg-3 form-group">
								    <label for="branchList">Branch :</label>
								    <select
										name="branch" class="form-control" id="branchList" >

										<%
											List<Branch> ret = (List<Branch>) session.getAttribute("branchList");
											for (Branch str : ret) {
										%>
										<option value="<%=str.getId()%>"><%=str.getName()%></option>
										<%}%>
									</select>
								</div>

								<div class="col-lg-3 form-group">
									<label >Month & Year:</label>
									<input type="text"
										   class="form-control date-picker-year" id="monthlyDate">
								</div>
								<div class="col-lg-3 form-group">
									<label> Designation:</label>
									<select class="form-control" id="designation">
										<option value="SK"> SK</option>
										<option value="PA"> PA </option>
									</select>
								</div>
								<div class="col-sm-3" style="margin-top: 24px;">
									<button type="submit" onclick="getStockReport()" class="btn btn-primary" value="confirm">Submit</button>
									<span id="validationMessage"></span>
								</div>
								
							</div>
							<br/>
						<h3>Stock Report : </h3>
						<div id="stockReportTable">
						</div>
					</div>
					
				</div>		
					
			</div>
		</div>
		</br>
		<jsp:include page="/WEB-INF/views/footer.jsp" />
		</div>
	</div>
	<!-- END CONTENT -->
<jsp:include page="/WEB-INF/views/dataTablejs.jsp" />

<script src="<c:url value='/resources/assets/admin/js/table-advanced.js'/>"></script>
<script src="<c:url value='/resources/js/dataTables.fixedColumns.min.js'/>"></script>
<script src="<c:url value='/resources/assets/global/js/select2-multicheckbox.js'/>"></script>
<script>


	$(function(){
		$('.date-picker-year').datepicker({
			changeMonth: true,
			changeYear: true,
			showButtonPanel: true,
			dateFormat: 'yy-mm',
			maxDate: new Date,

			onClose: function(dateText, inst) {
				var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
				$('#monthlyDate').datepicker('setDate', new Date(inst.selectedYear, inst.selectedMonth, 1));
				$(".date-picker-year").focus(function () {
					$(".ui-datepicker-calendar").hide();
					$(".ui-datepicker-current").hide();
				});

			}
		});

		$(".date-picker-year").focus(function () {
			$(".ui-datepicker-calendar").hide();
			$(".ui-datepicker-current").hide();
		});
	});



	jQuery(document).ready(function() {
	 Metronic.init(); // init metronic core components
		Layout.init(); // init current layout

	$('#branchList').select2MultiCheckboxes({
		placeholder: "Select branch",
		width: "auto",
		templateSelection: function(selected, total) {
			return "Selected " + selected.length + " of " + total;
		}
	});

});

function getStockReport() {

	if($('#monthlyDate').val() == "") {
		$('#validationMessage').html("please select a month");
		return;
	}


	$('#validationMessage').html("");
	var url = "/opensrp-dashboard/inventoryam/stock-report-table";
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url : url,
		dataType : 'html',
		timeout : 300000,
		data: {
			branchIds: ($('#branchList').val() || []).join(','),
			month: new Date($('#monthlyDate').val()).getMonth() + 1,
			year: new Date($('#monthlyDate').val()).getFullYear(),
			userRole: $('#designation').val(),
		},
		beforeSend: function() {
			$('#loading').show();
			$('#search-button').attr("disabled", true);
		},
		success : function(data) {
			$("#stockReportTable").html(data);
			$('#stockReport').DataTable({
				scrollY:        "300px",
				scrollX:        true,
				scrollCollapse: true,
				fixedColumns:   {
					leftColumns: 1
				}
			});


		},
		error : function(e) {
			$('#loading').hide();
			$('#search-button').attr("disabled", false);
		},
		complete : function(e) {
			$('#loading').hide();
			$('#search-button').attr("disabled", false);
		}
	});

}

</script>



















