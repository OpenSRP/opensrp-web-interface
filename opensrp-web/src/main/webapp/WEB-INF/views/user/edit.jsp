<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@page import="org.opensrp.common.util.CheckboxHelperUtil"%>
<%@ taglib prefix="sec"
		   uri="http://www.springframework.org/security/tags"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.opensrp.core.entity.Role"%>
<%@ page import="org.opensrp.core.entity.Branch" %>
<%@ page import="java.util.Set" %>

<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="utf-8">
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link type="text/css" href="<c:url value="/resources/css/magicsuggest-min.css"/>" rel="stylesheet">

	<title><spring:message code="lbl.editUserTitle"/></title>
	<link type="text/css" href="<c:url value="/resources/css/select2.css"/>" rel="stylesheet">
	<jsp:include page="/WEB-INF/views/css.jsp" />
</head>

<c:url var="saveUrl" value="/user/${id}/edit.html" />
<%
	Set<Branch> selectedBranches = (Set<Branch>)session.getAttribute("selectedBranches");
	String selectedParentUser = (String)session.getAttribute("parentUserName");
	Integer selectedParentId = (Integer)session.getAttribute("parentUserId");

//for teamMember
	Integer selectedPersonId = (Integer)session.getAttribute("selectedPersonId");
	String locationList = (String)session.getAttribute("locationList");
	String selectedLocationList = (String)session.getAttribute("selectedLocationList");

	Map<Integer, String> teams =  (Map<Integer, String>)session.getAttribute("teams");

	String selectedPersonName = (String)session.getAttribute("personName");

	Integer selectedTeamId = (Integer)session.getAttribute("selectedTeamId");
	int roleIdCHCP= -1;
	int roleIdProvider= -1;

%>

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
<jsp:include page="/WEB-INF/views/navbar.jsp" />

