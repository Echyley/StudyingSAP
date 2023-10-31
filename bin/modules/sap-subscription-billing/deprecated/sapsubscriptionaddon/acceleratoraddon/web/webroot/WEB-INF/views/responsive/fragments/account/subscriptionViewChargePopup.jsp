<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="subscription" tagdir="/WEB-INF/tags/addons/sapsubscriptionaddon/responsive/subscription"%>
<%-- <%@ attribute name="subscription" required="true" type="de.hybris.platform.subscriptionfacades.data.SubscriptionData" %> --%>


<spring:htmlEscape defaultHtmlEscape="true" />

<c:url value="/my-account/subscription/${subscriptionData.id}/ratePlan" var="viewChargesUrl" />

  <form:form id="subscriptionRatePlanForm"
             name="subscriptionRatePlanForm"
             action="${fn:escapeXml(viewChargesUrl)}" method="post"
             modelAttribute="subscriptionRatePlanForm">

	<div class="mini-cart js-mini-cart">
		<div class="mini-cart-body">
            <subscription:subscriptionDiscountedRatePlanPanel subscription="${subscriptionData}" />
        </div>
	</div>


</form:form>
