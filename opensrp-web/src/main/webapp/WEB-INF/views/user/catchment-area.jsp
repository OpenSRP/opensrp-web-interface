<%@ page language="java" contentType="text/html; charset=UTF-8"
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
    <meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <link type="text/css" href="<c:url value="/resources/css/jtree.min.css"/>" rel="stylesheet">
    <link type="text/css" href="<c:url value="/resources/css/multi-select.css"/>" rel="stylesheet">
    <title><spring:message code="lbl.catchmentArea"/> </title>
    <%@page import="org.json.JSONObject" %>
    <%@page import="org.json.JSONArray" %>
    <%@ page import="org.opensrp.core.entity.UsersCatchmentArea" %>
    <%@ page import="java.util.List" %>
    <%@ page import="org.opensrp.core.entity.User" %>
    <%@ page import="org.opensrp.common.dto.UserAssignedLocationDTO" %>
    <%@ page import="org.opensrp.web.util.AuthenticationManagerUtil" %>
    <jsp:include page="/WEB-INF/views/css.jsp" />
</head>
<%
    JSONArray locationTreeData = (JSONArray)session.getAttribute("locationTreeData");
    Integer userId = (Integer) session.getAttribute("userId");
    User user = (User) session.getAttribute("user");
    List<UsersCatchmentArea> usersCatchmentAreas = (List<UsersCatchmentArea>) session.getAttribute("usersCatchmentAreas");
    List<UserAssignedLocationDTO> userAssignedLocationDTOS = (List<UserAssignedLocationDTO>) session.getAttribute("assignedLocation");
    String fromRole = (String) session.getAttribute("fromRole");
    String role = AuthenticationManagerUtil.isAM()?"AM":"";
    Integer skId = (Integer) session.getAttribute("idFinal");
    String skUsername = (String) session.getAttribute("usernameFinal");
%>
<body class="fixed-nav sticky-footer bg-dark" id="page-top">
<jsp:include page="/WEB-INF/views/navbar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid">
        <!-- Example DataTables Card-->
        <div class="form-group">
            <jsp:include page="/WEB-INF/views/location/location-tag-link.jsp" />
        </div>

        <div class="card mb-3">
            <div class="card-header">
                <spring:message code="lbl.userInfo"/>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-sm-6">
                        <h4><%=user.getFullName()%></h4>
                    </div>
                    <div class="col-sm-6">
                        <h5><%=user.getUsername()%></h5>
                    </div>
                </div>
            </div>
        </div>

        <div class="card mb-3">
            <div class="card-header">
                <spring:message code="lbl.viewLocationsHierarchy"/>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-sm-5" style="overflow-y: auto; max-height: 350px;">
                        <div id="locationTree">
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <select id='locations' multiple='multiple'>
                        </select>
                    </div>
                    <div class="col-sm-1">
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
            </div>
            <div class="card-footer small text-muted"></div>
        </div>

        <div class="card mb-3">
            <div class="card-header">
                <spring:message code="lbl.catchmentArea"/>
            </div>
            <div class="card-body">
                <div class="row">
                </div>
            </div>
            <div class="card-footer small text-muted"></div>
        </div>
    </div>
    <!-- /.container-fluid-->
    <!-- /.content-wrapper-->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>