<div class="content-wrapper">
	<div class="container-fluid">
		<div class="form-group">
			<jsp:include page="/WEB-INF/views/user/user-role-link.jsp" />
		</div>
		<div class="card mb-3">
			<div class="card-header">
				<spring:message code="lbl.editUserTitle"/>				</div>
			<div class="card-body">
				<form:form method="POST" action="${saveUrl}"
						   modelAttribute="account" class="form-inline">


					<div class="row col-12 tag-height">
						<div class="form-group required">
							<label class="label-width" for="inputPassword6"> <spring:message code="lbl.firstName"/> </label>
							<form:input path="firstName" class="form-control mx-sm-3"
										required="required"/>
						</div>
					</div>

					<div class="row col-12 tag-height">
						<div class="form-group required">
							<label class="label-width" for="inputPassword6"><spring:message code="lbl.lastName"/> </label>
							<form:input path="lastName" class="form-control mx-sm-3"
										required="required"/>
						</div>
					</div>

					<div class="row col-12 tag-height">
						<div class="form-group required">
							<label class="label-width"  for="inputPassword6"> <spring:message code="lbl.email"/> </label>
							<input type="email" class="form-control mx-sm-3" name="email" value="${account.getEmail()}" required="required">

						</div>
					</div>

					<div class="row col-12 tag-height">
						<div class="form-group">
							<label class="label-width" for="inputPassword6"><spring:message code="lbl.mobile"/></label>
							<form:input path="mobile" class="form-control mx-sm-3" />
						</div>
					</div>

					<div class="row col-12 tag-height">
						<div class="form-group">
							<label class="label-width" for="inputPassword6"><spring:message code="lbl.identifier"/></label>
							<form:input path="idetifier" class="form-control mx-sm-3" />
							<small id="passwordHelpInline" class="text-muted text-para">
								<spring:message code="lbl.identifierMsg"/>
							</small>
						</div>
					</div>



					<div class="row col-12 tag-height">
						<div class="form-group required">
							<label class="label-width" for="inputPassword6"><spring:message code="lbl.userName"/></label>
							<form:input path="username" class="form-control mx-sm-3"
										readonly="true"	required="required"/>
							<small id="passwordHelpInline" class="text-muted text-para">
								<span class="text-red" id="usernameUniqueErrorMessage"></span> <spring:message code="lbl.userMessage"/>
							</small>
						</div>
					</div>
					<form:hidden path="parentUser" id="parentUser" value="<%=selectedParentId %>"/>
					<div class="row col-12 tag-height">
						<div class="form-group">
							<label class="label-width" for="inputPassword6"><spring:message code="lbl.parentUser"/></label>
							<select id="combobox" class="form-control">	</select>
						</div>
					</div>


					<form:hidden path="uuid" />
					<form:hidden path="personUUid" />
					<form:hidden path="provider" />
					<form:hidden path="chcp" />


					<form:hidden path="id" />
					<form:hidden path="password" />

					<div class="row col-12 tag-height">
						<div class="form-group required">
							<label class="label-width"  for="inputPassword6"><spring:message code="lbl.role"/></label>
							<%
								if(session.getAttribute("roles")!=null){
									List<Role> roles = (List<Role>) session.getAttribute("roles");
									int[] selectedRoles = (int[]) session.getAttribute("selectedRoles");
									for (Role role : roles) {
										if(role.getName().equals("Provider")){
											roleIdProvider = role.getId();
										}else if(role.getName().equals("CHCP")){
											roleIdCHCP = role.getId();
										}
							%>

							<form:radiobutton class="checkBoxClass form-check-input"
										   path="roles" value="<%=role.getId()%>" onclick='roleSelect(this)'
										   checked="<%=CheckboxHelperUtil.checkCheckedBox(selectedRoles,role.getId())%>" />
							<label class="form-control mx-sm-3" for="defaultCheck1"> <%=role.getName()%>
							</label>

							<%
									}
								}
							%>
						</div>
					</div>



					<!-- for location -->
					<div class="row col-12 tag-height" id="locationDiv" style="display:none">
						<div class="form-group">
							<label class="label-width" for="inputPassword6"><spring:message code="lbl.location"/></label>
							<div id="cm" class="ui-widget ">
								<div id="locationsTag" ></div>
								<span class="text-red">${locationSelectErrorMessage}</span>
							</div>
						</div>
					</div>

					<%--  <div id="cm" class="ui-widget">
                                    <label><spring:message code="lbl.location"/> </label>
                                    <div id="locationsTag"></div>
                                    <span class="text-red">${locationSelectErrorMessage}</span>
                    </div> --%>

					<div class="row col-12 tag-height">
						<div class="form-group required">
							<label class="label-width"  for="branches">
								<spring:message code="lbl.branches"/>
							</label>
							<select
									name="branches"
									id="branches"
									class="form-control mx-sm-3 js-example-basic-multiple"
									multiple="multiple">
								<c:forEach items="${branches}" var="branch">
									<option value="${branch.id}">${branch.name} (${branch.code})</option>
								</c:forEach>
							</select>
						</div>
					</div>

					<!-- end: for location -->
					<!-- for team -->
					 <div class="row col-12 tag-height" id="teamDiv" style="display:none">
							<div class="form-group">
								<label class="label-width" for="inputPassword6"><spring:message code="lbl.team"/></label>
									<select class="form-control mx-sm-3" id="team" name="team" required="required" disabled>
										<option value="" selected><spring:message code="lbl.pleaseSelect"/></option>
											<%
											for (Map.Entry<Integer, String> entry : teams.entrySet())
											{
												if(selectedTeamId==entry.getKey()){ %>
													<option value="<%=entry.getKey()%>" selected><%=entry.getValue() %></option>
												<% }else{
													%>
														<option value="<%=entry.getKey()%>"><%=entry.getValue() %></option>
													<%
												}

											}
											%>
										</select>
							</div>

					</div>
					<!--end: for team -->



					<div class="row col-12 tag-height">
						<div class="form-group">
							<label class="label-width" for="inputPassword6"><spring:message code="lbl.activeUser"/></label>
							<form:checkbox class="checkBoxClass form-check-input"
										   path="enabled" value="${account.isEnabled()}"/>
						</div>
					</div>

					<div class="row col-12 tag-height">
						<div class="form-group">
							<input type="submit" value="<spring:message code="lbl.edit"/>"
								   class="btn btn-primary btn-block" />
						</div>
					</div>
				</form:form>

			</div>
			<div class="card-footer small text-muted"></div>
		</div>
	</div>
	<!-- /.container-fluid-->
	<!-- /.content-wrapper-->
	<jsp:include page="/WEB-INF/views/footer.jsp" />
</div>
</body>

<script src="<c:url value='/resources/js/magicsuggest-min.js'/>"></script>
<script src="<c:url value='/resources/js/jquery-ui.js'/>"></script>
<script src="<c:url value='/resources/js/select2.js' />"></script>

