<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
    <c:url var="cssUrl" value="/styles/hybris_main.css"/>
    <link href="${fn:escapeXml(cssUrl)}" rel="stylesheet">
	<title>Maintenance page - hybris platform </title>
</head>
<body>
<div id="head">
	&nbsp;
</div>
<div id="rightmargin">&nbsp;</div>
		<div id="headsystem" class="header">
		</div>
<div id="headtop">
    <c:url var="businessPlatformImage" value="/images/HEAD_e-business_platform.gif"/>
	<img src="${fn:escapeXml(businessPlatformImage)}" alt="hybris platform"/>
	<br />
    <c:url var="transpImage" value="/images/transp.gif"/>
    <img src="${fn:escapeXml(transpImage)}" height="20" alt=""/>
 	<br /> 
 	<div class="header">Maintenance Page</div>
    <c:set value="<%=request.getSession().getId()%>" var="sessionID"/>
 	<div class="header" style="font-size: 8px;">SID ${fn:escapeXml(sessionID)}</div>
</div>
<div id="MainNav">
</div>

<div id="Scaleback">&nbsp;</div>

<div id="main">


	<div id="right">
	</div>
	
	<div id="content">
<div id="absatz" style="text-align: left">
		&nbsp;
	</div>
	

</div>

<div id="absatz" style="text-align: left">
<p>We're currently down for maintenance.</p>
<p>Sorry for the inconvenience. We'll be back shortly. Please email us if you need to get in touch.</p>

Please try again later.
<br/><br/><br/>
<br/><br/><br/>

</div>

	<div id="absatz" style="text-align: left">&copy; SAP SE,
		<script type="text/javascript">document.write(new Date().getFullYear())</script>
		- <a href="http://www.hybris.com">www.hybris.com</a></font><p></div>
</div>
</div>
</body>
</html>
