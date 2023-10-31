/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, SimpleChanges } from '@angular/core';
import { SelectPageTemplateComponent } from 'cmssmarteditcontainer/components/pages/addPageWizard/components/selectPageTemplate/SelectPageTemplateComponent';
import { PageTemplateService } from 'cmssmarteditcontainer/services/PageTemplateService';
import { functionsUtils, PageTemplateType } from 'smarteditcommons';

describe('SelectPageTemplateComponent', () => {
    let component: SelectPageTemplateComponent;
    let pageTemplateService: jasmine.SpyObj<PageTemplateService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const templates: PageTemplateType[] = [
        {
            name: 'Homepage Template',
            frontEndName: 'HomepageTemplate',
            uid: 'Homepage Template',
            uuid: 'Homepage Template',
            catalogVersion: 'Catalog Vesion online'
        },
        {
            name: 'FAQ Template',
            frontEndName: 'FAQTemplate',
            uid: 'FAQ Template',
            uuid: 'FAQ Template',
            catalogVersion: 'Catalog Vesion online'
        },
        {
            name: 'Contact Us',
            frontEndName: 'ContactUs',
            uid: 'Contact Us',
            uuid: 'Contact Us',
            catalogVersion: 'Catalog Vesion online'
        },
        {
            name: 'Mobile Homepage Template',
            frontEndName: 'MobileHomepageTemplate',
            uid: 'Mobile Homepage Template',
            uuid: 'Mobile Homepage Template',
            catalogVersion: 'Catalog Vesion online'
        },
        {
            name: 'Responsive Homepage Template',
            frontEndName: 'ResponsiveHomepageTemplate',
            uid: 'Responsive Homepage Template',
            uuid: 'Responsive Homepage Template',
            catalogVersion: 'Catalog Vesion online'
        }
    ];
    const pageCode = 'ContentPage';

    beforeEach(() => {
        pageTemplateService = jasmine.createSpyObj<PageTemplateService>('pageTemplateService', [
            'getPageTemplatesForType'
        ]);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        pageTemplateService.getPageTemplatesForType.and.returnValue(Promise.resolve({ templates }));

        component = new SelectPageTemplateComponent(pageTemplateService, cdr);
    });

    it('WHEN page code is not present in ngOnChanges THEN it should not throw', async () => {
        try {
            await component.ngOnChanges({});
        } catch {
            functionsUtils.assertFail();
        }
    });

    describe('[ngOnChanges] WHEN page code has changed', () => {
        it('AND templates for given page code are in cache THEN they should be returned, search term should be cleared and selected template should be reset', async () => {
            component.pageTypeCode = pageCode;
            component.searchString = 'value';
            (component as any).selectedTemplate = templates[0];
            (component as any).cache[pageCode] = templates;

            await component.ngOnChanges({
                pageTypeCode: { currentValue: pageCode }
            } as unknown as SimpleChanges);

            expect(pageTemplateService.getPageTemplatesForType).not.toHaveBeenCalled();
            expect((component as any).selectedTemplate).toEqual(null);
            expect(component.searchString).toEqual('');
            expect(component.filteredPageTemplates).toEqual(templates);
            expect(component.pageTemplates).toEqual(templates);
        });

        it('AND templates for given page are not in cache THEN it should get templates from service and update the cache', async () => {
            component.pageTypeCode = pageCode;
            component.uriContext = { context: 'context' };
            (component as any).cache = {};

            await component.ngOnChanges({
                pageTypeCode: { currentValue: pageCode }
            } as unknown as SimpleChanges);

            expect(pageTemplateService.getPageTemplatesForType).toHaveBeenCalledWith(
                { context: 'context' },
                pageCode as any
            );
            expect((component as any).cache[pageCode]).toEqual(templates);
            expect(component.filteredPageTemplates).toEqual(templates);
            expect(component.pageTemplates).toEqual(templates);
        });
    });

    it('WHEN template is being selected THEN it should set selected template and emit that change', () => {
        const emitSpy = spyOn(component.onTemplateSelected, 'emit');

        component.templateSelected(templates[0]);

        expect(emitSpy).toHaveBeenCalledWith(templates[0]);
        expect((component as any).selectedTemplate).toEqual(templates[0]);
    });

    it('WHEN given pageTemplate is the same as selected THEN it should return true', () => {
        component.templateSelected(templates[1]);

        expect(component.isSelected(templates[1])).toEqual(true);
    });

    it('WHEN search input is getting clear THEN it should clear input value and reset filtered page templates to default', () => {
        component.pageTemplates = templates;
        component.filteredPageTemplates = [];
        component.searchString = 'val';

        component.clearSearch();

        expect(component.searchString).toEqual('');
        expect(component.filteredPageTemplates).toEqual(templates);
    });

    describe('filtering', () => {
        beforeEach(async () => {
            component.pageTypeCode = pageCode;
            await component.ngOnChanges({
                pageTypeCode: { currentValue: pageCode }
            } as unknown as SimpleChanges);
        });

        it('WHEN criteria matches few templates THEN all matching templates should be returned', function () {
            const criteria = 'Template';

            component.onSearchChange(criteria);
            const filterResult = component.filteredPageTemplates;
            expect(filterResult).not.toBeNull();
            expect(filterResult[0].name).toBe('Homepage Template');
            expect(filterResult.length).toBe(4);
        });

        it('WHEN criteria does not match any templates THEN it should return empty results', function () {
            const criteria = 'testing';

            component.onSearchChange(criteria);
            const filterResult = component.filteredPageTemplates;
            expect(filterResult).not.toBeNull();
            expect(filterResult).toEqual([]);
            expect(filterResult.length).toBe(0);
        });

        it('WHEN criteria matches only one result regardless test case THEN it should be returned', function () {
            const criteria = 'mob';

            component.onSearchChange(criteria);
            const filterResult = component.filteredPageTemplates;
            expect(filterResult[0].name).toBe('Mobile Homepage Template');
            expect(filterResult.length).toBe(1);
        });

        it('WHEN criteria is empty THEN it should return all templates', function () {
            const criteria = ' ';

            component.onSearchChange(criteria);
            const filterResult = component.filteredPageTemplates;
            expect(filterResult.length).toBe(5);
        });

        it('WHEN criteria is null THEN it should return all templates', function () {
            const criteria = null;

            component.onSearchChange(criteria);
            const filterResult = component.filteredPageTemplates;
            expect(filterResult.length).toBe(5);
        });
    });
});
