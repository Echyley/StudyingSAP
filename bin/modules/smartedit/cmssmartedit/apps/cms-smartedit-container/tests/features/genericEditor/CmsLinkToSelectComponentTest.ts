/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { CmsLinkToSelectComponent } from 'cmssmarteditcontainer/components/genericEditor';
import {
    CmsLinkToSelectOption,
    LinkToOption
} from 'cmssmarteditcontainer/components/genericEditor/cmsLinkToSelect/types';
import { GenericEditorWidgetData, SystemEventService } from 'smarteditcommons';

describe('CmsLinkToSelectComponent', () => {
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);
    const qualifier = 'qualifier';

    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let widgetData: GenericEditorWidgetData<CmsLinkToSelectOption>;
    let model: CmsLinkToSelectOption;

    let component: CmsLinkToSelectComponent;
    beforeEach(() => {
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe',
            'publishAsync'
        ]);

        model = {
            linkTo: null,
            currentSelectedOptionValue: null,
            external: false,
            productCatalog: null,
            url: null,
            product: null,
            contentPage: null,
            category: null,
            linkToggle: {
                urlLink: null
            }
        };
        widgetData = {
            id: 'id',
            field: null,
            model,
            qualifier,
            isFieldDisabled: null
        };

        component = new CmsLinkToSelectComponent(cdr, systemEventService, widgetData);
    });

    it('GIVEN option model is set an option WHEN initialized THEN it sets the option on "linkTo" property', () => {
        model.url = 'test';

        component.ngOnInit();

        expect(component.optionModel.linkTo).toBe('external');
    });

    it('GIVEN option model is set an option WHEN initialized THEN it sets the option on "linkTo" property', () => {
        model.linkToggle.urlLink = 'test';

        component.ngOnInit();

        expect(component.optionModel.linkTo).toBe('external');
    });

    it('GIVEN option model is not set an option WHEN initialized THEN it does not set the option on "linkTo" property', () => {
        component.ngOnInit();

        expect(component.optionModel.linkTo).toBe(null);
    });

    describe('GIVEN onSelectValueChangedEventName is published', () => {
        let callback: (eventId: string, data: any) => void;

        beforeEach(() => {
            component.ngOnInit();
            callback = systemEventService.subscribe.calls.argsFor(0)[1];
        });

        it('AND selected option is already selected THEN it does not update Option Model', () => {
            model.currentSelectedOptionValue = LinkToOption.product;

            callback(null, {
                optionObject: {
                    id: LinkToOption.product,
                    structureApiMode: 'PRODUCT'
                },
                qualifier
            });

            expect(component.optionModel.currentSelectedOptionValue).toBe(LinkToOption.product);
        });

        it(`AND selected option is not already selected THEN it updates Option Model
            AND removes not selected properties from Option Model
            AND publishes system event with the correct payload
        `, () => {
            model.currentSelectedOptionValue = LinkToOption.product;

            callback(null, {
                optionObject: {
                    id: LinkToOption.category,
                    structureApiMode: 'CATEGORY'
                },
                qualifier
            });

            expect(systemEventService.publishAsync).toHaveBeenCalledWith(jasmine.any(String), {
                content: jasmine.objectContaining({
                    linkTo: null,
                    currentSelectedOptionValue: LinkToOption.category,
                    external: true,
                    category: null,
                    productCatalog: null,
                    linkToggle: {
                        urlLink: null
                    }
                }),
                structureApiMode: 'CATEGORY',
                editorId: widgetData.id
            });
        });
    });
});
