/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices.util

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

import java.lang.reflect.Field

@UnitTest
class HttpStatusUnitTest extends JUnitPlatformSpecification {
    @Test
    @Unroll
    def "isSuccessful with #code - #desc"() {
        given:
        def httpStatus = HttpStatus.valueOf code

        expect:
        httpStatus.successful == expected

        where:
        code | desc                            | expected
        // Informational
        103  | 'Early Hints'                   | false
        // Success
        200  | 'OK'                            | true
        201  | 'Created'                       | true
        202  | 'Accepted'                      | true
        203  | 'Non-Authoritative Information' | true
        204  | 'No Content'                    | true
        205  | 'Reset Content'                 | true
        206  | 'Partial Content'               | true
        207  | 'Multi-Status'                  | true
        208  | 'Already Reported'              | true
        226  | 'IM Used'                       | true
        // Redirects
        300  | 'Multiple Choices'              | false
        // User Error
        400  | 'Bad Request'                   | false
        // Server Error
        500  | 'Internal Server Error'         | false
    }

    @Test
    @Unroll
    def "isError with #code - #desc"() {
        given:
        def httpStatus = HttpStatus.valueOf code

        expect:
        httpStatus.error == expected

        where:
        code | desc                              | expected
        // Informational
        103  | 'Early Hints'                     | false
        // Success
        200  | 'OK'                              | false
        // Redirects
        308  | 'Permanent Redirect'              | false
        // User Error
        400  | 'Bad Request'                     | true
        451  | 'Unavailable for Legal Reasons'   | true
        // Server Error
        500  | 'Internal Server Error'           | true
        511  | 'Network Authentication Required' | true
    }

    @Test
    def "Status is instance equivalent"() {
        expect:
        HttpStatus.valueOf(200).is HttpStatus.valueOf(200)
        !HttpStatus.valueOf(200).is(HttpStatus.valueOf(300))
    }

    @Test
    def "equals with null object"() {
        given:
        def httpStatus = HttpStatus.valueOf(200)
        def nullHttpStatus = null

        expect:
        !httpStatus.equals(nullHttpStatus)
    }

    @Test
    def "equals with different object"() {
        given:
        def httpStatus = HttpStatus.valueOf(200)

        expect:
        !httpStatus.equals(new Object())
    }

    @Test
    def "equals with same object"() {
        given:
        def httpStatus = HttpStatus.valueOf(200)

        expect:
        httpStatus.equals(httpStatus)
    }

    @Test
    def "equals with similar code"() {
        given:
        def httpStatus = HttpStatus.valueOf(200)
        def httpStatus2 = HttpStatus.valueOf(200)

        expect:
        httpStatus.equals(httpStatus2)
    }


    @Test
    def "equals with different code"() {
        given:
        def httpStatus = HttpStatus.valueOf(200)
        def httpStatus2 = HttpStatus.valueOf(300)

        expect:
        !httpStatus.equals(httpStatus2)
    }

    @Test
    def "hash code"() {
        given:
        def httpStatus = HttpStatus.valueOf(200)

        expect:
        httpStatus.hashCode() == 200
    }

    @Test
	def "to string"()
	{
		given:
		def httpStatus = HttpStatus.valueOf(2000)
		setInternalState(httpStatus, "name", "My Name")

        expect:
        httpStatus.toString() == "HttpStatus{2000 - My Name}"
  	}

	private void setInternalState(Object target, String field, Object value) {
        Class<?> c = target.getClass();
        try {
            Field f = getFieldFromHierarchy(c, field);  // Checks superclasses.
            f.setAccessible(true)
            f.set(target, value)
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to set internal state on a private field. [...]", e);
        }
    }

	private Field getFieldFromHierarchy(Class c, String fieldName) {
		if (c == null) {
			return null
		}

		for (Field f : c.declaredFields) {
			if (f.name == fieldName) {
				return f
			}
		}
		return getFieldFromHierarchy(c.superclass, fieldName)
	}
}
