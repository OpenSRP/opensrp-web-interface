<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
		   uri="http://www.springframework.org/security/tags"%>
<%@page import="org.opensrp.web.util.AuthenticationManagerUtil"%>

<title>Training Details</title>
	
	


<jsp:include page="/WEB-INF/views/header.jsp" />
<jsp:include page="/WEB-INF/views/dataTablecss.jsp" />
<c:url var="back_url" value="/training/training-list.html?lang=en" />


<div class="page-content-wrapper">
		<div class="page-content">
		<ul class="page-breadcrumb breadcrumb">
				<li>
					<a class="btn btn-primary" href="<c:url value="/"/>">Home</a>
					<i class="fa fa-arrow-right"></i>
				</li>
				<li>
					<a class="btn btn-primary" href="${back_url }">Training List</a>
					
				</li>
			
			</ul>
		<div class="portlet box blue-madison">
					<div class="portlet-title">
						<div class=center-caption>Training - ${trainingObj.getTitle()}</div>
						</div>
			<div class="portlet-body">
				<div class="form-group row">
						<label for="trainingTitle" class="col-sm-2 col-form-label"><spring:message code="lbl.trainingTitle"></spring:message>:</label>
						<div class="col-sm-3">
						<label for="trainingTitle" class=" col-form-label">${trainingObj.getTitle()}</label>
							
						</div>
					</div>
					<div class="form-group row">
						<label for="trainingTitle" class="col-sm-2 col-form-label"><spring:message code="lbl.location"></spring:message>:</label>
						<div class="col-sm-6">
						<label for="trainingTitle" class=" col-form-label">${trainingObj.getLocationName()}</label>
							
						</div>
					</div>
					<div class="form-group row">
						<label for="trainingStartDate" class="col-sm-2 col-form-label"><spring:message code="lbl.trainingStartDate"></spring:message> :</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${trainingObj.getStartDate()}</label>
						</div>
					</div>
					<div class="form-group row">
						<label for="trainingDuration" class="col-sm-2 col-form-label"><spring:message code="lbl.trainingduration"></spring:message> :</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${trainingObj.getDuration()} Days</label>
						</div>
					</div>
					<div class="form-group row">
						<label for="trainingDuration" class="col-sm-2 col-form-label">Total Participated :</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${trainingObj.getParticipantNumber()} </label>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="trainingAudience" class="col-sm-2 col-form-label"><spring:message code="lbl.trainingAudience"></spring:message> :</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${trainingObj.getAudience()}</label>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="nameOfTrainer" class="col-sm-2 col-form-label"><spring:message code="lbl.nameOfTrainer"></spring:message> :</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${trainingObj.getNameOfTrainer()}</label>
						</div>
					</div>
					<div class="form-group row">
						<label for="designationOfTrainer" class="col-sm-2 col-form-label"><spring:message code="lbl.designationOfTrainer"></spring:message>:</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${trainingObj.getDesignationOfTrainer()}</label>
						</div>
					</div>
					<div class="form-group row">
						<label for="designationOfTrainer" class="col-sm-2 col-form-label">Location Description</label>
						<div class="col-sm-6">
							<label for="trainingTitle" class=" col-form-label">${trainingObj.getDescription()}</label>
						</div>
					</div>
					
					 <div class="row" style="margin: 0px">
		                <div class="col-sm-12" id="content" style="overflow-x: auto;">
		                 <h3>Attenders</h3>
						<table class="display table table-bordered table-striped" id="reportDataTable">
						    <thead>
								    <tr>
								        <th>Name </th>
								         <th>Mobile </th>			        
								        <th>Role  </th>	
								        <th>Branch  </th>	    
								    </tr>
						    </thead>
						   
						    <tbody id="t-body">
						    	
						   		<c:forEach items="${users}" var="user"> 
						   			<tr>
						   				<td> ${user.getFullName() }</td>	
								   		<td> ${user.getMobile() }</td>	   			
								   		<td> ${user.getRoles() }</td>	
								   		<c:choose>	
								   			<c:when test="${user.getRoles()=='DivM' }">
								   			<td> </td>
								   			</c:when>
								   			<c:otherwise>
								   				<td>${user.getBranches() }</td>
								   			</c:otherwise>						   			
								   			
								   		
								   		</c:choose>
								   			
							 		</tr>
								</c:forEach>
						    </tbody>
						</table>
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

<script>
jQuery(document).ready(function() {       
	 Metronic.init(); // init metronic core components
		Layout.init(); // init current layout
   //TableAdvanced.init();
		 $('#reportDataTable').DataTable({            	
         	
         });
});
</script>



















