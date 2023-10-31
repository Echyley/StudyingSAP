/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bocctests.test.groovy.webservicetests.v2.controllers

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_NOT_FOUND
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.users.AbstractUserTest

import groovyx.net.http.RESTClient
import spock.lang.Unroll

@ManualTest
@Unroll
class B2BCostCentersControllerTest extends AbstractUserTest {

    private static final B2B_CUSTOMER = ['id': "mark.rivers@rustic-hw.com", 'password': "1234"]
    private static final B2B_ADMIN = ['id': "linda.wolf@rustic-hw.com", 'password': "1234"]
    private static final B2B_ADMIN_PRONTO = ['id': "linda.wolf@pronto-hw.com", 'password': "1234"]

    private static final int NUMBER_OF_COST_CENTERS = 4
    private static final int NUMBER_OF_BUDGETS = 2
    private static final int PAGE_SIZE = 20

    static final COST_CENTERS_PATH = "/costcenters"
    static final BUDGETS_PATH = "/budgets"

    def "B2B Admin gets cost centers: #format"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to get cost centers"
        def response = restClient.get(
                path: getBasePathWithSite() + "/costcentersall",
                contentType: format,
                requestContentType: URLENC)

        then: "he/she gets cost centers"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            data.costCenters.size() == NUMBER_OF_COST_CENTERS
            data.sorts.size() > 0
            data.pagination.pageSize == PAGE_SIZE
            data.pagination.currentPage == 0
            data.pagination.totalResults.toInteger() == NUMBER_OF_COST_CENTERS
        }

        where:
        format << [JSON]
    }

    def "B2B Customer tries to get cost centers"() {
        given: "a registered and logged in B2B customer without B2B Admin role"
        authorizeCustomer(restClient, B2B_CUSTOMER)

        when: "he requests to get cost centers"
        def response = restClient.get(
                path: getBasePathWithSite() + "/costcentersall",
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets unauthorized error"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_UNAUTHORIZED
            data.errors[0].type == "AccessDeniedError"
            data.errors[0].message == "Access is denied"
        }
    }

    def "B2B user (#user.id) gets active cost centers: #format"() {
        given: "a registered and logged in B2B customer without B2B Admin role"
        authorizeCustomer(restClient, user)

        when: "he requests to get active cost centers"
        def response = restClient.get(
                path: getBasePathWithSite() + COST_CENTERS_PATH,
                contentType: format,
                requestContentType: URLENC)

        then: "he/she gets active cost centers with unit addresses"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            data.costCenters.size() == NUMBER_OF_COST_CENTERS
            data.costCenters[0].unit.addresses.size() == 4
        }

        where:
        format | user
        JSON   | B2B_CUSTOMER
        JSON   | B2B_ADMIN
    }

    def "B2B Admin creates a new cost center"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to create a new cost center"
        def response = restClient.post(
                path: getBasePathWithSite() + COST_CENTERS_PATH,
                body: postBody,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets created response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            compareCostCenters(data, costCenterCode, name, currencyIso, null)
            status == SC_CREATED
        }

        def createdCostCenter = getCostCenter(restClient, costCenterCode)

        and: "creation of cost center is persistent"
        with(createdCostCenter) {
            compareCostCenters(data, costCenterCode, name, currencyIso, null)
        }

        where:
        costCenterCode  | name            | currencyIso | postBody
        "Rustic_Sample" | "Rustic Sample" | "USD"       | ["code": "Rustic_Sample", "name": "Rustic Sample", "unit": ["uid": "Rustic"],"currency": ["isocode": "USD"]]
    }

    def "B2B Admin tries to create a new cost center for different Unit: #costCenterCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to create a new cost center"
        def response = restClient.post(
            path: getBasePathWithSite() + COST_CENTERS_PATH,
            body: ["code": costCenterCode, "name": name, "unit": ["uid": unit], "currency": ["isocode": currencyIso]],
            contentType: JSON,
            requestContentType: JSON)

        then: "he gets an error"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_NOT_FOUND
            data.errors[0].type == "NotFoundError"
            data.errors[0].message == "Unit with code [Pronto} was not found"
        }

        when: "when querying the other unit as B2B Admin"
        authorizeCustomer(restClient, B2B_ADMIN_PRONTO)
        response = restClient.get(
            path: getBasePathWithSite() + COST_CENTERS_PATH + '/' + costCenterCode,
            contentType: JSON,
            requestContentType: JSON)

        then: "creation of cost center was not persisted"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_NOT_FOUND
        }

        where:
        costCenterCode    | name              | unit     | currencyIso
        "Pronto_Sample_2" | "Pronto Sample 2" | "Pronto" | "USD"
    }

    def "B2B Admin tries to create a new cost center with an already existing cost center code: #costCenterCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to create a new cost center with an already existing cost center code"
        def response = restClient.post(
                path: getBasePathWithSite() + COST_CENTERS_PATH,
                body: postBody,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets bad request response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_BAD_REQUEST
            data.errors[0].type == "ModelSavingError"
            data.errors[0].message == "The application has encountered an error"
        }

        where:
        costCenterCode  | postBody
        "Rustic_Global" | ["code": "Rustic_Global", "name": "Rustic Sample", "unit": ["uid": "Rustic"], "currency": ["isocode": "USD"]]
    }

    def "B2B Admin tries to create a new cost center with non-valid attributes: #descriptor"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to create a new cost center with non-valid attributes"
        def response = restClient.post(
                path: getBasePathWithSite() + COST_CENTERS_PATH,
                body: postBody,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets bad request response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_BAD_REQUEST
            data.errors.any { it.message == message && it.subject == subject && it.type == type }
        }

        where:
        descriptor               | message                   | subject            | type              | postBody
        "Null_Code"              | "This field is required." | "code"             | "ValidationError" | ["code": null, "name": "Rustic Sample", "unit": ["uid": "Rustic"], "currency": ["isocode": "USD"]]
        "Short_Code"             | "This field is required." | "code"             | "ValidationError" | ["code": "", "name": "Rustic Sample", "unit": ["uid": "Rustic"], "currency": ["isocode": "USD"]]
        "Long_Code"              | "This field is required." | "code"             | "ValidationError" | ["code": createTestString(256), "name": "Rustic Sample", "unit": ["uid": "Rustic"], "currency": ["isocode": "USD"]]
        "Null_Name"              | "This field is required." | "name"             | "ValidationError" | ["code": "Rustic_Sample", "name": null, "unit": ["uid": "Rustic"], "currency": ["isocode": "USD"]]
        "Short_Name"             | "This field is required." | "name"             | "ValidationError" | ["code": "Rustic_Sample", "name": "", "unit": ["uid": "Rustic"], "currency": ["isocode": "USD"]]
        "Long_Name"              | "This field is required." | "name"             | "ValidationError" | ["code": "Rustic_Sample", "name": createTestString(256), "unit": ["uid": "Rustic"], "currency": ["isocode": "USD"]]
        "Null_Unit"              | "This field is required." | "unit"             | "ValidationError" | ["code": "Rustic_Sample", "name": "Rustic Sample", "unit": null, "currency": ["isocode": "USD"]]
        "Null_Unit_Uid"          | "This field is required." | "unit.uid"         | "ValidationError" | ["code": "Rustic_Sample", "name": "Rustic Sample", "unit": ["uid": null], "currency": ["isocode": "USD"]]
        "Null_Currency"          | "This field is required." | "currency"         | "ValidationError" | ["code": "Rustic_Sample", "name": "Rustic Sample", "unit": ["uid": "Rustic"], "currency": null]
        "Null_Currency_Isocode"  | "This field is required." | "currency.isocode" | "ValidationError" | ["code": "Rustic_Sample", "name": "Rustic Sample", "unit": ["uid": "Rustic"], "currency": ["isocode": null]]
    }

    def "B2B Customer tries to create a new cost center: #scenario"() {
        given: "a registered and logged in customer without B2B Admin role"
        authorizeCustomer(restClient, B2B_CUSTOMER)

        when: "he requests to create a cost center"
        def response = restClient.post(
                path: getBasePathWithSite() + COST_CENTERS_PATH,
                body: postBody,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets unauthorized error"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_UNAUTHORIZED
            data.errors[0].type == "AccessDeniedError"
            data.errors[0].message == "Access is denied"
        }

        where:
        scenario         | postBody
        "Same Unit"      | ["code": "Custom_Retail", "name": "Custom Retail", "unit": ["uid": "Rustic"], "currency": ["isocode": "USD"]]
        "Different Unit" | ["code": "Custom_Retail", "name": "Custom Retail", "unit": ["uid": "Pronto"], "currency": ["isocode": "USD"]]
    }

    def "B2B #role gets a specific cost center: #costCenterCode"() {
        given: "a registered and logged in customer"
        authorizeCustomer(restClient, credentials)

        when: "he requests to get a specific cost center"
        def response = restClient.get(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode,
                query: [ "fields":"FULL"],
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets a specific cost center"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            compareCostCenters(data, costCenterCode, name, currencyIso, null)
            data.assignedBudgets != null
        }

        where:
        role       | credentials  | costCenterCode  | name            | currencyIso
        "Admin"    | B2B_ADMIN    | "Rustic_Global" | "Rustic Global" | "USD"
        "Admin"    | B2B_ADMIN    | "Rustic_Retail" | "Retail"        | "USD"
        "Customer" | B2B_CUSTOMER | "Rustic_Global" | "Rustic Global" | "USD"
        "Customer" | B2B_CUSTOMER | "Rustic_Retail" | "Retail"        | "USD"
    }

    def "B2B Customer tries to get a non-existing cost center: #costCenterCode"() {
        given: "a registered and logged in customer without B2B Admin role"
        authorizeCustomer(restClient, B2B_CUSTOMER)

        when: "he requests to get a non-existing cost center"
        def response = restClient.get(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets not found response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_NOT_FOUND
            data.errors[0].type == "NotFoundError"
            data.errors[0].message == "Cost center with code [Non_Existing_Cost_Center] was not found"
        }

        where:
        costCenterCode << ["Non_Existing_Cost_Center"]
    }

    def "B2B Admin adds a budget to a specific cost center with cost center code: #costCenterCode and budget code: #budgetCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to add a budget to a specific cost center"
        def response = restClient.post(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH,
                contentType: JSON,
                requestContentType: JSON,
                query: [
                        'budgetCode': budgetCode
                ])

        then: "he gets ok response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            compareB2BSelectionData(data, budgetCode, true)
        }

        where:
        costCenterCode  | budgetCode
        "Rustic_Global" | "Monthly_20K_USD"
    }

    def "B2B Admin tries to add a budget to a specific cost center where it is already present with cost center code: #costCenterCode and budget code: #budgetCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to add a budget to a specific cost center where it is already present"
        def response = restClient.post(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH,
                contentType: JSON,
                requestContentType: JSON,
                query: [
                        'budgetCode': budgetCode
                ])

        then: "he gets ok response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            compareB2BSelectionData(data, budgetCode, true)
        }

        where:
        costCenterCode  | budgetCode
        "Rustic_Global" | "Monthly_50K_USD"
    }

    def "B2B Admin tries to add a budget to a non-existent cost center with cost center code: #costCenterCode and budget code: #budgetCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to add a budget to a non-existent cost center"
        def response = restClient.post(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH,
                contentType: JSON,
                requestContentType: JSON,
                query: [
                        'budgetCode': budgetCode
                ])

        then: "he gets not found response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_NOT_FOUND
            data.errors[0].type == "NotFoundError"
            data.errors[0].message == "Cost center with code [Non_Existent] was not found"
        }

        where:
        costCenterCode | budgetCode
        "Non_Existent" | "Monthly_20K_USD"
    }

    def "B2B Admin tries to add a non-existent budget to a specific cost center with cost center code: #costCenterCode and budget code: #budgetCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to add a non-existent budget to a specific cost center"
        def response = restClient.post(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH,
                contentType: JSON,
                requestContentType: JSON,
                query: [
                        'budgetCode': budgetCode
                ])

        then: "he gets not found response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_NOT_FOUND
            data.errors[0].type == "NotFoundError"
            data.errors[0].message == "Budget with uid [Non_Existent] was not found"
        }

        where:
        costCenterCode  | budgetCode
        "Rustic_Global" | "Non_Existent"
    }

    def "B2B Customer tries to add a budget to a specific cost center with cost center code: #costCenterCode and budget code: #budgetCode"() {
        given: "a registered and logged in customer without B2B Admin role"
        authorizeCustomer(restClient, B2B_CUSTOMER)

        when: "he requests to add a budget to a specific cost center"
        def response = restClient.post(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH,
                contentType: JSON,
                requestContentType: JSON,
                query: [
                        'budgetCode': budgetCode
                ])

        then: "he gets unauthorized error"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_UNAUTHORIZED
            data.errors[0].type == "AccessDeniedError"
            data.errors[0].message == "Access is denied"
        }

        where:
        costCenterCode  | budgetCode
        "Rustic_Global" | "Monthly_20K_USD"
    }

    def "B2B Admin removes a budget from a specific cost center with cost center code: #costCenterCode and budget code: #budgetCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to remove a budget from a specific cost center"
        def response = restClient.delete(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH + "/" + budgetCode,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets ok response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            compareB2BSelectionData(data, budgetCode, false)
        }

        where:
        costCenterCode  | budgetCode
        "Rustic_Global" | "Monthly_50K_USD"
    }

    def "B2B Admin tries to remove a budget from a specific cost center where it is not present with cost center code: #costCenterCode and budget code: #budgetCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to remove a budget from a specific cost center where it is not present"
        def response = restClient.delete(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH + "/" + budgetCode,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets ok response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            compareB2BSelectionData(data, budgetCode, false)
        }

        where:
        costCenterCode  | budgetCode
        "Rustic_Global" | "Monthly_20K_USD"
    }

    def "B2B Admin tries to remove a budget from a non-existent cost center with cost center code: #costCenterCode and budget code: #budgetCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to remove a budget from a non-existent cost center"
        def response = restClient.delete(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH + "/" + budgetCode,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets not found response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_NOT_FOUND
        }

        where:
        costCenterCode | budgetCode
        "Non_Existent" | "Monthly_20K_USD"
    }

    def "B2B Admin tries to remove a non-existent budget from a specific cost center with cost center code: #costCenterCode and budget code: #budgetCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to remove a non-existent budget from a specific cost center"
        def response = restClient.delete(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH + "/" + budgetCode,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets not found response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_NOT_FOUND
            data.errors[0].type == "NotFoundError"
            data.errors[0].message == "Budget with uid [Non_Existent] was not found"
        }

        where:
        costCenterCode  | budgetCode
        "Rustic_Global" | "Non_Existent"
    }

    def "B2B Customer tries to remove a budget from a specific cost center with cost center code: #costCenterCode and budget code: #budgetCode"() {
        given: "a registered and logged in customer without B2B Admin role"
        authorizeCustomer(restClient, B2B_CUSTOMER)

        when: "he requests to remove a budget from a specific cost center"
        def response = restClient.delete(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH + "/" + budgetCode,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets unauthorized error"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_UNAUTHORIZED
            data.errors[0].type == "AccessDeniedError"
            data.errors[0].message == "Access is denied"
        }

        where:
        costCenterCode  | budgetCode
        "Rustic_Global" | "Monthly_20K_USD"
    }

    def "B2B Admin tries to edit a non-existing cost center: #costCenterCodeInRequest"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to edit a non-existing cost center"
        def response = restClient.patch(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCodeInRequest,
                body: patchBody,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets not found response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_NOT_FOUND
            data.errors[0].type == "NotFoundError"
            data.errors[0].message == "Cost center with code [Non_Existing_Cost_Center] was not found"
        }

        where:
        costCenterCodeInRequest    | patchBody
        "Non_Existing_Cost_Center" | ["code": "Non_Existing_Cost_Center_2"]
    }

    def "B2B Admin tries to edit a cost center [#costCenterCodeInRequest] with non-valid attributes: #descriptor"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to edit a cost center with non-valid attributes"
        def response = restClient.patch(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCodeInRequest,
                body: patchBody,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets bad request response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_BAD_REQUEST
            data.errors.any { it.message == message && it.subject == subject && it.type == type }
        }

        where:
        costCenterCodeInRequest | descriptor               | message                   | subject | type              | patchBody
        "Rustic_Global"         | "Short_Code"             | "This field is required." | "code"  | "ValidationError" | ["code": "", "name": "Rustic Sample", "unit": ["uid": "Rustic"],"currency": ["isocode": "USD"]]
        "Rustic_Global"         | "Long_Code"              | "This field is required." | "code"  | "ValidationError" | ["code": createTestString(256), "name": "Rustic Sample", "unit": ["uid": "Rustic"],"currency": ["isocode": "USD"]]
        "Rustic_Global"         | "Short_Name"             | "This field is required." | "name"  | "ValidationError" | ["code": "Rustic_Sample", "name": "", "unit": ["uid": "Rustic"],"currency": ["isocode": "USD"]]
        "Rustic_Global"         | "Long_Name"              | "This field is required." | "name"  | "ValidationError" | ["code": "Rustic_Sample", "name": createTestString(256), "unit": ["uid": "Rustic"],"currency": ["isocode": "USD"]]
    }

    def "B2B Customer tries to edit a specific cost center: #costCenterCodeInRequest"() {
        given: "a registered and logged in customer without B2B Admin role"
        authorizeCustomer(restClient, B2B_CUSTOMER)

        when: "he requests to edit a specific cost center"
        def response = restClient.patch(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCodeInRequest,
                body: patchBody,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets unauthorized error"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_UNAUTHORIZED
            data.errors[0].type == "AccessDeniedError"
            data.errors[0].message == "Access is denied"
        }

        where:
        costCenterCodeInRequest | patchBody
        "Rustic_Global"         | '{"code": "Rustic_Global_2", "name": "Rustic Global 2", "currency": {"isocode": "EUR"}}'
        "Rustic_Retail"         | '{"code": "Rustic_Retail_2", "name": "Retail 2", "currency": {"isocode": "EUR"}}'
    }

    def "B2B Admin gets budgets for a specific cost center: #costCenterCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to get budgets for a specific cost center"
        def response = restClient.get(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH,
                contentType: format,
                requestContentType: URLENC)

        then: "he/she gets budgets"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            data.budgets.size() == NUMBER_OF_BUDGETS
            data.sorts.size() > 0
            data.pagination.pageSize == PAGE_SIZE
            data.pagination.currentPage == 0
            data.pagination.totalResults.toInteger() == NUMBER_OF_BUDGETS
        }

        where:
        costCenterCode  | format
        "Rustic_Retail" | JSON
    }

    def "B2B Admin tries to get budgets for a non-existent cost center with cost center code: #costCenterCode. #costCenterCode"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests budgets for a specific cost center where it is already present."
        def response = restClient.get(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets not found response"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_NOT_FOUND
            data.errors[0].type == "NotFoundError"
            data.errors[0].message == "Cost center with code [Non_Existent] was not found"
        }

        where:
        costCenterCode << ["Non_Existent"]
    }

    def "B2B Customer tries to get budgets for a specific cost center: #costCenterCode"() {
        given: "a registered and logged in B2B customer without B2B Admin role"
        authorizeCustomer(restClient, B2B_CUSTOMER)

        when: "he requests to get budgets"
        def response = restClient.get(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCode + "/" + BUDGETS_PATH,
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets unauthorized error"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_UNAUTHORIZED
            data.errors[0].type == "AccessDeniedError"
            data.errors[0].message == "Access is denied"
        }

        where:
        costCenterCode << ["Rustic_Global"]
    }

    def "B2B Admin enables/disables a specific costCenter: #costCenterCodeInRequest"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to enable/disable a specific cost center by editing the permission"
        def response = restClient.patch(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCodeInRequest,
                body: patchBody,
                contentType: JSON,
                requestContentType: JSON)

        then: "activation/deactivation is successfully applied"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            compareCostCenters(data, costCenterCode, name, currencyIso, active)
            status == SC_OK
        }

        def updatedInfo = getCostCenter(restClient, costCenterCode)

        and: "activation/deactivation is persistent"
        with(updatedInfo) {
            compareCostCenters(data, costCenterCode, name, currencyIso, active)
        }

        where:
        costCenterCodeInRequest | costCenterCode  | name            | currencyIso | active       | patchBody
        "Rustic_Global"         | "Rustic_Global" | "Rustic Global" | "USD"       | "false"      | ["activeFlag": false]
        "Rustic_Retail"         | "Rustic_Retail" | "Retail"        | "USD"       | "true"       | ["activeFlag": true]
    }

    def "B2B Admin edits a specific cost center: #costCenterCodeInRequest"() {
        given: "a registered and logged in customer with B2B Admin role"
        authorizeCustomer(restClient, B2B_ADMIN)

        when: "he requests to edit a specific cost center"
        def response = restClient.patch(
                path: getBasePathWithSite() + COST_CENTERS_PATH + "/" + costCenterCodeInRequest,
                body: patchBody,
                contentType: JSON,
                requestContentType: JSON)

        then: "his changes are successfully applied"
        with(response) {
            compareCostCenters(data, costCenterCode, name, currencyIso, null)
            status == SC_OK
        }

        def updatedInfo = getCostCenter(restClient, costCenterCode)

        and: "new values are persistent"
        with(updatedInfo) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            compareCostCenters(data, costCenterCode, name, currencyIso, null)
        }

        where:
        costCenterCodeInRequest | costCenterCode    | name              | currencyIso | patchBody
        "Rustic_Global"         | "Rustic_Global_2" | "Rustic Global 2" | "EUR"       | ["code": "Rustic_Global_2", "name": "Rustic Global 2", "currency": ["isocode": "EUR"]]
        "Rustic_Retail"         | "Rustic_Retail_2" | "Retail 2"        | "EUR"       | ["code": "Rustic_Retail_2", "name": "Retail 2", "currency": ["isocode": "EUR"]]
    }

    protected static String createTestString(length) {
        StringBuilder stringBuilder = new StringBuilder("")
        for (int i = 0; i < length; i++) {
            stringBuilder.append("1")
        }
        return stringBuilder.toString()
    }

    protected static void compareCostCenters(responseData, costCenterCode, name, currencyIso, active) {
        assert responseData.code == costCenterCode
        assert responseData.name == name
        assert responseData.currency.isocode == currencyIso
        if (active != null) {
            assert responseData.active == active
        }
    }

    protected getCostCenter(RESTClient client, costCenterCode) {
        def response = client.get(
                path: getBasePathWithSite() + COST_CENTERS_PATH + '/' + costCenterCode,
                contentType: JSON,
                requestContentType: JSON)
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
        }
        return response
    }

    protected static void compareB2BSelectionData(Object selectionData, String code, boolean selected) {
        assert selectionData.id == code
        assert selectionData.selected == selected
    }
}
