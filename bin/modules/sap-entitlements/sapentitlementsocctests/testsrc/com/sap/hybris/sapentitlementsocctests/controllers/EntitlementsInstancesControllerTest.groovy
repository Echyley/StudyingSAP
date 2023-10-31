/**
 *
 */
package com.sap.hybris.sapentitlementsocctests.controllers

import com.sap.hybris.sapentitlementsfacades.facade.SapEntitlementFacade
import com.sap.hybris.sapentitlementsfacades.facade.impl.DefaultSapEntitlementFacade
import com.sap.hybris.sapentitlementsintegration.service.SapEntitlementService
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import de.hybris.platform.core.Registry
import groovyx.net.http.HttpResponseDecorator
import org.junit.AfterClass
import org.junit.BeforeClass
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
class EntitlementsInstancesControllerTest extends AbstractSpockFlowTest{


	static final String USER_ID = "democustomer"
	static final String PASSWORD = "1234"
	static final customer_with_entitlements = ["id": USER_ID, "password": PASSWORD]
	static final String ID = "1000"
	static final MODEL_NAME = "ModelName"
	static final ACTIVE = "Active"
	static final ORDER_NO =	"Ref_1234"
	static final SERVICE = "Service"

	
	@BeforeClass
	public void initMockEntitlements(){
		final SapEntitlementFacade sapEntitlementFacade = Registry.getApplicationContext().getBean("sapEntitlementFacade", SapEntitlementFacade.class);
	
		if(sapEntitlementFacade instanceof DefaultSapEntitlementFacade) {
			final SapEntitlementService mockSapEntitlementService = Registry.getApplicationContext().getBean("mockSapEntitlementService", SapEntitlementService.class);
			def defaultSapEntitlementFacade = (DefaultSapEntitlementFacade) sapEntitlementFacade 
			defaultSapEntitlementFacade.setSapEntitlementService(mockSapEntitlementService)
		}
	}
	
	@AfterClass
	public void cleanMockEntitlements(){
		final SapEntitlementFacade sapEntitlementFacade = Registry.getApplicationContext().getBean("sapEntitlementFacade", SapEntitlementFacade.class);
		
			if(sapEntitlementFacade instanceof DefaultSapEntitlementFacade) {
				final SapEntitlementService defaultSapEntitlementService = Registry.getApplicationContext().getBean("defaultSapEntitlementService", SapEntitlementService.class);
				def defaultSapEntitlementFacade = (DefaultSapEntitlementFacade) sapEntitlementFacade
				defaultSapEntitlementFacade.setSapEntitlementService(defaultSapEntitlementService)
			}
	}
	
	def setup() {
		authorizeCustomer(restClient, customer_with_entitlements)
	}

	def "Retrieve Entitlement Instances: "() {

		when: "A request is made to retrieve Entitlement Instances"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_entitlements.id + "/entitlementInstances",
				contentType: format,
				requestContentType: URLENC
				)

		then: "Entitlement Instances are retrieved."
		with(response) {
			println '---' + data
			status == SC_OK;
			isNotEmpty(data)
			data.entitlements.name[0] == MODEL_NAME
			data.entitlements.status[0] == ACTIVE
			data.entitlements.type[0] == SERVICE
			data.entitlements.quantity[0] == 5
			data.entitlements.orderNumber[0] == ORDER_NO
		}

		where:
		format << [JSON]
	}
	
	def "Retrieve Entitlement Instance with Entitlement Number: "() {
		
				when: "A request is made to retrieve Entitlement Instance withEntitlement Number "
				HttpResponseDecorator response = restClient.get(
						path: getBasePathWithSite() + '/users/' + customer_with_entitlements.id + "/entitlementInstances/" + ID,
						contentType: format,
						requestContentType: URLENC
						)
		
				then: "Entitlement Instance is retrieved."
				with(response) {
					println '---' + data
					status == SC_OK;
					isNotEmpty(data)
					data.name == MODEL_NAME
					data.status == ACTIVE
					data.type == SERVICE
					data.quantity == 5
					data.orderNumber == ORDER_NO
				}
		
				where:
				format << [JSON]
			}
}
