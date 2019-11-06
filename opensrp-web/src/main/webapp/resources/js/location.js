
function getLocationHierarchy(url, id) {
	$("#"+id).html("");
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url : url,

		dataType : 'html',
		timeout : 100000,
		beforeSend: function() {},
		success : function(data) {
			$("#"+id).html(data);
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

jQuery(document).ready(function($) {
	$("#division").change(function(event) {
		var division = $("#division").val();
		if (division != '' && division != null && division != -1 && division != undefined && division != "0?") {
			getLocationHierarchy("/opensrp-dashboard/location?id="+$("#division").val().split("?")[0]+"&title=","district") ;
		} else {
			$("#district").html("");
			$("#district").append("<option value='0?'>Select District</option>");
		}
		$("#upazila").html("");
		$("#upazila").append("<option value='0?'>Select Upazila/City Corporation</option>");
		$("#pourasabha").html("");
		var division = $("#division").val().split("?")[1];
		$("#address_field").val(division == undefined?"division":"district");
		$("#searched_value").val(division == undefined?"empty":"division = " + "'"+ division +"'");
		$("#union").html("");
		$("#village").html("");
	});

	$("#district").change(function(event) {
		getLocationHierarchy("/opensrp-dashboard/location?id="+$("#district").val().split("?")[0]+"&title=","upazila") ;
		var district = $("#district").val().split("?")[1];
		$("#address_field").val(district == ''?"district":"upazila");
		$("#searched_value").val(district == ''?"empty":"district = " + "'"+ district +"'");
		$("#pourasabha").html("");
		$("#union").html("");
		$("#village").html("");
	});
	$("#upazila").change(function(event) {
		getLocationHierarchy("/opensrp-dashboard/location?id="+$("#upazila").val().split("?")[0]+"&title=","pourasabha") ;
		var upazila = $("#upazila").val().split("?")[1];
		$("#address_field").val(upazila == ''?"upazila":"sk_id");
		$("#searched_value").val(upazila == ''?"empty":"upazila = " + "'"+ upazila +"'");
		$("#ward").html("");
	});
	$("#pourasabha").change(function(event) {
		getLocationHierarchy("/opensrp-dashboard/location?id="+$("#pourasabha").val().split("?")[0]+"&title=","union") ;
		$("#address_field").val("sk_id");
		var concatingString = "pourasabha = " + "'"+$("#pourasabha").val().split("?")[1]+"'";
		$("#searched_value").val(concatingString);
		$("#village").html("");
	});
	$("#union").change(function(event) {
		getLocationHierarchy("/opensrp-dashboard/location?id="+$("#union").val().split("?")[0]+"&title=","village") ;
	});
});
