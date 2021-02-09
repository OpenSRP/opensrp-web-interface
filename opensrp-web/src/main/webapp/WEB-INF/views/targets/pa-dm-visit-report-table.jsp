<%@page import="java.util.List"%>
<%@ page import="org.opensrp.common.dto.AggregatedBiometricDTO" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags"%>

<head>
    <style>
        th, td {
            text-align: center;
        }
        .elco-number {
            width: 30px;
        }
    </style>
</head>
<body>
<% Object targets = request.getAttribute("jsonReportData"); %>

<div id="column-chart"></div>

<table class="display table table-bordered table-striped" id="reportDataTable"
       style="width: 100%;">
    <thead>
    <c:choose>
		<c:when test="${type =='managerWise'}">
		    <tr>
				<th>AM name</th>
				<th>Number of branch</th>
				<th>Number of active PA</th>
				<th>SK target vs achievement</th>
		    </tr>
	 	</c:when>
	 	<c:otherwise>
	 		 <tr>
				<th>Location name</th>
				<th>Number of branch</th>
				<th>Number of  SK</th>
				<th>SK target vs achievement</th>
		    </tr>
	 	</c:otherwise>
	 
	 </c:choose>
    </thead>
   
    <tbody>
    
   		<c:forEach items="${reportDatas}" var="reportData">
   		<tr>
   			<c:choose>
				<c:when test="${type =='managerWise'}">
		   			<td> ${reportData.getFullName() }</td>		   			
		   			<td> ${reportData.getNumberOfBranch() }</td>
		   			<td> ${reportData.getNumberOfPA() }</td>
		   			
		   			<td> <fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${reportData.getAchievementInPercentage() }" /> %</td>
		   		
	   			</c:when>
	 
	 		
	 		<c:otherwise>
	 				<td> ${reportData.getLocationName() }</td>		   			
		   			<td> ${reportData.getNumberOfBranch() }</td>
		   			<td> ${reportData.getNumberOfPA() }</td>
		   			<td> <fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${reportData.getAchievementInPercentage() }" /> %</td>
		   		
	 		</c:otherwise>
	 		</c:choose>
	 		</tr>
		</c:forEach>
    </tbody>
	<tfoot>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tfoot>
</table>


<script>

	var reportData = <%= targets %>;
	console.log(reportData);
	var managers = [];
	var percentages = [];
	var totalSk = 0, totalPa, skTva = 0, skAchvAvailable = 0;
	for(var i=0; i < reportData.length; i++) {
		managers.push(reportData[i].firstName + ' '+ reportData[i].lastName);
		percentages.push(reportData[i].achievementInPercentage);
		totalSk+=reportData[i].numberOfPA;
		
		if(reportData[i].achievementInPercentage > 0) {
			skAchvAvailable++;
			skTva+= parseInt(reportData[i].achievementInPercentage);
		}
	}

	Highcharts.chart('column-chart', {
		chart: {
			type: 'column'
		},
		title: {
			text: 'Target vs Achievement'
		},
		subtitle: {
			text: ''
		},
		xAxis: {
			categories: managers,
			crosshair: true
		},
		yAxis: {
			min: 0,
			title: {
				text: 'Average Achievement'
			}
		},
		tooltip: {
			headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			pointFormat: '<tr><td style="color:{series.color};padding:0"> </td>' +
					'<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
			footerFormat: '</table>',
			shared: true,
			useHTML: true
		},
		plotOptions: {
			column: {
				pointPadding: 0.2,
				borderWidth: 0
			}
		},
		series: [{name:'', data: percentages}],
	});
	
	$('#totalSK').html(totalSk);
	$('#skAvgTva').html( skAchvAvailable === 0 ? 0 : (skTva / skAchvAvailable).toFixed(2));

	$('#reportDataTable').DataTable({

		scrollY: "300px",
		scrollX: true,
		scrollCollapse: true,
		fixedColumns: {
			leftColumns: 2/* ,
						 rightColumns: 1 */
		},
		"footerCallback": function ( row, data, start, end, display ) {
			var api = this.api(), data, total=0;

			// Remove the formatting to get integer data for summation
			var intVal = function ( i ) {
				return typeof i === 'string' ?
						i.replace(/[\%,]/g, '')*1 :
						typeof i === 'number' ?
								i : 0;
			};

			// Total over all pages
			$('.DTFC_LeftFootWrapper').css('margin-top', '-5px');
			$(api.column(0).footer()).html('Total');
			console.log("i am getting called in service");
			for(var i=1; i<4; i++) {
				total = api
						.column(i)
						.data()
						.reduce(function (a, b) {
							return intVal(a) + intVal(b);
						}, 0);


				$(api.column(i).footer()).html(total);
			}
		}
	});

</script>
</body>