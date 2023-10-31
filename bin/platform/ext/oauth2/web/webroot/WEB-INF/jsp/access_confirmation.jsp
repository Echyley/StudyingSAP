<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Access Confirmation &middot; hybris</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Le styles -->
    <c:url var="bootstrapCssUrl" value="/static/bootstrap/css/bootstrap.min.css"/>
    <link href="${fn:escapeXml(bootstrapCssUrl)}" rel="stylesheet">
    <c:url var="bootstrapResponsiveCssUrl" value="/static/bootstrap/css/bootstrap-responsive.min.css"/>
    <link href="${fn:escapeXml(bootstrapResponsiveCssUrl)}" rel="stylesheet">
    <style type="text/css">
        body {
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #f5f5f5;
        }

        .authorize {
            max-width: 300px;
            padding: 19px 29px 29px;
            margin: 0 auto 20px;
            background-color: #fff;
            border: 1px solid #e5e5e5;
            -webkit-border-radius: 5px;
            -moz-border-radius: 5px;
            border-radius: 5px;
            -webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
            -moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
            box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
        }

        form {
            display: inline;
        }


    </style>
</head>

<body>
<c:set var="authException" value="${sessionScope[WebAttributes.AUTHENTICATION_EXCEPTION]}"/>
<c:set var="showErrorMsg">
    <%=session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) != null && !(session.getAttribute(
            WebAttributes.AUTHENTICATION_EXCEPTION) instanceof UnapprovedClientAuthenticationException)%>
</c:set>
<div class="container">

    <div class="authorize">

        <c:if test="${showErrorMsg}">
            <div class="error">
                <div class="alert alert-error">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <strong>Whoops!</strong> Access could not be granted.
                    (${fn:escapeXml(authException.getMessage())})
                </div>
            </div>
        </c:if>
        <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>

        <h2 class="form-authorize-heading">Please confirm</h2>
        <p>You hereby authorize <strong>${fn:escapeXml(client.clientId)}</strong> to access your protected
            resources.</p>

        <c:url var="confirmationOrDenialFormActionUrl" value="/oauth/authorize"/>
        <form id="denialForm" name="denialForm" action="${fn:escapeXml(confirmationOrDenialFormActionUrl)}"
              method="post">
            <input name="user_oauth_approval" value="false" type="hidden"/>
            <input name="deny" class="btn btn-large" value="Deny" type="submit">
        </form>
        <form id="denialForm" name="denialForm" action="${fn:escapeXml(confirmationOrDenialFormActionUrl)}"
              method="post">
            <input name="user_oauth_approval" value="true" type="hidden"/>
            <input name="authorize" class="btn btn-large btn-primary" value="Authorize" type="submit">
        </form>
    </div>
</div> <!-- /container -->

</body>
</html>
