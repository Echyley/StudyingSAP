/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.util

import de.hybris.platform.catalog.model.classification.ClassificationSystemModel
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import org.junit.rules.ExternalResource

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx

@EqualsAndHashCode(includes = ['classificationSystem', 'version'])
class ClassificationSystemVersionBuilder extends ExternalResource {

	static final def CLASSIFICATION_SYSTEM = ClassificationSystemBuilder.classificationSystem()
	static final def VERSION = 'testClassificationSystemVersion'

    final Set<Key> createdVersions = []
    final Set<ClassificationSystemBuilder> createdSystems = []
	ClassificationSystemBuilder classificationSystem
	String version
	boolean active

	static ClassificationSystemVersionBuilder classificationSystemVersion() {
		new ClassificationSystemVersionBuilder()
	}

	ClassificationSystemVersionBuilder withVersion(String version) {
		tap { this.version = version }
	}

    ClassificationSystemVersionBuilder withClassificationSystem(String classificationSystem) {
        withClassificationSystem ClassificationSystemBuilder.classificationSystem().withId(classificationSystem)
    }

	ClassificationSystemVersionBuilder withClassificationSystem(ClassificationSystemBuilder builder) {
		tap {classificationSystem = builder }
	}

	ClassificationSystemVersionBuilder withActive(boolean active) {
		tap { this.active = active }
	}

	ClassificationSystemVersionModel build() {
        def system = deriveClassificationSystem()
        def ver = deriveVersion()
        createdVersions << new Key(system.id, ver)
		importImpEx(
			'INSERT_UPDATE ClassificationSystemVersion; catalog[unique = true]; version[unique = true]; active',
			"; $system.pk ; $ver ; $active"
		)
		getModel(system, ver)
	}

	private def deriveClassificationSystem() {
		def builder = classificationSystem ?: CLASSIFICATION_SYSTEM
        createdSystems << builder
        builder.build()
	}

	private String deriveVersion() {
		version ?: VERSION
	}

	private static ClassificationSystemVersionModel getModel(ClassificationSystemModel system, String version) {
		IntegrationTestUtil.findAny(ClassificationSystemVersionModel, {
            it.version == version && it.catalog == system }
        ).orElse(null)
	}

    void cleanup() {
        createdVersions.each { key ->
            IntegrationTestUtil.removeSafely(ClassificationSystemVersionModel) {
                it.version == key.version && it.catalog.id == key.system
            }
        }
        createdVersions.clear()

        createdSystems.each { it.cleanup() }
        createdSystems.clear()
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
    @Immutable
    private final static class Key {
        String system
        String version
    }
}
