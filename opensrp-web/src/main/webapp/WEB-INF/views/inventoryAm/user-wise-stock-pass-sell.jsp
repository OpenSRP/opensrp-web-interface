<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
		   uri="http://www.springframework.org/security/tags"%>
<%@page import="org.opensrp.web.util.AuthenticationManagerUtil"%>

<title>Stock in Details</title>
	
	
<c:url var="stock_in_list" value="/rest/api/v1/stock/pass-stock-or-sell-to-ss-list" />


<jsp:include page="/WEB-INF/views/header.jsp" />
<jsp:include page="/WEB-INF/views/dataTablecss.jsp" />
	
<link type="text/css"
	href="<c:url value="/resources/css/daterangepicker.css"/>"
	rel="stylesheet"
	http-equiv="Cache-control" content="public">
<div class="page-content-wrapper">
		<div class="page-content">
		<div class="portlet box blue-madison">
					<div class="portlet-title">
						<div class=center-caption>${titleType} details of ${user.getFullName() } of branch ${user.getBranch() }</div>
						</div>
			<div class="portlet-body">
			
				<div class="form-group">
			<div class="row">								 	
				<div class="col-lg-4 form-group">
			 		<label class="control-label">Date range </label> 
		            <input  type="text" id="dateRange" name="dateRange" class="form-control"/>
		                         
				</div>
				<div class="col-lg-4 form-group ">
					<label class="control-label">Product name </label> 
				 		<input class="form-control" type="text" id="productName" placeholder="Product name"> 
				</div>
				<div class="col-lg-4" style="padding-top: 20px">
				
					<div  onclick="filter()"  class="btn btn-primary btn-lg">Search</div>
				</div>
			
			</div>
		</div>
				<table class="table table-striped table-bordered"
					id="stock_pass_sell">
					<thead>
						<tr>
							<th>Product name</th>							
							<th>Receive date</th>							
							<th>Quantity</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
		</br>
		<jsp:include page="/WEB-INF/views/footer.jsp" />
		</div>
	</div>
	<!-- END CONTENT -->
<jsp:include page="/WEB-INF/views/dataTablejs.jsp" />

<script src="<c:url value='/resources/assets/admin/js/table-advanced.js'/>"></script>
<script src="<c:url value='/resources/js/moment.min.js' />"></script>
 
<script src="<c:url value='/resources/js/daterangepicker.min.js' />"></script>
<script>
jQuery(document).ready(function() {       
	 Metronic.init(); // init metronic core components
		Layout.init(); // init current layout
   //TableAdvanced.init();
		//$('#requisitionDetails').DataTable();
});
</script>

<script type="text/javascript">
var dateToday = new Date();
$(function() {

  $('input[name="dateRange"]').daterangepicker({
      autoUpdateInput: false,
      maxDate: dateToday,
      locale: {
          cancelLabel: 'Clear'
      }
  });

  $('input[name="dateRange"]').on('apply.daterangepicker', function(ev, picker) {
      $(this).val(picker.startDate.format('YYYY-MM-DD') + ' - ' + picker.endDate.format('YYYY-MM-DD'));
  });

  $('input[name="dateRange"]').on('cancel.daterangepicker', function(ev, picker) {
      $(this).val('');
  });

});
</script>
<script>

    let stockList;
    $(document).ready(function() {
    	stockList = $('#stock_pass_sell').DataTable({
            bFilter: false,
            serverSide: true,
            processing: true,
            columnDefs: [
                { targets: [0, 1, 2], orderable: false },
                { width: "20%", targets: 0 },
                { width: "20%", targets: 1 },
                { width: "20%", targets: 2 }
            ],
            ajax: {
                url: "${stock_in_list}",
                data: function(data){
                	data.branchId =${branchId};
                    data.userId=${userId};
                    data.type=${type};
                    data.startDate = '';
                    data.endDate ='';
                    data.productName = '';
                    
                },
                dataSrc: function(json){
                    if(json.data){
                        return json.data;
                    }
                    else {
                        return [];
                    }
                },
                complete: function() {
                },
                type: 'GET'
            },
            bInfo: true,
            destroy: true,
            language: {
                searchPlaceholder: ""
            }
        });
    });

function filter(){
	
	stockList = $('#stock_pass_sell').DataTable({
         bFilter: false,
         serverSide: true,
         processing: true,
         columnDefs: [
             { targets: [0, 1, 2], orderable: false },
             { width: "20%", targets: 0 },
             { width: "20%", targets: 1 },
             { width: "20%", targets: 2 }
         ],
         ajax: {
             url: "${stock_in_list}",
             data: function(data){
            	 let dateFieldvalue = $("#dateRange").val();  
            	 if(dateFieldvalue != '' && dateFieldvalue != undefined){
 	     	        data.startDate = $("#dateRange").data('daterangepicker').startDate.format('YYYY-MM-DD');
 	                data.endDate =$("#dateRange").data('daterangepicker').endDate.format('YYYY-MM-DD');
             	}else{
             		data.startDate = '';
                    data.endDate ='';
             	}
            	 data.branchId =${branchId};
                 data.userId=${userId};
                 data.type=${type};
                 data.productName = $("#productName").val();
             },
             dataSrc: function(json){
                 if(json.data){
                     return json.data;
                 }
                 else {
                     return [];
                 }
             },
             complete: function() {
             },
             type: 'GET'
         },
         bInfo: true,
         destroy: true,
         language: {
             searchPlaceholder: ""
         }
     });
}
</script>


















