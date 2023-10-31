package de.hybris.platform.inboundservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel
import de.hybris.platform.integrationservices.enums.AuthenticationType
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Issue
import spock.lang.Unroll

@UnitTest
@Issue('https://cxjira.sap.com/browse/GRIFFIN-4231')
class ExposedDestinationICCMatchedCredentialUnitTest extends JUnitPlatformSpecification
{
	def interceptor = new ExposedDestinationICCMatchedCredentialValidateInterceptor();

	@Test
	@Unroll
	def "throws error message if credential [#exposedCredential] of Exposed Destination is not matched credential type [#authenticationType] of ICC"()
	{
		given:
		def exposedDestination = Stub(ExposedDestinationModel) {
			getInboundChannelConfiguration() >> Stub(InboundChannelConfigurationModel) {
				getAuthenticationType() >> authenticationType;
			}
			getCredential() >> exposedCredential;
		}

		when:
		interceptor.onValidate exposedDestination, Stub(InterceptorContext)

		then:
		def e = thrown InterceptorException
		e.message.contains "does not match assigned credential type of InboundChannelConfiguration"

		where:
		exposedCredential                   | authenticationType
		Stub(ExposedOAuthCredentialModel)   | AuthenticationType.BASIC
		Stub(BasicCredentialModel)          | AuthenticationType.OAUTH
		null                                | AuthenticationType.OAUTH
	}

	@Test
	@Unroll
	def "no error message if credentials are matched ([#exposedCredential] of ExposedDestination and [#authenticationType] of ICC)"()
	{
		given:
		def exposedDestination = Stub(ExposedDestinationModel) {
			getInboundChannelConfiguration() >> Stub(InboundChannelConfigurationModel) {
				getAuthenticationType() >> authenticationType;
			}
			getCredential() >> exposedCredential;
		}

		when:
		interceptor.onValidate exposedDestination, Stub(InterceptorContext)

		then:
		noExceptionThrown()

		where:
		exposedCredential                   | authenticationType
		Stub(BasicCredentialModel)          | AuthenticationType.BASIC
		Stub(ExposedOAuthCredentialModel)   | AuthenticationType.OAUTH
		Stub(ExposedOAuthCredentialModel)   | null
		null                                | null

	}
}