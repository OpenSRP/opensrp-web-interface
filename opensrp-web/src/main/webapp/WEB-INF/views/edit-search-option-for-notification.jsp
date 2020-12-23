<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:url var="location_url" value="/child-location-options" />
<c:url var="branch_url" value="/branch-list-options-inventoy" />
<div class="row">

   <c:choose>
	    <c:when test="${roleName =='AM' || roleName =='DivM'}">
	   	</c:when>
	   	
	   	<c:otherwise> 
		    <div class="col-lg-3 form-group">
		        <label ><spring:message code="lbl.division"></spring:message> </label>
		        <select	onclick="getChildLocation(this.value,'districtList')" name="division" class="form-control" id="divisionList">
		            <option value="0">Please select</option>
		            
		            <c:forEach items="${divisions}" var="division">
		            	<c:choose>
		            	<c:when test="${webNotification.getDivision() == division.id }">
		            		<option selected value="${division.id}">${division.name}</option>
		            	</c:when>
		            	<c:otherwise>
		                <option value="${division.id}">${division.name}</option>
		                </c:otherwise>
		                </c:choose>
		            </c:forEach>
		        </select>
		    </div>
		    <div class="col-lg-3 form-group">
		        <label ><spring:message code="lbl.district"></spring:message> </label>
		
		        <select	name="districtList" onclick="getChildLocation(this.value,'upazilaList')" id="districtList" class="form-control">
		            <option value="0">Please select </option>
		             <c:forEach items="${districts}" var="district">
		             
		             <c:choose>
		            	<c:when test="${webNotification.getDistrict() == district.id }">
		            		<option selected value="${district.id}">${district.name}</option>
		            	</c:when>
		            	<c:otherwise>
		                <option value="${district.id}">${district.name}</option>
		                </c:otherwise>
		                </c:choose>
		                
		            </c:forEach>
		
		        </select>
		    </div>
		    <div class="col-lg-3 form-group">
		        <label ><spring:message code="lbl.upazila"></spring:message> </label>
		        <select	name="upazilaList"  onclick="getBranchList(this.value, '')" id="upazilaList" class="form-control">
		            <option value="0">Please select </option>
		            <c:forEach items="${Upazilas}" var="upazila">
		            
		            	<c:choose>
			            	<c:when test="${webNotification.getUpazila() == upazila.id }">
			            		<option selected value="${upazila.id}">${upazila.name}</option>
			            	</c:when>
			            	<c:otherwise>
			               <option value="${upazila.id}">${upazila.name}</option>
			                </c:otherwise>
		                </c:choose>
		                
		            </c:forEach>
		
		        </select>
		    </div>
		    </c:otherwise> 
	    
    </c:choose> 
    <div class="col-lg-3 form-group">
        <label ><spring:message code="lbl.branch"></spring:message></label>
        <select	name="branchList" class="form-control" id="branchList">
		 	
            <c:forEach items="${branches}" var="branch">
                <option id="${branch.id}" class="${branch.id}" value="${branch.id}">${branch.name}</option>
            </c:forEach>
        </select>
    </div>

</div>

<script>
    function getChildLocation(locationId,divId) {
        $("#"+divId).html("");
        if(locationId==0){
        	locationId=-1;
        }
        if(divId=='districtList'){
            $("#districtList").html('<option value="0">Please select </option>');
            $("#upazilaList").html('<option value="0">Please select </option>');
            //$("#branchList").html('<option value="0">Please select </option>');
        }else if(divId=='upazilaList'){
            $("#upazilaList").html('<option value="0">Please select </option>');
            //$("#branchList").html('<option value="0">Please select </option>');
        }
        getBranchList(locationId, '');
        let url = '${location_url}';
        $.ajax({
            type : "GET",
            contentType : "application/json",
            url : url+"?id="+locationId,
            dataType : 'html',
            timeout : 100000,
            beforeSend: function() {},
            success : function(data) {
                $("#"+divId).html(data);
            },
            error : function(e) {
                console.log("ERROR: ", e);
                display(e);
            },
            done : function(e) {
                console.log("DONE");
            }
        });

    }


    function getBranchList(locationId,divId) {
        // $("#"+divId).html("");

        console.log("---------->>>>  getBranchList method is getting called");
        let url = '${branch_url}';
        $.ajax({
            type : "GET",
            contentType : "application/json",
            url : url+"?id="+locationId,

            dataType : 'html',
            timeout : 100000,
            beforeSend: function() {},
            success : function(data) {
                $("#branchList").html(data);
               $("#branchList > option").prop("selected","selected");
               $("#branchList").trigger("change");
            },
            error : function(e) {
                console.log("ERROR: ", e);
                display(e);
            },
            done : function(e) {

                console.log("DONE");
                //enableSearchButton(true);
            }
        });

    }


</script>