<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@page import="org.opensrp.common.util.CheckboxHelperUtil"%>

<%@page import="java.util.List"%>
<%@page import="org.opensrp.acl.entity.Role"%>

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
<title><spring:message code="lbl.addUserTitle"/></title>

<jsp:include page="/WEB-INF/views/css.jsp" />
</head>

<c:url var="saveUrl" value="/user/add.html" />

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
	<jsp:include page="/WEB-INF/views/navbar.jsp" />

	<div class="content-wrapper">
		<div class="container-fluid">
			<div class="form-group">				
				<jsp:include page="/WEB-INF/views/user/user-role-link.jsp" />			
			</div>
			<div class="card mb-3">
				<div class="card-header" id="data">
					<i class="fa fa-table"></i> <spring:message code="lbl.addUser"/>
				</div>
				<div class="card-body">
					
	                          <span class="text-red" id="usernameUniqueErrorMessage"></span>
	                    
	                 <div id="loading" style="display: none;position: absolute; z-index: 1000;margin-left:45%"> 
							<img width="50px" height="50px" src="<c:url value="/resources/images/ajax-loading.gif"/>"></div>
							
					</div>
					<form:form 	modelAttribute="account" id="UserInfo" class="form-inline">	
										
						<div class="row col-12 tag-height">						 
							<div class="form-group required">														
								<label class="label-width" for="inputPassword6"> <spring:message code="lbl.firstName"/> </label>										 
								<form:input path="firstName" class="form-control mx-sm-3"
								required="required" />
							</div>							
						 </div>
						 
						 <div class="row col-12 tag-height">						
							<div class="form-group required">														
								<label class="label-width" for="inputPassword6"> <spring:message code="lbl.lastName"/> </label>										 
								<form:input path="lastName" class="form-control mx-sm-3"
											required="required"/>								
							 </div>
						 </div>
						 
						 <div class="row col-12 tag-height">						
							<div class="form-group required">														
								<label class="label-width"  for="inputPassword6"> <spring:message code="lbl.email"/> </label>
								<input type="email" class="form-control mx-sm-3" name="email" required="required">										 
															
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
								
							 </div>
						 </div>
						 
						 <div class="row col-12 tag-height">						
							<div class="form-group required">														
								<label class="label-width" for="inputPassword6"><spring:message code="lbl.userName"/></label>										 
								<form:input path="username" class="form-control mx-sm-3"
										required="required" />
								<small id="passwordHelpInline" class="text-muted text-para">
	                          		<spring:message code="lbl.userMessage"/> 
	                        	</small>
							 </div>							 
						 </div>
						 
						 <form:hidden path="parentUser" id="parentUser"/>
						 <div class="row col-12 tag-height">						
							<div class="form-group">														
								<label class="label-width" for="inputPassword6"><spring:message code="lbl.parentUser"/></label>										 
								<select id="combobox" class="form-control">	</select>								
							 </div>							 
						 </div>
						 
						
						<div class="row col-12 tag-height">						
							<div class="form-group required">														
								<label class="label-width" for="inputPassword6"><spring:message code="lbl.password"/></label>										 
								<input type="password" class="form-control mx-sm-3" id="password" name="password" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}" title="Must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters" required />
								<small id="passwordHelpInline" class="text-muted text-para">
								<spring:message code="lbl.passwordMEssage"/>
	                          		 
	                        	</small>
							 </div>
						 </div>
						
						<div class="row col-12 tag-height">						
							<div class="form-group required">														
								<label class="label-width"  for="inputPassword6"><spring:message code="lbl.confirmedPassword"/></label>										 
								<form:password path="retypePassword" class="form-control mx-sm-3"
										required="required" />
								<small id="passwordHelpInline" class="text-muted text-para">
	                          		 <span class="text-red" id="passwordNotmatchedMessage"></span> <spring:message code="lbl.retypePasswordMessage"/>
	                        	</small>
							 </div>
							 
						 </div>
						
						<div class="row col-12 tag-height">						
							<div class="form-group required">
								<label class="label-width"  for="inputPassword6"><spring:message code="lbl.role"/></label>
									<%
										List<Role> roles = (List<Role>) session.getAttribute("roles");											
										for (Role role : roles) {
									%>									
										<form:checkbox 
											path="roles" class="chk" value="<%=role.getId()%>" />
										<label class="form-control mx-sm-3" for="defaultCheck1"> <%=role.getName()%></label>									
									<%
										}
									%>
								
							</div>
							
						</div>
						<div class="row col-12 tag-height">	
							<div class="form-group">
								<label class="label-width"></label>
								<div class="text-red" id="roleSelectmessage"></div>
							</div>
						</div>
						<div class="row col-12 tag-height">						
							<div class="form-group">
									<input type="submit" onclick="return Validate()"  value="<spring:message code="lbl.save"/>" 	class="btn btn-primary btn-block btn-center" />
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
		<script src="<c:url value='/resources/js/jquery-ui.js'/>"></script>
	</div>
	
	<script type="text/javascript">
	$("#UserInfo").submit(function(event) { 
			$("#loading").show();
			var url = "/opensrp-dashboard/rest/api/v1/user/save";			
			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");
			var formData = {
		            'firstName': $('input[name=firstName]').val(),
		            'lastName': $('input[name=lastName]').val(),
		            'email': $('input[name=email]').val(),
		            'mobile': $('input[name=mobile]').val(),
		            'idetifier': $('input[name=idetifier]').val(),
		            'username': $('input[name=username]').val(),
		            'password': $('input[name=password]').val(),
		            'parentUser': $('input[name=parentUser]').val(),
		            'roles': getCheckboxValueUsingClass()
		        };
			event.preventDefault();
			
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
				   $("#usernameUniqueErrorMessage").html(data);
				   $("#loading").hide();
				   if(data == ""){					   
					   window.location.replace("/opensrp-dashboard/user.html");
					   
				   }
				   
				},
				error : function(e) {
				   
				},
				done : function(e) {				    
				    console.log("DONE");				    
				}
			});
		});		
		 
	
	function getCheckboxValueUsingClass(){
		/* declare an checkbox array */
		var chkArray = [];
		
		/* look for all checkboes that have a class 'chk' attached to it and check if it was checked */
		$(".chk:checked").each(function() {
			chkArray.push($(this).val());
		});
		
		/* we join the array separated by the comma */
		var selected;
		selected = chkArray.join(',') ;		
		
		return selected;
	}
	
	 function Validate() {
		 
         var password = document.getElementById("password").value;
         var confirmPassword = document.getElementById("retypePassword").value;
         if (password != confirmPassword) {
        	 $("#passwordNotmatchedMessage").html("Your password is not similar with confirm password. Please enter same password in both");
        	
        	 return false;
         }
         
         $("#passwordNotmatchedMessage").html("");
         var chkArray = [];
 		
 		/* look for all checkboes that have a class 'chk' attached to it and check if it was checked */
 		$(".chk:checked").each(function() {
 			chkArray.push($(this).val());
 		});
 		
 		/* we join the array separated by the comma */
 		var selected;
 		selected = chkArray.join(',') ;		
 		if(selected.length > 0){			
 		}else{			
 			$("#roleSelectmessage").html("Please select at least one role");
 			return false;
 		}
 		$("#roleSelectmessage").html("");
         return true;
     }
	
		</script>
		
<script>
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
         value = "";
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
</body>
</html>