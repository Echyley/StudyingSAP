/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bocctests.test.groovy.webservicetests.v2.controllers

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.b2bocctests.setup.TestSetupUtils
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.users.AbstractUserTest
import de.hybris.platform.core.Registry
import de.hybris.platform.util.Config
import groovyx.net.http.HttpResponseDecorator
import org.apache.http.ProtocolVersion
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.message.BasicStatusLine
import org.mockito.Mockito
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static org.apache.http.HttpStatus.*
import static org.mockito.ArgumentMatchers.any

@ManualTest
@Unroll
class B2BUsersControllerTest extends AbstractUserTest {

	static final String B2BADMIN_USERNAME = "linda.wolf@rustic-hw.com"
	static final String B2BADMIN_PASSWORD = "1234"

	static final String USERS_PATH = "/users"
	static final String COMPATIBLE_USERS_PATH = "/orgUsers"
	static final String OCC_OVERLAPPING_PATHS_FLAG = "occ.rewrite.overlapping.paths.enabled"
	static final String SECURE_BASE_SITE="/wsTestB2B"
    static final String MOCK_ON_HTTP_CLIENT = "mockOnHttpClient" 

	static final ENABLED_USERS_PATH = Config.getBoolean(OCC_OVERLAPPING_PATHS_FLAG, false) ? COMPATIBLE_USERS_PATH : USERS_PATH

