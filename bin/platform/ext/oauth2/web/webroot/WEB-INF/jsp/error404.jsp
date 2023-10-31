<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="errorPath" value="${requestScope['javax.servlet.forward.request_uri']}" scope="request" />
<c:choose>
    <c:when test="${header.accept=='application/xml'}">
<% response.setContentType("application/xml"); %>
<?xml version='1.0' encoding='UTF-8'?>
<errors>
   <error>
      <message>There is no resource for path ${fn:escapeXml(errorPath)}</message>
      <type>UnknownResourceError</type>
   </error>
</errors>
</c:when>
    <c:otherwise><% response.setContentType("application/json"); %>{
   "errors" : [ {
        "message": "There is no resource for path ${errorPath}"
      "type": "UnknownResourceError"
   } ]
}
</c:otherwise>
</c:choose>
