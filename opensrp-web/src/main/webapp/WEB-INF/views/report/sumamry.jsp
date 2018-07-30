<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Summary Report</title>
<link type="text/css"
	href="<c:url value="/resources/css/dataTables.jqueryui.min.css"/>" rel="stylesheet">
<jsp:include page="/WEB-INF/views/header.jsp" />

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
	<jsp:include page="/WEB-INF/views/navbar.jsp" />
	<div class="content-wrapper">
		<div class="container-fluid">

			<jsp:include page="/WEB-INF/views/report-search-panel.jsp" />
			<div id="loading" style="display: none;position: absolute; z-index: 1000;margin-left:45%"> 
							<img width="50px" height="50px" src="<c:url value="/resources/images/ajax-loading.gif"/>"></div>
							
			<div class="card mb-3">
				<div class="card-header">
					<i class="fa fa-table"></i> Summary Report
				</div>
				<div class="card-body">
					
						<div class="form-group">
							<div class="row">
								<div class="col-6">% of population we are reaching: </div>
								<div class="col-3"> 70%</div>
							
							</div>
							
							<div class="row">
								<div class="col-6">% of pregnant women counseled: </div>
								<div class="col-3"> 30%</div>
							
							</div>
							<div class="row">
								<div class="col-6">% of children under six months who are exclusively breastfeed : </div>
								<div class="col-3"> 40%</div>
							
							</div>
							
							<div class="row">
								<div class="col-6">% timely introduction of complementary food : </div>
								<div class="col-3"> 50%</div>
							
							</div>
							<div class="row">
								<div class="col-6">% of children who are growth faltering : </div>
								<div class="col-3"> 50%</div>
							
							</div>
							<div class="row">
								<div class="col-6">% of children growth faltering for 2 months consecutively : </div>
								<div class="col-3"> 45%</div>
							
							</div>
							
							<div class="row">
								<div class="col-6">% of women with an underweight child committed to a small doable action before nenxt appointment : </div>
								<div class="col-3"> 25%</div>							
							</div>
							<div class="row">
								<div class="col-6">% of severely underweight children 6-23 months : </div>
								<div class="col-3"> 25%</div>							
							</div>
					</div>
				
				<div class="card-footer small text-muted"></div>
			</div>
		</div>

		<jsp:include page="/WEB-INF/views/footer.jsp" />
		
		<script src="<c:url value='/resources/js/jquery-ui.js' />"></script>
		
		
		<script src="<c:url value='/resources/js/datepicker.js'/>"></script>
		
		<script type="text/javascript">
		
		
		$("#search-form").submit(function(event) { 
			$("#loading").show();
			 
			var division = "";
			var district = "";
			var upazila = "";
			var union = "";
			var ward = "";
			var subunit = "";
			var mauzapara = "";
			var params = "" ;
			
			division = $('#division').val();
			district = $('#district').val();
			upazila = $('#upazila').val();
			union = $('#union').val();
			ward = $('#ward').val();
			subunit = $('#subunit').val();
			mauzapara = $('#mauzapara').val();
			if(division != "" && division != "0?" && division != null ){
				params ="?division="+division;
			}
			if(district != "0?" &&  district != "" && district != null){
				params +="&district="+district;
				
			}
			if(upazila != "0?" && upazila != "" && upazila != null){
				params +="&upazila="+upazila;
			}
			if(union != "0?" && union != "" && union != null){
				params +="&union="+union;
				console.log(union);
			}
			if(ward != "0?" && ward != "" && ward != null){
				params +="&ward="+ward;
			}
			if(subunit != "0?" && subunit != "" && subunit != null){
				params +="&subunit="+subunit;
			}
			if(mauzapara != "0?" && mauzapara != "" && mauzapara != null){
				params +="&mauzapara="+mauzapara;
			}
			console.log(params);
			event.preventDefault();
			$.ajax({
				type : "GET",
				contentType : "application/json",				
				url : "/opensrp-dashboard/report/child-growth-ajax.html"+params,				 
				dataType : 'html',
				timeout : 100000,
				beforeSend: function() {
				    
				   
				},
				success : function(data) {	
					$("#loading").hide();
				   $("#tableBody").html(data);
				},
				error : function(e) {
				    console.log("ERROR: ", e);
				    display(e);
				},
				done : function(e) {				    
				    console.log("DONE");				    
				}
			});
		});		
		 
		</script>
	</div>
</body>
</html>