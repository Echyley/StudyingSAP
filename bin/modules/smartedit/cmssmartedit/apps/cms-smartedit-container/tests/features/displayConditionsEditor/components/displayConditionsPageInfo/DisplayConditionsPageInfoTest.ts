/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DisplayConditionsPageInfoComponent } from 'cmssmarteditcontainer/components/pages/displayConditions/displayConditionsPageInfo/DisplayConditionsPageInfoComponent';

describe('DisplayConditionsPageInfoComponent', () => {
    let component: DisplayConditionsPageInfoComponent;

    beforeEach(() => {
        component = new DisplayConditionsPageInfoComponent();
    });

    describe('getPageDisplayConditionI18nKey', () => {
        it('GIVEN isPrimary is true THEN it should return translations for primary', () => {
            component.isPrimary = true;

            expect(component.getPageDisplayConditionI18nKey()).toEqual(
                'se.cms.display.conditions.primary.id'
            );
        });

        it('GIVEN isPrimary is false THEN it should return translations for variation', () => {
            component.isPrimary = false;

            expect(component.getPageDisplayConditionI18nKey()).toEqual(
                'se.cms.display.conditions.variation.id'
            );
        });
    });

    describe('getPageDisplayConditionDescriptionI18nKey', () => {
        it('GIVEN isPrimary is true THEN it should return translations for primary', () => {
            component.isPrimary = true;

            expect(component.getPageDisplayConditionDescriptionI18nKey()).toEqual(
                'se.cms.display.conditions.primary.description'
            );
        });

        it('GIVEN isPrimary is false THEN it should return translations for variation', () => {
            component.isPrimary = false;

            expect(component.getPageDisplayConditionDescriptionI18nKey()).toEqual(
                'se.cms.display.conditions.variation.description'
            );
        });
    });
});
