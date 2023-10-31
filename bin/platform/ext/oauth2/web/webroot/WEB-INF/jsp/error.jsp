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
</head>

<body>
<div class="container">
    <div class="body-content">
        <div class="row">
            <div class="col-md-6">
                <h2>Authorization not granted</h2>
                <p class="lead">${fn:escapeXml(error.getMessage())}</p>
            </div>
            <div class="col-md-6">
            </div>
        </div>
    </div>
</div>

</body>
</html>
