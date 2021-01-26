<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.opensrp.web.util.AuthenticationManagerUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="ISO-8859-1"%>
<%@ page import="org.opensrp.core.entity.Branch" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<%
    List<Object[]> divisions = (List<Object[]>) session.getAttribute("divisions");
    String startDate = (String) session.getAttribute("startDate");
    String endDate = (String) session.getAttribute("endDate");
%>


<div class="portlet box blue-madison">
    <div class="portlet-title">
        <div class="caption">
            <i class="fa fa-table"></i><spring:message code="lbl.searchArea"/>
        </div>
    </div>
    <div class="portlet-body">
        <div class="row"></div>
        <form autocomplete="off">
            <div class="row">
                <div class="col-md-2">
                    <label><spring:message code="lbl.startDate"/></label>
                    <input class="form-control custom-select-lg" type=text
                           name="start" id="start" value="<%=startDate%>">
                    <label style="display: none;" class="text-danger" id="startDateValidation"><small>Input is not valid for date</small></label>
                </div>
                <div class="col-md-2">
                    <label><spring:message code="lbl.endDate"/></label>
                    <input class="form-control custom-select-lg" type="text"
                           name="end" id="end" value="<%=endDate%>">
                    <label style="display: none;" class="text-danger" id="endDateValidation"><small>Input is not valid for date</small></label>
                </div>
                <div class="col-md-2" id="divisionHide">
                    <label><spring:message code="lbl.division"/></label>
                    <select required class="form-control custom-select-lg mb-3" id="division"
                            name="division">
                        <option value=""><spring:message code="lbl.selectDivision"/>
                        </option>
                        <%
                            for (Object[] objects : divisions) {
                        %>
                        <option value="<%=objects[1]%>?<%=objects[0]%>"><%=objects[0]%></option>
                        <%
                            }
                        %>
                    </select>
                </div>

                <div class="col-md-2" id="districtHide">
                    <label><spring:message code="lbl.district"/></label>
                    <select class="form-control custom-select-lg mb-3" id="district"
                            name="district">
                        <option value="0?"><spring:message code="lbl.selectDistrict"/></option>
                        <option value=""></option>
                    </select>
                </div>
                <div class="col-md-3" id="upazilaHide">
                    <label><spring:message code="lbl.upazila"/></label>
                    <select class="form-control custom-select-lg mb-3" id="upazila"
                            name="upazila">
                        <option value="0?"><spring:message code="lbl.selectUpazila"/></option>
                        <option value=""></option>

                    </select>
                </div>
            </div>
        </form>
        <input type="hidden" id ="address_field" name="address_field"/>
        <input type="hidden" id ="searched_value" name="searched_value"/>
        <input type="hidden" id ="searched_value_id" name="searched_value_id"/>
        <br/>
        <div class="row">

            <div class="col-md-6">
                <button id="search-button"
                        onclick="onSearchClicked()"
                        type="submit"
                        class="btn btn-primary">
                    <spring:message code="lbl.search"/>
                </button>
            </div>
        </div>
    </div>
    <div class="card-footer small text-muted"></div>
</div>
