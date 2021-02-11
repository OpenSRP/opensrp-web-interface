<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
		   uri="http://www.springframework.org/security/tags"%>
<%@page import="org.opensrp.web.util.AuthenticationManagerUtil"%>




<title>Notification Details</title>
	
	
<jsp:include page="/WEB-INF/views/header.jsp" />
<link type="text/css" href="<c:url value="/resources/css/jquery.simple-dtpicker.css"/>" rel="stylesheet">

<c:url var="back" value="/web-notification/list.html" />/


<div class="page-content-wrapper">
		<div class="page-content">
			<ul class="page-breadcrumb breadcrumb">
				<li>
					<i class="fa fa-star" id="size_star" aria-hidden="true"></i> <span class="sub-menu-title"><strong>Web Notification</strong> </span>  <a  href="<c:url value="/"/>">Home</a>
					 
				</li>
				<li>
					 /  Web Notification list <b> / Details Web Notification </b> /  
				</li>
				<li>
					<a class="" href="${back}">Back</a>
					
				</li>
			
			</ul>
		<div class="portlet box blue-madison">
			<div class="portlet-title">
				<div class="center-caption">Web Notification Details</div>


			</div>
			
			<div class="portlet-body">
				<div class="form-group row">
						<label for="trainingTitle" class="col-sm-2 col-form-label">Notification Title:</label>
						<div class="col-sm-3">
						<label for="trainingTitle" class=" col-form-label">${notificationDetails.getTitle()}</label>
							
						</div>
					</div>
					<div class="form-group row">
						<label for="trainingTitle" class="col-sm-2 col-form-label">Notification Message:</label>
						<div class="col-sm-6">
						<label for="trainingTitle" class=" col-form-label">${notificationDetails.getNotification()}</label>
							
						</div>
					</div> 
					<div class="form-group row">
						<label for="trainingStartDate" class="col-sm-2 col-form-label">Notification Status :</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${notificationDetails.getStatus()}</label>
						</div>
					</div>
					<div class="form-group row">
						<label for="trainingStartDate" class="col-sm-2 col-form-label">Notification Type :</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${notificationDetails.getType()}</label>
						</div>
					</div>
					<div class="form-group row">
						<label for="trainingDuration" class="col-sm-2 col-form-label">Sending Date and Time :</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${notificationDetails.getCreatedTime()}</label>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="trainingDuration" class="col-sm-2 col-form-label">Date & time(Training or meeting) :</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${notificationDetails.getMeetingOrtrainingDateAndTime()}</label>
						</div>
					</div>
					<div class="form-group row">
						<label for="trainingDuration" class="col-sm-2 col-form-label">Receiver Role:</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${notificationDetails.getRoleName()}</label>
						</div>
					</div>
					<div class="form-group row">
						<label for="trainingDuration" class="col-sm-2 col-form-label">Branch Name</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${notificationDetails.getBranchName()} </label>
						</div>
					</div>
					
					<%-- <div class="form-group row">
						<label for="trainingAudience" class="col-sm-2 col-form-label">Division Name:</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${notificationDetails.getDivisionName()}</label>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="nameOfTrainer" class="col-sm-2 col-form-label">District Name :</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${notificationDetails.getDistrictName()}</label>
						</div>
					</div>
					<div class="form-group row">
						<label for="designationOfTrainer" class="col-sm-2 col-form-label">Upazilla Name:</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${notificationDetails.getUpazillaName()}</label>
						</div>
					</div> --%>
			</div>
		</div>
		
		<jsp:include page="/WEB-INF/views/footer.jsp" />
		</div>
	</div>
	<!-- END CONTENT -->
<%-- <jsp:include page="/WEB-INF/views/dataTablejs.jsp" />
 --%>
<script src="<c:url value='/resources/js/jquery.simple-dtpicker.js' />"></script>

<script>
jQuery(document).ready(function() {       
	 Metronic.init(); // init metronic core components
		Layout.init(); // init current layout
  
});

</script>




















