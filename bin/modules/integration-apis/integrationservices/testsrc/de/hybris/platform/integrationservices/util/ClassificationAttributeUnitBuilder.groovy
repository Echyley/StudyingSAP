/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.util

import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import org.junit.rules.ExternalResource

import static de.hybris.platform.integrationservices.util.ClassificationSystemVersionBuilder.classificationSystemVersion
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx

@EqualsAndHashCode(includes = ['systemVersion', 'code'])
class ClassificationAttributeUnitBuilder extends ExternalResource {

	static final def CODE = 'testClassificationAttributeUnit'
	static final def CLASSIFICATION_SYSTEM_VERSION = classificationSystemVersion()
	static final def UNIT_TYPE = 'testClassificationAttributeUnitType'
	static final def SYMBOL = 'testClassificationAttributeUnitSymbol'

    final Set<ClassificationSystemVersionBuilder> createdSystemVersions = []
    final Set<Key> createdUnits = []
	String code
	ClassificationSystemVersionBuilder systemVersion
	String unitType
	String symbol

	static ClassificationAttributeUnitBuilder classificationAttributeUnit() {
		new ClassificationAttributeUnitBuilder()
	}

	ClassificationAttributeUnitBuilder withCode(String code) {
		this.code = code
		this
	}

	ClassificationAttributeUnitBuilder withSystemVersion(ClassificationSystemVersionBuilder builder) {
		tap { systemVersion = builder }
	}

	ClassificationAttributeUnitBuilder withUnitType(String unitType) {
		this.unitType = unitType
		this
	}

	ClassificationAttributeUnitBuilder withSymbol(String symbol) {
		this.symbol = symbol
		this
	}

	ClassificationAttributeUnitModel build() {
        def code = deriveCode()
        def sysVersion = deriveSystemVersion()
        def type = deriveUnitType()
        def key = new Key()
        createdUnits << key
		importImpEx(
			'INSERT_UPDATE ClassificationAttributeUnit; code[unique = true]; unitType[unique = true]; systemVersion[unique = true]; symbol',
			"; $code; $type; $sysVersion.pk; ${deriveSymbol()}"
		)
		getModel(key)
	}

	private String deriveCode() {
		code ?: CODE
	}

	private ClassificationSystemVersionModel deriveSystemVersion() {
		def builder = systemVersion ?: CLASSIFICATION_SYSTEM_VERSION
        createdSystemVersions << builder
        builder.build()
	}

	private String deriveUnitType() {
		unitType ?: UNIT_TYPE
	}

	private String deriveSymbol() {
		symbol ?: SYMBOL
	}

	private static ClassificationAttributeUnitModel getModel(Key key) {
		IntegrationTestUtil.findAny(ClassificationAttributeUnitModel, { key.matches(it) }).orElse(null)
	}

    void cleanup() {
        createdUnits.each {key ->
            IntegrationTestUtil.remove(ClassificationAttributeUnitModel, { key.matches(it) })
        }
        createdUnits.clear()

        createdSystemVersions.each { it.cleanup() }
        createdSystemVersions.clear()
    }

    @Override
    protected void before() throws Throwable {
        build()
    }

    @Override
    protected void after() {
        cleanup()
    }

    @Canonical
    private final static class Key {
        ClassificationSystemVersionModel version
        String unit
        String type

        boolean matches(ClassificationAttributeUnitModel model) {
            model.code == unit && model.unitType == type && model.systemVersion == version
        }
    }
}
