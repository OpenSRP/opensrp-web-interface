<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags"%>
<%@page import="org.opensrp.web.util.AuthenticationManagerUtil"%>
<%@page import="java.util.List"%>
<%@ page import="org.opensrp.core.entity.User" %>
<%@ page import="org.opensrp.common.dto.UserDTO" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="org.opensrp.common.dto.UserAssignedLocationDTO" %>
<%@ page import="org.opensrp.core.entity.UsersCatchmentArea" %>

<%
    List<UserDTO> users = (List<UserDTO>) session.getAttribute("allSK");
    Integer userId = (Integer) session.getAttribute("userId");
%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="lbl.userList"/></title>
    <link type="text/css" href="<c:url value="/resources/css/jquery.toast.css"/>" rel="stylesheet">
    <link type="text/css" href="<c:url value="/resources/css/jquery.modal.min.css"/>" rel="stylesheet">
    <link type="text/css" href="<c:url value="/resources/css/jtree.min.css"/>" rel="stylesheet">
    <link type="text/css" href="<c:url value="/resources/css/multi-select.css"/>" rel="stylesheet">
    <meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <jsp:include page="/WEB-INF/views/css.jsp" />
</head>

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
<jsp:include page="/WEB-INF/views/navbar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid">
        <div class="form-group">
            <% if(AuthenticationManagerUtil.isPermitted("PERM_ADD_SK")){ %>
            <a  href="<c:url value="/user/add-SK.html?lang=${locale}"/>">
                <strong>
                    <spring:message code="lbl.addNew"/>
                    <spring:message code="lbl.sk"/>
                </strong> </a> <%} %>
        </div>

        <div style="overflow: unset;display: none; max-width: none; position: relative; z-index: 1050"
             id="catchment-area" class="modal">
            <div id ="modal-body" class="row">
                <div class="col-sm-5" style="overflow-y: auto; height: 350px;">
                    <div id="locationTree">
                    </div>
                </div>
                <div class="col-sm-6" style="height: 350px;">
                    <select id='locations' multiple='multiple'>
                    </select>
                </div>
                <div class="col-sm-1" style="height: 350px;">
                    <input id="userId" value="<%=userId%>" type="hidden">
                    <div class="row">
                        <button id="saveCatchmentArea"
                                disabled = true
                                class="btn btn-primary btn-sm"
                                style="position: absolute; top: 50%;
                                transform: translateY(-50%);">
                            Save
                        </button>
                        <p id="pleaseWait" style="display: none; color: red;">Please wait...</p>
                    </div>
                </div>
            </div>
            <div id="table-body" class="row" style="overflow-x: auto; margin-bottom: 10px;">
            </div>
            <a class="btn btn-sm btn-dark" href="#" rel="modal:close" style="float: right; bottom: 0px">Close</a>
            <div class="modal" id="delete-modal" style="overflow: unset;display: none; max-width: none; position: relative; z-index: 1150; max-width: 70%;">
                <h4>Do you really want to delete this location from this SK?</h4>
                <p id="delete-ss-validation"></p>
                <a class="btn btn-sm btn-danger" href="#" onclick="deleteConfirm()" style="float: right; bottom: 0px; margin-left: 5px;">Yes</a>
                <a class="btn btn-sm btn-dark" href="#" rel="modal:close" style="float: right; bottom: 0px">Close</a>
            </div>

        </div>
        <!-- Example DataTables Card-->
        <div class="card mb-3">
            <div class="card-header">
                SK List
            </div>
            <div class="card-body">
                <div class="table-responsive" style="overflow-x: auto;">
                    <table class="display" id="userList">
                        <thead>
                        <tr>
                            <th><spring:message code="lbl.fullName"></spring:message></th>
                            <th><spring:message code="lbl.userName"></spring:message></th>
                            <th><spring:message code="lbl.phoneNumber"></spring:message></th>
                            <th><spring:message code="lbl.branches"></spring:message></th>
                            <th><spring:message code="lbl.union"></spring:message></th>
                            <th><spring:message code="lbl.action"></spring:message></th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            if (users != null){
                                for (UserDTO user: users) {
                                	Integer id = user.getId();
                                	String username = user.getUsername();
                                	if (user.getLocationList() == null) {
                                		user.setLocationList("Location not assigned");
                                    }
                                    session.setAttribute("skId", id);
                                    session.setAttribute("skUsername", username);
                        %>
                        <tr>
                            <td><%=user.getFullName()%></td>
                            <td><%=user.getUsername()%></td>
                            <td><%=user.getMobile()%></td>
                            <td><%=user.getBranches()%></td>
                            <td><%=user.getLocationList()%></td>
                            <td>
                                <% if(AuthenticationManagerUtil.isPermitted("PERM_UPDATE_USER")){ %>
                                <a href="<c:url value="/user/${skId}/edit-SK.html?lang=${locale}"/>"><spring:message code="lbl.edit"/></a> |  <%} %>
                                <% if(AuthenticationManagerUtil.isPermitted("PERM_WRITE_USER")){ %>
                                <a href="#" onclick="catchmentLoad(${skId})" id = "catchment-modal"><spring:message code="lbl.catchmentArea"/></a> <%} %>
<%--                                <% if(AuthenticationManagerUtil.isPermitted("PERM_WRITE_USER")){ %>--%>
<%--                                | <a href="<c:url value="/user/${skId}/catchment-area.html?lang=${locale}"/>"><spring:message code="lbl.catchmentArea"/></a> <%} %>--%>
                                <% if(AuthenticationManagerUtil.isPermitted("PERM_WRITE_USER")){ %>
                                | <a href="<c:url value="/user/${skId}/change-password.html?lang=${locale}"/>"><spring:message code="lbl.changePassword"/></a> <%} %>
                                | <a href="<c:url value="/user/${skId}/${skUsername}/my-ss.html?lang=${locale}"/>"><spring:message code="lbl.ssList"/></a>
                            </td>
                        </tr>
                        <%
                                }
                            }
                        %>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="card-footer small text-muted"></div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/footer.jsp" />
