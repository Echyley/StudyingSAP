/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { TypePermissionsRestService } from 'cmscommons';
import { SelectPageTypeComponent } from 'cmssmarteditcontainer/components/pages/addPageWizard/components/selectPageType/SelectPageTypeComponent';
import { PageType, PageTypeService } from 'cmssmarteditcontainer/dao/PageTypeService';
import { CMSPageTypes } from 'smarteditcommons';

describe('SelectPageTypeComponent', () => {
    let component: SelectPageTypeComponent;
    let pageTypeService: jasmine.SpyObj<PageTypeService>;
    let typePermissionsRestService: jasmine.SpyObj<TypePermissionsRestService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const pageTypes: PageType[] = [
        {
            code: 'ContentPage' as CMSPageTypes,
            description: { en: 'desc1' },
            name: { en: 'name1' },
            type: 'type1'
        },
        {
            code: 'CategoryPage' as CMSPageTypes,
            description: { en: 'desc2' },
            name: { en: 'name2' },
            type: 'type1'
        },
        {
            code: 'ProductPage' as CMSPageTypes,
            description: { en: 'desc3' },
            name: { en: 'name3' },
            type: 'type1'
        },
        {
            code: 'EmailPage' as CMSPageTypes,
            description: { en: 'desc4' },
            name: { en: 'name4' },
            type: 'type1'
        }
    ];

    beforeEach(() => {
        pageTypeService = jasmine.createSpyObj<PageTypeService>('pageTypeService', [
            'getPageTypes'
        ]);
        typePermissionsRestService = jasmine.createSpyObj<TypePermissionsRestService>(
            'typePermissionsRestService',
            ['hasCreatePermissionForTypes']
        );
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        pageTypeService.getPageTypes.and.returnValue(Promise.resolve(pageTypes));
        typePermissionsRestService.hasCreatePermissionForTypes.and.returnValue(
            Promise.resolve<any>({
                ContentPage: {},
                CategoryPage: null,
                ProductPage: null,
                EmailPage: null
            })
        );

        component = new SelectPageTypeComponent(pageTypeService, typePermissionsRestService, cdr);
    });

    describe('GIVEN component is initialized', () => {
        beforeEach(async () => {
            component.pageTypeCode = 'ContentPage';
            await component.ngOnInit();
        });

        it('THEN is should have filtered page types assigned', () => {
            expect(component.pageTypes).toEqual([pageTypes[0]]);
        });

        it('WHEN type is being selected THEN it should emit selected value', () => {
            const spyObj = spyOn(component.onTypeSelected, 'emit');
            component.selectType(pageTypes[2]);

            expect(spyObj).toHaveBeenCalledWith(pageTypes[2]);
        });

        it('AND given pageType is the same as pageTypeCode from input THEN it should return true', () => {
            const actual = component.isSelected(pageTypes[0]);

            expect(actual).toEqual(true);
        });
    });
});