	def "B2B Admin gets information about himself with compatible users path: #descriptor"() {
		given: "a registered and logged in customer with B2B Admin role"
		authorizeTrustedClient(restClient)
		def b2bAdminCustomer = [
				'id'      : B2BADMIN_USERNAME,
				'password': B2BADMIN_PASSWORD
		]
		authorizeCustomer(restClient, b2bAdminCustomer)

		when: "he requests to get information about himself"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + ENABLED_USERS_PATH + "/current",
				query: [
						'fields': FIELD_SET_LEVEL_FULL
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the request is successful"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}
		where:
		descriptor << [ENABLED_USERS_PATH]
	}

	def "B2B Admin tries to get information about himself when a B2C base-site is used"() {
		given: "a registered and logged in customer with B2B Admin role"
		authorizeTrustedClient(restClient)
		def b2bAdminCustomer = [
				'id'      : B2BADMIN_USERNAME,
				'password': B2BADMIN_PASSWORD
		]
		authorizeCustomer(restClient, b2bAdminCustomer)

		when: "he requests to get information about himself"
		HttpResponseDecorator response = restClient.get(
				path: getBasePath() + "/wsTestB2C" + ENABLED_USERS_PATH + "/current",
				query: [
						'fields': FIELD_SET_LEVEL_FULL
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "request fails because a restricted B2B API endpoint is used from a B2C base-site"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_UNAUTHORIZED
		}
	}

	def "An anonymous user should be able to create a B2B registration request when captchaCheckEnabled is true and token is valid"() {
		given: "an anonymous user"
		removeAuthorization(restClient)

        and: "set captchaCheckEnabled=true"
        TestSetupUtils.updateCaptchaCheckEnabled(true);

        and: "captcha validation is passed"
        mockRecaptchaCheckResult(200, true, null, "localhost");

		when: "he requests to create registration request"
		HttpResponseDecorator response = restClient.post(
				path: getBasePath() + "/wsTest" + COMPATIBLE_USERS_PATH,
                headers: [
                        "sap-commerce-cloud-captcha-token": "mock_token"
                ],
				body: [
						"firstName": "Mark",
						"lastName" : "River",
						"email"    : "mark.river01@saptest.com",
						"titleCode": "mr",
						"message"  : "test"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "the request is successful"
		with(response) {
			status == SC_CREATED
		}

        //set captchaCheckEnabled=false
        TestSetupUtils.updateCaptchaCheckEnabled(false);
	}

    def "An anonymous user should not be able to create a B2B registration request when captchaCheckEnable is true and #scenario"() {
        given: "an anonymous user"
        removeAuthorization(restClient)

        and: "set captchaCheckEnabled=true"
        TestSetupUtils.updateCaptchaCheckEnabled(true);

        and: "captcha validation is failed"
        mockRecaptchaCheckResult(inputStatus, inputSuccess, inputReason, hostname);

        when: "he requests to create registration request"
        HttpResponseDecorator response = restClient.post(
                path: getBasePath() + "/wsTest" + COMPATIBLE_USERS_PATH,
                headers: [
                        "sap-commerce-cloud-captcha-token": inputHeader
                ],
                body: [
                        "firstName": "Mark",
                        "lastName" : "River",
                        "email"    : "mark.river01@saptest.com",
                        "titleCode": "mr",
                        "message"  : "test"
                ],
                contentType: JSON,
                requestContentType: JSON)
        then: "the request is successful"
        with(response) {
            status == responseStatus
            data.errors[0].message == errorMessage
            data.errors[0].type == errorType
            data.errors[0].reason == errorReason
        }

        //set captchaCheckEnabled=false
        TestSetupUtils.updateCaptchaCheckEnabled(false);

        where:
        scenario                    | inputHeader | inputStatus | inputSuccess | inputReason              | responseStatus | errorMessage                                               | errorType                  | errorReason              | hostname
        "header is missing"         | null        | 400         | false        | null                     | 400            | "The captcha response token is required but not provided." | "CaptchaTokenMissingError" | null                     | "localhost"
        "privateKey is missing"     | "mockToken" | 400         | false        | "missing-input-secret"   | 400            | "Invalid answer to captcha challenge."                     | "CaptchaValidationError"   | "missing-input-secret"   | "localhost"
        "privateKey is invalid"     | "mockToken" | 400         | false        | "invalid-input-secret"   | 400            | "Invalid answer to captcha challenge."                     | "CaptchaValidationError"   | "invalid-input-secret"   | "localhost"
        "header is invalid"         | "mockToken" | 400         | false        | "invalid-input-response" | 400            | "Invalid answer to captcha challenge."                     | "CaptchaValidationError"   | "invalid-input-response" | "localhost"
        "header timeout/duplicated" | "mockToken" | 400         | false        | "timeout-or-duplicated"  | 400            | "Invalid answer to captcha challenge."                     | "CaptchaValidationError"   | "timeout-or-duplicated"  | "localhost"
        "exceed quota limits"       | "mockToken" | 429         | false        | "exceed-quota-limits"    | 400            | "Invalid answer to captcha challenge."                     | "CaptchaValidationError"   | "exceed-quota-limits"    | "localhost"
        "header format is invalid"  | "<html>"    | 400         | false        | "format-invalid"         | 400            | "Invalid answer to captcha challenge."                     | "CaptchaValidationError"   | "format-invalid"         | "localhost"
        "invalid keys"              | "mockToken" | 400         | false        | "invalid-keys"           | 400            | "Invalid answer to captcha challenge."                     | "CaptchaValidationError"   | "invalid-keys"           | "localhost"
        "invalid hostname"          | "mockToken" | 400         | true         | "invalid-hostname"       | 400            | "Invalid answer to captcha challenge."                     | "CaptchaValidationError"   | "invalid-hostname"       | "electronics.local"
    }

	def "A known customer as a TRUSTED_CLIENT should be able to create a B2B registration request when captchaCheckEnabled is false and without token"() {
        given: "an anonymous user"
        authorizeTrustedClient(restClient)
        and: "set captchaCheckEnabled=false"
        TestSetupUtils.updateCaptchaCheckEnabled(false);

        when: "he requests to create registration request"
        HttpResponseDecorator response = restClient.post(
                path: getBasePath() + "/wsTest" + COMPATIBLE_USERS_PATH,
                body: [
                        "firstName": "Mark",
                        "lastName" : "River",
                        "email"    : "mark.river02@saptest.com",
                        "titleCode": "mr",
                        "message"  : "test"
                ],
                contentType: JSON,
                requestContentType: JSON)
        then: "the request is successful"
        with(response) {
            status == SC_CREATED
        }
    }

	def "An anonymous user should be able to create a B2B registration request when captchaCheckEnabled is false and without token and #scenario "() {
		given: "an anonymous user"
		removeAuthorization(restClient)

        and: "set captchaCheckEnabled=false"
        TestSetupUtils.updateCaptchaCheckEnabled(false);

		when: "he requests to create registration request"
		HttpResponseDecorator response = restClient.post(
				path: getBasePath() + "/wsTest" + COMPATIBLE_USERS_PATH,
				body: [
						"firstName": "Mark",
						"lastName" : "River",
						"email"    : email,
						"titleCode": titleCode,
						"message"  : message
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "the request is successful"
		with(response) {
			status == SC_CREATED
		}

		where:
		scenario               | titleCode | message | email
		"titleCode is missing" | null      | "test"  | "mark.river03@saptest.com"
		"message is missing"   | "mr"      | null    | "mark.river04@saptest.com"
		"message is empty"     | "mr"      | ""      | "mark.river05@saptest.com"
	}

	def "An anonymous user should not be able to create a B2B registration request  when captchaCheckEnabled is false and #scenario"() {
		given: "an anonymous user"
		removeAuthorization(restClient)
        and: "set captchaCheckEnabled=false"
        TestSetupUtils.updateCaptchaCheckEnabled(false);

		when: "he requests to create a B2B registration request"
		HttpResponseDecorator response = restClient.post(
				path: getBasePath() + "/" + baseSite + COMPATIBLE_USERS_PATH,
				body: [
						"firstName": firstName,
						"lastName" : lastName,
						"email"    : email,
						"titleCode": titleCode,
						"message"  : "test"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "fail to create a registration"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
			data.errors[0].subject == subject
		}
		where:
		scenario                                | baseSite           | firstName       | lastName        | email                       | titleCode | statusCode     | errorType                        | errorMessage                                                 | subject
		"when firstName is not supplied"        | "wsTest"           | null            | "River"         | "mark.river@saptest.com"    | "mr"      | SC_BAD_REQUEST | "ValidationError"                | "This field is required."                                    | "firstName"
		"when firstName is empty"               | "wsTest"           | ""              | "River"         | "mark.river@saptest.com"    | "mr"      | SC_BAD_REQUEST | "ValidationError"                | "This field is required."                                    | "firstName"
		"when lastName is not supplied"         | "wsTest"           | "Mark"          | null            | "mark.river@saptest.com"    | "mr"      | SC_BAD_REQUEST | "ValidationError"                | "This field is required."                                    | "lastName"
		"when lastName is empty"                | "wsTest"           | "Mark"          | ""              | "mark.river@saptest.com"    | "mr"      | SC_BAD_REQUEST | "ValidationError"                | "This field is required."                                    | "lastName"
		"when email is not supplied"            | "wsTest"           | "Mark"          | "River"         | null                        | "mr"      | SC_BAD_REQUEST | "ValidationError"                | "This field is not a valid email address."                   | "email"
		"when email is invalid"                 | "wsTest"           | "Mark"          | "River"         | "mark.river+saptest.com"    | "mr"      | SC_BAD_REQUEST | "ValidationError"                | "This field is not a valid email address."                   | "email"
		"when email is empty"                   | "wsTest"           | "Mark"          | "River"         | ""                          | "mr"      | SC_BAD_REQUEST | "ValidationError"                | "This field is not a valid email address."                   | "email"
		"when titleCode is invalid"             | "wsTest"           | "Mark"          | "River"         | "mark.river@saptest.com"    | 1         | SC_BAD_REQUEST | "RegistrationRequestCreateError" | "TitleModel with code '1' not found!"                        | null
		"when baseSite is invalid"              | "fakeSiteId"       | "Mark"          | "River"         | "mark.river@saptest.com"    | "mr"      | SC_BAD_REQUEST | "InvalidResourceError"           | "Base site fakeSiteId doesn't exist"                         | null
		"when firstName is too long"            | "wsTest"           | "a".repeat(256) | "River"         | "mark.river@saptest.com"    | "mr"      | SC_BAD_REQUEST | "ValidationError"                | "The sum of length of first name and last name exceeds 255." | "name"
		"when lastName is too long"             | "wsTest"           | "Mark"          | "a".repeat(256) | "mark.river@saptest.com"    | "mr"      | SC_BAD_REQUEST | "ValidationError"                | "The sum of length of first name and last name exceeds 255." | "name"
		"when user already exists"              | "wsTest"           | "Mark"          | "River"         | "mark.river01@saptest.com"  | "mr"      | SC_CONFLICT    | "AlreadyExistsError"             | "User already exists"                                        | null
		"when site registration is not enabled" | "wsTestSecuredB2B" | "Mark"          | "River"         | "mark.river010@saptest.com" | "mr"      | SC_BAD_REQUEST | "RegistrationRequestCreateError" | "Registration is not enabled"                                | null
	}

	def "An anonymous user should be able to create a B2B registration request when requiresAuthentication true"() {
		given: "an anonymous user"
		removeAuthorization(restClient)

		when: "he requests to create registration request"
		HttpResponseDecorator response = restClient.post(
				path: getBasePath() + SECURE_BASE_SITE + COMPATIBLE_USERS_PATH,
				body: [
						"firstName": "Mark",
						"lastName" : "Anonymous",
						"email"    : "mark.anonymous@test.com",
						"titleCode": "mr",
						"message"  : "anonymousTest"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "the request is successful"
		with(response) {
			status == SC_CREATED
		}

	}

	def "B2B Admin gets information about himself with compatible users path when requiresAuthentication true : #descriptor"() {
		given: "a registered and logged in customer with B2B Admin role"
		authorizeTrustedClient(restClient)
		def b2bAdminCustomer = [
				'id'      : B2BADMIN_USERNAME,
				'password': B2BADMIN_PASSWORD
		]
		authorizeCustomer(restClient, b2bAdminCustomer)

		and: "secure base site with requiresAuthentication=true"

		when: "he requests to get information about himself"
		HttpResponseDecorator response = restClient.get(
				path: getBasePath() + SECURE_BASE_SITE + ENABLED_USERS_PATH + "/current",
				query: [
						'fields': FIELD_SET_LEVEL_FULL
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the request is successful"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		where:
		descriptor << [ENABLED_USERS_PATH]

	}

    private void mockRecaptchaCheckResult(int httpStatus, boolean success, String reason, String hostname) {
        final CloseableHttpClient httpClient = Registry.getApplicationContext().getBean(MOCK_ON_HTTP_CLIENT, HttpClient.class);
        // it should be an spy of defaultCartFacade

        final CloseableHttpResponse httpResponse = Mockito.mock(CloseableHttpResponse.class);
        final ProtocolVersion protocolVersion = Mockito.mock(ProtocolVersion.class);

        Mockito.when(httpClient.execute(any())).thenReturn(httpResponse);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, httpStatus, reason));
        if (200 == httpStatus) {
            String response = String.format("{\"success\":%s,\"error-codes\":[\"%s\"],\"hostname\":\"%s\"}", success, reason, hostname);
            InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());

            final BasicHttpEntity httpEntity = new BasicHttpEntity();
            httpEntity.setContent(inputStreamRoute);
            Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
        }
    }
}
