/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel
import de.hybris.platform.catalog.model.classification.ClassificationClassModel
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel
import groovy.transform.Canonical
import org.junit.rules.ExternalResource

import static de.hybris.platform.integrationservices.util.ClassificationSystemVersionBuilder.classificationSystemVersion

/**
 * A helper class for setting up classifications in test scenarios
 */
class ClassificationBuilder extends ExternalResource {
    private final Set<ClassificationClass> createdClasses
    private final ClassificationSystemVersionBuilder classificationVersion
    private final Collection<Attribute> attributes
    private final Set<ClassificationAttributeUnitBuilder> units
    private String classificationClass

    private ClassificationBuilder() {
        attributes = []
        createdClasses = []
        units = []
        classificationVersion = classificationSystemVersion()
    }

    static ClassificationBuilder classification() {
        new ClassificationBuilder()
    }

    ClassificationBuilder withSystem(String system) {
        tap { classificationVersion.withClassificationSystem(system) }
    }

    ClassificationBuilder withVersion(String version) {
        tap { classificationVersion.withVersion(version) }
    }

    ClassificationBuilder withClassificationClass(String className) {
        tap { classificationClass = className }
    }

    ClassificationBuilder withAttribute(Attribute attribute) {
        tap { attributes.add attribute }
    }

    ClassificationBuilder withUnit(ClassificationAttributeUnitBuilder builder) {
        tap { units << builder }
    }

    void setup() {
        def sysVersion = classificationVersion.build()
        units.each {
            it.withSystemVersion(classificationVersion).build()
        }
        if (classificationClass) {
            def classClass = new ClassificationClass(version: sysVersion, className: classificationClass)
            createdClasses << classClass.build()
        }
        if (attributes) {
            attributes.each { it.withClassificationSystem(sysVersion).withClass(classificationClass) }
            Attribute.buildClassificationAttributes(attributes)
            Attribute.buildAttributeValues(attributes)
            Attribute.buildClassAttributeAssignments(attributes)
        }
    }

    @Override
    protected void before() {
        setup()
    }

    @Override
    void after() {
        cleanup()
    }

    void cleanup() {
        attributes.each { it.cleanup() }
        attributes.clear()
        createdClasses.each { it.cleanup() }
        createdClasses.clear()
        units.each { it.cleanup() }
        units.clear()
        classificationVersion.cleanup()
    }

    static Attribute attribute() {
        new Attribute()
    }

    static class Attribute {
        private ClassificationSystemVersionModel systemVersion
        private String classificationClass
        private String attributeName
        private String attributeType
        private String attributeReferencedType
        private boolean mandatory
        private boolean multiValue
        private boolean localized
        private List<String> enumValues = []
        private boolean range
        private String unit

        Attribute withClassificationSystem(ClassificationSystemVersionModel version) {
            tap { systemVersion = version }
        }

        Attribute withClass(String code) {
            tap { classificationClass = code }
        }

        Attribute withName(String name) {
            tap { attributeName = name }
        }

        Attribute number() {
            attributeType('number')
        }

        Attribute string() {
            attributeType('string')
        }

        Attribute date() {
            attributeType('date')
        }

        Attribute references(String type) {
            attributeType('reference', type)
        }

        Attribute valueList(List<String> values) {
            attributeType('enum', '', values)
        }

        Attribute multiValue() {
            tap { multiValue = true }
        }

        Attribute mandatory() {
            tap { mandatory = true }
        }

        Attribute localized() {
            tap { localized = true }
        }

        Attribute range() {
            tap { range = true }
        }

        Attribute withUnit(String code) {
            tap { unit = code }
        }

        private Attribute attributeType(String type, String refType = '', List values = []) {
            tap {
                attributeType = type
                attributeReferencedType = refType
                enumValues = values
            }
        }

        static void buildAttributeValues(Collection<Attribute> attributes) {
            def valueLines = attributes.collectMany({ it.valuesImpexLines() })
            if (valueLines) {
                def impex = ['INSERT_UPDATE ClassificationAttributeValue; systemVersion[unique = true]; code[unique = true]']
                impex.addAll valueLines
                IntegrationTestUtil.importImpEx impex
            }
        }

        private def valuesImpexLines() {
            enumValues
                    ? enumValues.collect { "                                          ; $systemVersion.pk; $it" }
                    : []
        }

        static void buildClassificationAttributes(Collection<Attribute> attributes) {
            def impex = ['INSERT_UPDATE ClassificationAttribute; code[unique = true]; systemVersion[unique = true]']
            impex.addAll attributes.collect({ it.attributeImpexLine() })
            IntegrationTestUtil.importImpEx impex
        }

        private def attributeImpexLine() {
            "                                     ; $attributeName ; $systemVersion.pk"
        }

        private static void buildClassAttributeAssignments(Collection<Attribute> attributes) {
            def impex = [
                    '$class=classificationClass(catalogVersion, code)',
                    '$attribute=classificationAttribute(systemVersion, code)',
                    'INSERT_UPDATE ClassAttributeAssignment; $class[unique = true]; $attribute[unique = true]; attributeType(code); referenceType(code); mandatory; multiValued; localized; range; unit(systemVersion, code); attributeValues(code, systemVersion)']
            impex.addAll attributes.collect({ it.assigmentImpexLine() })
            IntegrationTestUtil.importImpEx impex
        }

        private def assigmentImpexLine() {
            "; $systemVersion.pk:$classificationClass; $systemVersion.pk:$attributeName; $attributeType; $attributeReferencedType; $mandatory; $multiValue; $localized; $range; ${deriveUnit()}; ${flattenValues()}"
        }

        private def flattenValues() {
            enumValues.collect({ "$it:$systemVersion.pk" }).join(',')
        }

        private String deriveUnit() {
            unit ? "$systemVersion.pk:$unit" : ''
        }

        void cleanup() {
            IntegrationTestUtil.remove(ClassAttributeAssignmentModel) { matches(it) }
            IntegrationTestUtil.remove(ClassificationAttributeModel) { matches(it) }
            IntegrationTestUtil.remove(ClassificationAttributeValueModel) { matches(it) }
        }

        boolean matches(ClassAttributeAssignmentModel model) {
            (matches(model.classificationAttribute) && classificationClass == model.classificationClass.code
                    && model.classificationClass.catalogVersion == systemVersion)
        }

        boolean matches(ClassificationAttributeModel model) {
            model.code == attributeName && model.systemVersion == systemVersion
        }

        boolean matches(ClassificationAttributeValueModel model) {
            model.systemVersion == systemVersion
        }
    }


    @Canonical
    private static class ClassificationClass {
        private ClassificationSystemVersionModel version
        private String className

        boolean matches(ClassificationClassModel m) {
            m.code == className && m.catalogVersion == version
        }

        ClassificationClass build() {
            IntegrationTestUtil.importImpEx(
                    'INSERT_UPDATE ClassificationClass; code[unique = true]; catalogVersion[unique = true]',
                    "                                 ; $className         ; $version.pk")
            this
        }

        void cleanup() {
            IntegrationTestUtil.remove(ClassificationClassModel) { matches(it) }
        }
    }

}

