<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
	
<%@page import="org.opensrp.acl.entity.TeamMember"%>
<%@page import="org.opensrp.acl.entity.Location"%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>

<title>Team List</title>

<jsp:include page="/WEB-INF/views/css.jsp" />
</head>
<%
Map<String, String> paginationAtributes = (Map<String, String>) session
.getAttribute("paginationAtributes");
String name = "";
if (paginationAtributes.containsKey("name")) {
	name = paginationAtributes.get("name");
}
%>
<body class="fixed-nav sticky-footer bg-dark" id="page-top">
	<jsp:include page="/WEB-INF/views/navbar.jsp" />

	<div class="content-wrapper">
		<div class="container-fluid">
			<!-- Example DataTables Card-->
		<div class="form-group">				
				   <a  href="<c:url value="/team/list.html"/>"> <strong> Manage Team</strong> 
					</a>  |  <a  href="<c:url value="/team/teammember/list.html"/>"> <strong>Manage Team Member</strong>
					</a>		
		</div>
		<div class="form-group">
			<h1>Team Member Management</h1>
			<a  href="<c:url value="/team/teammember/add.html"/>"> <strong>Add New Team Member</strong>
					</a>
		</div>
		<div class="card mb-3">
				
				<div class="card-body">
					<form id="search-form">
						<div class="row">
							<div class="col-3">					
							<input name="name" type="search" class="form-control"
							value="<%=name%>" placeholder="">					
							</div>
							<div class="col-6">
								<button name="search" type="submit" id="bth-search"
									class="btn btn-primary" value="search">Search</button>
							</div>
						</div>			
					</form>
				</div>
				<div class="card-footer small text-muted"></div>
			</div>
			<div class="card mb-3">
				<div class="card-header">
					<i class="fa fa-table"></i> Team Member List
				</div>
				<div class="card-body">
					<div class="table-responsive">
						<table class="table table-bordered" id="dataTable">
							<thead>
								<tr>
									<th>Name</th>
									<th>Identifier</th>									
									<th>Location</th>
									<th>Team</th>									
								</tr>
							</thead>
							<tfoot>
								<tr>
									<th>Name</th>
									<th>Identifier</th>									
									<th>Location</th>
									<th>Team</th>	
								</tr>
							</tfoot>
							<tbody>
							
							<%
								List<TeamMember> teamMembers = (List<TeamMember>) session
														.getAttribute("dataList");
								
								String team = "";
							
								for (TeamMember teamMember : teamMembers) 
									{
									pageContext.setAttribute("id", teamMember.getId());
									
									Set<Location> locations = teamMember.getLocations();
									String locationNames = "";
									if(locations.size()!=0){
										for (Location location : locations) {
											locationNames +=location.getName()+" <br /> ";
										}
										
									}
									if(teamMember.getTeam()!= null){
										team = teamMember.getTeam().getName();
									}
							%>
								
									<tr>
										<td><a href="<c:url value="/team/teammember/${id}/edit.html"/>"><%=teamMember.getPerson().getUsername() %></a></td>
										<td><%=teamMember.getIdentifier() %></td>
										<td><%=locationNames%></td>
										<td><%=team%></td>
										

									</tr>
									<%
									}
									%>
								
							</tbody>
						</table>
					</div>
				</div>
				<jsp:include page="/WEB-INF/views/pager.jsp" />
				<div class="card-footer small text-muted"></div>
			</div>
		</div>
		<!-- /.container-fluid-->
		<!-- /.content-wrapper-->
		<jsp:include page="/WEB-INF/views/footer.jsp" />
	</div>

</body>
</html>

