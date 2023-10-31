/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.util


import de.hybris.platform.catalog.model.classification.ClassificationSystemModel
import groovy.transform.EqualsAndHashCode
import org.junit.rules.ExternalResource

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx

@EqualsAndHashCode(includes = 'id')
class ClassificationSystemBuilder extends ExternalResource {
	static final String ID = 'testClassificationSystem'
	static final String NAME = ID

    final Set<String> createdSystemIds = []
	String id
	String name

	static ClassificationSystemBuilder classificationSystem() {
		new ClassificationSystemBuilder()
	}

	ClassificationSystemBuilder withId(String id) {
		tap { this.id = id }
	}

	ClassificationSystemBuilder withName(String name) {
		tap { this.name = name }
	}

	ClassificationSystemModel build() {
        def id = deriveId()
        createdSystemIds << id
		importImpEx(
				'INSERT_UPDATE ClassificationSystem; id[unique = true]; name[lang = en]',
				"; $id ; ${deriveName()}"
		)
		getModel(id)
	}

	private String deriveId() {
		id ?: ID
	}

	private String deriveName() {
		name ?: NAME
	}

	private static ClassificationSystemModel getModel(String id) {
		IntegrationTestUtil.findAny(ClassificationSystemModel, { it.id == id }).orElse(null)
	}

    void cleanup() {
        createdSystemIds.each { id ->
            IntegrationTestUtil.removeSafely(ClassificationSystemModel) { it.id == id }
        }
        createdSystemIds.clear()
    }

    @Override
    protected void before() throws Throwable {
        build()
    }

    @Override
    protected void after() {
        cleanup()
    }
}