</div>
<script src="<c:url value='/resources/js/jquery.toast.js'/>"></script>
<script src="<c:url value='/resources/js/jquery.modal.min.js'/>"></script>
<script src="<c:url value='/resources/js/jstree.min.js'/>"></script>
<script src="<c:url value='/resources/js/jquery.multi-select.js'/>"></script>
<script>
    var isTeamMember = false;
    var ssWithUCAId = [];
    var currentSK = -1;
    var currentLocation = -1;
    $(document).ready(function () {
        <%
            session.setAttribute("heading", "");
            session.setAttribute("toastMessage", "");
            session.setAttribute("icon", "");
        %>
        var heading = "<%=(String) session.getAttribute("heading")%>";
        var toastMessage = "<%=(String) session.getAttribute("toastMessage")%>";
        var icon = "<%=(String) session.getAttribute("icon")%>";
        console.log("heading: "+ toastMessage);
        if (heading != null && heading != "" && heading != 'null') {
            $.toast({
                heading: heading,
                text: toastMessage,
                icon: icon,
                position: 'top-right',
                loader: false
            });
        }
        $('#locationTree').jstree();
        $('#locations').multiSelect();
    });

    function catchmentLoad(skId) {
        $('#locationTree').jstree(true).destroy();
        $('#table-body').html("");
        $('#locations option').remove();
        $('#locations').multiSelect('refresh');
        $('#catchment-area').modal({
            escapeClose: false,
            clickClose: false,
            show: true
        });

        var url = "/opensrp-dashboard/rest/api/v1/user/"+skId+"/catchment-area";
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            contentType : "application/json",
            type: "GET",
            url: url,
            dataType : 'json',

            timeout : 100000,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success : function(data) {
                isTeamMember = data["isTeamMember"];
                var locationData = data["locationTree"];
                var assignedLocation = data["assignedLocation"];
                var catchmentAreas = data["catchmentAreas"];
                var catchmentAreaTable = data["catchmentAreaTable"];
                console.log(catchmentAreaTable[0]);
                $('#locationTree').jstree({
                    'core' : {
                        'data' : locationData
                    },
                    'checkbox' : {
                        'keep_selected_style' : false
                    },
                    'plugins': [
                        'sort', 'wholerow'
                    ]
                });
                $('#locationTree').on("changed.jstree", function (e, data) {
                    tempEdit = false;
                    $('#saveCatchmentArea').prop('disabled', true);
                    $('#locations option').remove();
                    $('#locations').multiSelect('refresh');
                    var selectedAreas = [];
                    var i, j, r = [], z = [];
                    for (var i = 0; i < catchmentAreas.length; i++) {
                        selectedAreas[i] = catchmentAreas[i];
                    }
                    var id = data.selected[0];
                    var ids = [];
                    r = data.instance.get_node(id).children;

                    for (i = 0; i < r.length; i++) {
                        z.push({
                            name: data.instance.get_node(r[i]).icon,
                            id: data.instance.get_node(r[i]).id,
                        });
                    }

                    for (i = 0; i < z.length; i++) {
                        if (selectedAreas.indexOf(parseInt(z[i].id)) >= 0) {
                            ids.push(z[i].id);
                        }
                        $('#locations').multiSelect('addOption',{
                            value: z[i].id,
                            text: z[i].name,
                            index: i
                        });
                    }
                    for (i = 0; i < assignedLocation.length; i++) {
                        var locationId = assignedLocation[i]["locationId"];
                        if (assignedLocation[i]["userId"] != skId) {
                            $("#locations option[value="+locationId+"]").attr("disabled", 'disabled');
                        }
                    }
                    $('#locations').val(ids);
                    $('#locations').multiSelect('refresh');
                }).jstree();
                //create table

                var content = "<table id='catchment-table' class='display'>";
                content += '<thead><tr><th>Division</th><th>District</th><th>City Corporation/Upazila</th><th>Pourashabha</th>' +
                    '<th>Union</th><th>Village</th><th>Action</th></tr></thead><tbody>';
                for(var y = 0; y < catchmentAreaTable.length; y++){
                    content += '<tr id="row'+y+'"><td>'+catchmentAreaTable[y][0]+'</td><td>'+catchmentAreaTable[y][1]+'</td>' +
                        '<td>'+catchmentAreaTable[y][2]+'</td><td>'+catchmentAreaTable[y][3]+'</td>' +
                        '<td>'+catchmentAreaTable[y][4]+'</td><td>'+catchmentAreaTable[y][5]+'</td>' +
                        '<td><button class="btn btn-sm btn-danger" onclick="deleteLocation('+catchmentAreaTable[y][6]+','+y+','+skId+')">Delete</button></td></tr>';
                }
                content += "</tbody></table>";

                $('#table-body').append(content);
            },
            error : function(e) {
                console.log("ERROR OCCURRED");
                $('#locationTree').jstree();
            },
            done : function(e) {
                console.log("DONE");
                $('#locationTree').jstree();
            }
        });
    }

    function deleteLocation(locationId, row, skId) {
        ssWithUCAId = [];
        currentSK = skId;
        currentLocation = locationId;
        console.log(locationId + " " + row);
        $('#delete-ss-validation').html();
        var url = "/opensrp-dashboard/rest/api/v1/user/ss-by-location?locationId="+locationId;
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            contentType : "application/json",
            type: "GET",
            url: url,
            dataType : 'json',

            timeout : 100000,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success : function(data) {
                ssWithUCAId = data;
                var ssInfos = data.map(function(ss) {
                    return ss["ss_name"];
                }).join(', ');
                console.log(ssInfos);
                $('#delete-ss-validation').html("If you release this location from this SK - <b>" +ssInfos+ "</b> also be released from those location..");
            },
            error : function(e) {
                console.log(e);
            },
            done : function(e) {
                console.log(e);
            }
        });
        $('#delete-modal').modal({
            escapeClose: false,
            clickClose: false,
            closeExisting: false,
            show: true
        });
        // $('#row'+row).remove();
    }

    function deleteConfirm() {
        console.log("in delete confirm");
        var skWithLocation = {
            sk_id: currentSK,
            sk_location_id: currentLocation,
            ss_of_sk: ssWithUCAId
        };
        console.log(skWithLocation);
        var url = "/opensrp-dashboard/rest/api/v1/user/delete-sk-location";
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            contentType : "application/json",
            type: "DELETE",
            url: url,
            dataType : 'json',
            data: JSON.stringify(skWithLocation),
            timeout : 100000,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success : function(data) {
                ssWithUCAId = data;
                var ssInfos = data.map(function(ss) {
                    return ss["ss_name"];
                }).join(', ');
                console.log(ssInfos);
                $('#delete-ss-validation').html("If you release this location from this SK - <b>" +ssInfos+ "</b> also be released from those location..");
            },
            error : function(e) {
                console.log(e);
            },
            done : function(e) {
                console.log(e);
            }
        });
    }

    $('#locations').change(function(){
        if ($('#locations').val() != null) {
            $('#saveCatchmentArea').prop('disabled', false);
        } else {
            console.log(tempEdit);
            if (tempEdit == true) {
                $('#saveCatchmentArea').prop('disabled', false);
            } else {
                $('#saveCatchmentArea').prop('disabled', true);
            }
        }
    });

    $('#saveCatchmentArea').unbind().click(function () {
        $('#saveCatchmentArea').prop('disabled', true);
        $('#pleaseWait').show();
        var url = "";
        console.log(isTeamMember);
        if (isTeamMember == true) {
            url = "/opensrp-dashboard/rest/api/v1/user/catchment-area/update";
        } else {
            url = "/opensrp-dashboard/rest/api/v1/user/catchment-area/save";
        }
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var allLocation = [];
        $("#locations option").each(function() {
            allLocation.push($(this).val());
        });

        var formData = {
            allLocation: allLocation,
            locations: $('#locations').val(),
            userId: $('#userId').val()
        };

        $.ajax({
            contentType : "application/json",
            type: "POST",
            url: url,
            data: JSON.stringify(formData),
            dataType : 'json',

            timeout : 100000,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success : function(data) {
                if(data == "") {
                    $('#pleaseWait').hide();
                    $('#catchment-area').modal('hide');
                }
            },
            error : function(e) {
                console.log(data);
                $('#saveCatchmentArea').prop('disabled', false);
                $('#pleaseWait').hide();
            },
            done : function(e) {
                console.log("DONE");
                $('#saveCatchmentArea').prop('disabled', false);
                $('#pleaseWait').hide();
            }
        });
    });
</script>

</body>
</html>
