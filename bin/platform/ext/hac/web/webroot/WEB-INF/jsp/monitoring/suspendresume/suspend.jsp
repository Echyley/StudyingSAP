<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="hac" uri="/WEB-INF/custom.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <script type="application/javascript">
        window.onload = function () {
            document.forms['suspendStatus'].submit();
        };
    </script>

</head>
<body>
<div class="prepend-top span-17 colborder" id="content">
    <div class="marginLeft">
        <div>
            <form name="suspendStatus" action="<c:url value="/monitoring/suspendresume/status"/>" method="post">
                <input type="hidden" name="suspendToken" value="${fn:escapeXml(suspendToken)}"/>
                <input type="hidden" name="resumeToken" value="${fn:escapeXml(resumeToken)}"/>
            </form>
        </div>
    </div>

</div>
</body>
</html>


