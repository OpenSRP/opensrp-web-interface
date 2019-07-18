<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.math.RoundingMode"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="org.opensrp.core.entity.User" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags"%>

<%
    List<Object[]> divisions = (List<Object[]>) session.getAttribute("divisions");
    String startDate = (String) session.getAttribute("startDate");
    String endDate = (String) session.getAttribute("endDate");
    String memberType = (String) session.getAttribute("memberType");
    User user = (User) request.getAttribute("user");
%>

<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="utf-8">
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <meta http-equiv="refresh"
              content="<%=session.getMaxInactiveInterval()%>;url=/login" />

        <title>Form Wise Report Status</title>

        <jsp:include page="/WEB-INF/views/css.jsp" />
        <link type="text/css"
              href="<c:url value="/resources/css/dataTables.jqueryui.min.css"/>"
              rel="stylesheet">
    </head>

    <body class="fixed-nav sticky-footer bg-dark" id="page-top">
        <jsp:include page="/WEB-INF/views/navbar.jsp" />
        <div class="content-wrapper">
            <div class="container-fluid">
                <div class="card mb-3">
                    <div class="card-header">
                        <i class="fa fa-table"></i> Household Wise Report Status
                    </div>
                    <div class="card-body">
                        <div>
                            <div>
                                <p>MHV Name: <b style="font-size: 14px;"><%=user.getFullName()%></b></p>
                                <p>MHV ID: <b style="font-size: 14px;"><%=user.getUsername()%></b></p>
                                <p>MHV Phone No: <b style="font-size: 14px;"><%=user.getMobile()%></b></p>
                            </div>
                        </div>
                        <div class="row" style="margin-top: 30px;">
                            <div class="col-sm-12" id="content">
                                <table class="display" id="householdListTable"
                                       style="width: 100%;">
                                    <thead>
                                        <tr>
                                            <th><spring:message code="lbl.householdId"/></th>
                                            <th><spring:message code="lbl.householdHead"/></th>
                                            <th><spring:message code="lbl.population"/></th>
                                            <th><spring:message code="lbl.female"/></th>
                                            <th><spring:message code="lbl.male"/></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    <%
                                        List<Object[]> householdList = (List<Object[]>)session.getAttribute("householdList");
                                        for (int i = 0; i < householdList.size(); i++) {
                                            String householdBaseId = (String) householdList.get(i)[5];
                                            %>
                                            <tr>
                                                <td>
                                                    <a href="<c:url value="/report/household-member-list.html">
											                    <c:param
                                                                    name="householdBaseId"
                                                                    value="<%=householdBaseId%>"/>
                                                             </c:url>">
                                                        <%=householdList.get(i)[0]%>
                                                    </a>
                                                </td>
                                                <td><%=householdList.get(i)[1]%></td>
                                                <td><%=householdList.get(i)[2]%></td>
                                                <td><%=householdList.get(i)[4]%></td>
                                                <td><%=householdList.get(i)[3]%></td>
                                            </tr>
                                    <%
                                        }
                                    %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <jsp:include page="/WEB-INF/views/footer.jsp" />
        </div>
    </body>
    <script src="<c:url value='/resources/js/jquery-1.12.4.js' />"></script>
    <script src="<c:url value='/resources/js/jquery.dataTables.min.js' />"></script>
    <script src="<c:url value='/resources/js/dataTables.jqueryui.min.js' />"></script>
    <script src="<c:url value='/resources/js/jquery-ui.js' />"></script>
    <script src="<c:url value='/resources/js/datepicker.js' />"></script>
    <script src="<c:url value='/resources/js/jspdf.debug.js' />"></script>
    <script src="<c:url value='/resources/js/jquery.tabletoCSV.js' />"></script>
    <script src="<c:url value='/resources/js/jquery.tabletoPDF.js' />"></script>
    <script>
        $(document).ready(function() {
            $('#householdListTable').DataTable({
                "paginate" : true
            });
        });

        $("#exportcsv").click(function() {
            $("table").tableToCSV();
        });

        $("#exportpdf").click(function() {
            $("table").tableToPDF();
        });
    </script>
</html>