<script src="<c:url value='/resources/js/jstree.min.js'/>"></script>
<script src="<c:url value='/resources/js/jquery.multi-select.js'/>"></script>
<script type="text/javascript">
    var tempEdit = false;
    $(document).ready(function () {
        $('#locationTree').jstree({
            'core' : {
                'data' : <%=locationTreeData %>
            },
            'checkbox' : {
                'keep_selected_style' : false
            },
            'plugins': [
                'sort', 'wholerow'
            ]
        });

        $('#locations').multiSelect();

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

        $('#locationTree').on('changed.jstree', function (e, data) {
            tempEdit = false;
            $('#saveCatchmentArea').prop('disabled', true);
            $('#locations option').remove();
            $('#locations').multiSelect('refresh');
            var selectedAreas = [];
            <% if (usersCatchmentAreas != null) {
                for (int i = 0; i < usersCatchmentAreas.size(); i++) {%>
                    selectedAreas[<%=i%>] = <%=usersCatchmentAreas.get(i).getLocationId()%>
                <%}
            }%>
            var i, j, r = [], z = [];
            var id = data.selected[0];
            var ids = [];
            r = data.instance.get_node(id).children;

            for (i = 0; i < r.length; i++) {
                z.push({
                    name: data.instance.get_node(r[i]).text,
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

            <% for (UserAssignedLocationDTO dto: userAssignedLocationDTOS) {
                if(user.getId() != dto.getId()) {%>
            	    $('#locations option[value=<%=dto.getLocationId()%>]').attr("disabled", 'disabled');
                <%}
            }%>
            $('#locations').val(ids);
            $('#locations').multiSelect('refresh');
        }).jstree();

        $('#saveCatchmentArea').unbind().click(function () {
            $('#saveCatchmentArea').prop('disabled', true);
            $('#pleaseWait').show();
            var url = "";
            <% if (isTeamMember) {%>
            url = "/opensrp-dashboard/rest/api/v1/user/catchment-area/update";
            <%} else {%>
            url = "/opensrp-dashboard/rest/api/v1/user/catchment-area/save";
            <% } %>
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            var allLocation = [];
            $("#locations option").each(function() {
                allLocation.push($(this).val());
            });

            var redirectUrl = "/opensrp-dashboard/user.html";
            var role = "<%=role%>";
            var fromRole = "<%=fromRole%>";
            var skId = "<%=skId%>";
            var skUsername = "<%=skUsername%>";

            console.log(role);
            if (role == 'AM') {
                if (fromRole == 'SK') {
                    redirectUrl = "/opensrp-dashboard/user/sk-list.html";
                } else if (fromRole == 'SS') {
                    redirectUrl = "/opensrp-dashboard/user/"+skId+"/"+skUsername+"/my-ss.html?lang=en"
                }
            }

            console.log("redirect url: "+ redirectUrl);

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
                    window.location.replace(redirectUrl);
                },
                error : function(e) {
                    $('#saveCatchmentArea').prop('disabled', false);
                    $('#pleaseWait').hide();
                },
                done : function(e) {
                    $('#saveCatchmentArea').prop('disabled', false);
                    $('#pleaseWait').hide();
                }
            });
        });
    });
    <%--function editLocation(parentId) {--%>
    <%--    tempEdit = true;--%>
    <%--    $('#saveCatchmentArea').prop('disabled', false);--%>
    <%--    $('#locations option').remove();--%>
    <%--    $('#locations').multiSelect('refresh');--%>
    <%--    var i, selectedAreas = [], z = [], locations = [], ids = [];--%>

    <%--    <%if (usersCatchmentAreas != null) {--%>
    <%--        for (int i = 0; i < usersCatchmentAreas.size(); i++) {%>--%>
    <%--            selectedAreas[<%=i%>] = <%=usersCatchmentAreas.get(i).getLocationId()%>--%>
    <%--        <%}--%>
    <%--    }%>--%>

    <%--    locations = $('#locationTree').jstree(true).get_node(parentId).children;--%>
    <%--    console.log(locations);--%>
    <%--    for (i = 0; i < locations.length; i++) {--%>
    <%--        z.push({--%>
    <%--            name: $('#locationTree').jstree(true).get_node(locations[i]).text,--%>
    <%--            id: $('#locationTree').jstree(true).get_node(locations[i]).id--%>
    <%--        });--%>
    <%--    }--%>

    <%--    for (i = 0; i < z.length; i++) {--%>
    <%--        if (selectedAreas.indexOf(parseInt(z[i].id)) >= 0) {--%>
    <%--            ids.push(z[i].id);--%>
    <%--        }--%>
    <%--        $('#locations').multiSelect('addOption',{--%>
    <%--            value: z[i].id,--%>
    <%--            text: z[i].name,--%>
    <%--            index: i--%>
    <%--        });--%>
    <%--    }--%>

    <%--    $('#locations').val(ids);--%>
    <%--    <% for (UserAssignedLocationDTO dto: userAssignedLocationDTOS) {--%>
    <%--        if(user.getId() != dto.getId()) {%>--%>
    <%--            $('#locations option[value=<%=dto.getLocationId()%>]').attr("disabled", 'disabled');--%>
    <%--        <%}--%>
    <%--    }%>--%>

    <%--    $('#locations').multiSelect('refresh');--%>
    <%--    console.log($('#locations').val());--%>
    <%--}--%>
    $("a[href='#top']").click(function() {
        $("html, body").animate({ scrollTop: 0 }, "slow");
        return false;
    });
</script>
</body>
</html>

