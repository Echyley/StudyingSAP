/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.support.util

import org.apache.commons.lang3.ClassUtils

/**
 * Assertion utilities.
 */
class ObjectUtils {

	private ObjectUtils() {
		// utility class
	}

	/**
	 * Checks if an object is null or empty.
	 *
	 * @param obj
	 *  - the object to check
	 *
	 * @return <code>true</code> if the object is empty, <code>false</code> otherwise
	 */
	static final boolean isEmpty(obj) {
		if (obj == null) {
			return true
		}

		def objMetaclass = obj.getMetaClass()

		if (objMetaclass.respondsTo(obj, 'isEmpty', null)) {
			return obj.isEmpty()
		}

		if (objMetaclass.respondsTo(obj, 'size', null)) {
			return obj.size() == 0
		}

		throw new UnsupportedOperationException()
	}

	/**
	 * Checks if an object is not null and not empty.
	 *
	 * @param obj
	 *  - the object to check
	 *
	 * @return <code>true</code> if the object is empty, <code>false</code> otherwise
	 */
	static final boolean isNotEmpty(obj) {
		return !isEmpty(obj)
	}

	/**
	 * Checks if a container object contains the given expected object. The following rules are applied:
	 *	<ul>
	 *		<li>Collections are expected to be of the same size, elements are compared using the general rules of this method (called in a recursive way)</li>
	 *		<li>Maps can be a subset of the container map, keys should be equal but values are compared using the general rules of this method (called in a recursive way)</li>
	 *		<li>For all other object types, general object equality applies</li>
	 *  </ul>
	 *
	 * @param containerObj
	 *  - the container object
	 * @param expectedObj
	 *  - the expected object
	 * @param useAssertions
	 *  - whether to use assertions or not
	 *
	 * @return <code>true</code> if the container object contains the given object, <code>false</code> otherwise
	 */
	static final boolean matchContains(def containerObj, def expectedObj, boolean useAssertions = true) {
		if (containerObj == null && expectedObj == null) {
			return true
		} else if (containerObj == null || expectedObj == null) {
			return false
		} else if (containerObj instanceof Collection && expectedObj instanceof Collection) {
			Comparator comparator = { a, b ->
				matchContains(a, b) ? 0 : -1
			} as Comparator
			return Arrays.equals(((Collection) containerObj).toArray(), ((Collection) expectedObj).toArray(), comparator)
		} else if (containerObj instanceof Map && expectedObj instanceof Map) {
			for (def key : expectedObj.keySet()) {
				if (!matchContains(containerObj.get(key), expectedObj.get(key))) {
					return false
				}
			}

			return true
		} else if (!ClassUtils.isPrimitiveOrWrapper(containerObj.getClass()) && expectedObj instanceof Map) {
			def containerProperties = containerObj.properties

			for (def key : expectedObj.keySet()) {
				if (!matchContains(containerProperties.get(key), expectedObj.get(key))) {
					return false
				}
			}

			return true
		} else {
			def expectedObjAsType = expectedObj.asType(containerObj.class)

			if (useAssertions) {
				assert containerObj == expectedObjAsType
			}

			return containerObj == expectedObjAsType
		}
	}
}