<script>
	var locationMagicSuggest;
	var isCHCP= 0;
	var isProvider= 0;
	function roleSelect(cBox){
		//alert(cBox.checked+" - "+cBox.value);
		var roleIdOfCHCP = <%=roleIdCHCP%>;
		var roleIdOfProvider = <%=roleIdProvider%>;
		var roleIdOfClickedCheckbox = cBox.value;

		if(roleIdOfClickedCheckbox == roleIdOfCHCP){
			if(cBox.checked){
				isCHCP= 1;
			}else{
				isCHCP= 0;
			}
		}

		if(roleIdOfClickedCheckbox == roleIdOfProvider){
			if(cBox.checked){
				isProvider= 1;
			}else{
				isProvider= 0;
			}
		}
		showTeamAndLocationDiv();
	}

	function showTeamAndLocationDiv(){
		if(isTeamMember()){
			$("#locationDiv").show();
			$("#team").prop('required',true);
			$("#team").prop('disabled', false);
			$("#teamDiv").show();
		}else{
			$("#locationDiv").hide();
			$("#team").prop('required',false);
			$("#team").prop('disabled', true);
			$("#teamDiv").hide();
		}
	}

	function isTeamMember(){
		if(isCHCP== 1 || isProvider== 1){
			return true;
		}
		return false;
	}






	$( function() {
		$.widget( "custom.combobox", {
			_create: function() {
				this.wrapper = $( "<div>" )
						.addClass( "custom-combobox" )
						.insertAfter( this.element );

				this.element.hide();
				this._createAutocomplete();

			},

			_createAutocomplete: function() {
				var selected = this.element.children( ":selected" ),
						value = selected.val() ? selected.text() : "";
				value = "<%=selectedParentUser%>";
				this.input = $( "<input>" )
						.appendTo( this.wrapper )
						.val( value )
						.attr( "title", "" )
						.attr( "name", "parentUserName" )
						.addClass( "form-control mx-sm-3 ui-widget ui-widget-content  ui-corner-left" )
						.autocomplete({
							delay: 0,
							minLength: 1,
							source: $.proxy( this, "_source" )
						})
						.tooltip({
							classes: {
								"ui-tooltip": "ui-state-highlight"
							}
						});

				this._on( this.input, {
					autocompleteselect: function( event, ui ) {
						ui.item.option.selected = true;
						$("#parentUser").val(ui.item.option.value);
						this._trigger( "select", event, {
							item: ui.item.option
						});
					},

					autocompletechange: "_removeIfInvalid"
				});
			},



			_source: function( request, response ) {

				$.ajax({
					type: "GET",
					dataType: 'html',
					url: "/opensrp-dashboard/user/user.html?name="+request.term,
					success: function(res)
					{

						$("#combobox").html(res);
					}
				});
				var matcher = new RegExp( $.ui.autocomplete.escapeRegex(request.term), "i" );
				response( this.element.children( "option" ).map(function() {
					var text = $( this ).text();
					if ( this.value && ( !request.term || matcher.test(text) ) )
						return {
							label: text,
							value: text,
							option: this
						};
				}) );
			},

			_removeIfInvalid: function( event, ui ) {

				// Selected an item, nothing to do
				if ( ui.item ) {
					return;
				}

				// Search for a match (case-insensitive)
				var value = this.input.val(),
						valueLowerCase = value.toLowerCase(),
						valid = false;
				this.element.children( "option" ).each(function() {
					if ( $( this ).text().toLowerCase() === valueLowerCase ) {
						this.selected = valid = true;
						return false;
					}
				});

				// Found a match, nothing to do
				if ( valid ) {
					return;
				}

				// Remove invalid value
				this.input
						.val( "" )
						.attr( "title", value + " didn't match any item" )
						.tooltip( "open" );
				$("#parentUser").val(0);
				this.element.val( "" );
				this._delay(function() {
					this.input.tooltip( "close" ).attr( "title", "" );
				}, 2500 );
				this.input.autocomplete( "instance" ).term = "";
			},

			_destroy: function() {
				this.wrapper.remove();
				this.element.show();
			}
		});

		$( "#combobox" ).combobox();

		$( "#toggle" ).on( "click", function() {
			$( "#combobox" ).toggle();
		});


	} );
</script>
<script type="text/javascript">

	locationMagicSuggest = $('#locationsTag').magicSuggest({
		required: true,
		//placeholder: 'Type Locations',
		data: <%=locationList%>,
		valueField: 'id',
		displayField: 'value',
		name: 'locationList',
		inputCfg: {"class":"magicInput"},
		value: <%=selectedLocationList%>,
		useCommaKey: true,
		allowFreeEntries: false,
		maxSelection: 2,
		maxEntryLength: 70,
		maxEntryRenderer: function(v) {
			return '<div style="color:red">Typed Word TOO LONG </div>';
		}

	});
</script>
<script>
	$(document).ready(function() {
		$('.js-example-basic-multiple').select2({dropdownAutoWidth : true});
		var selectedBranchList = [];
		<%if (selectedBranches != null) {
			for (Branch branch: selectedBranches) {%>
				selectedBranchList.push(<%=branch.getId()%>);
			<%}
		}%>
		$('.js-example-basic-multiple').val(selectedBranchList);
		$('.js-example-basic-multiple').select2().refresh();
	});
</script>
</html>