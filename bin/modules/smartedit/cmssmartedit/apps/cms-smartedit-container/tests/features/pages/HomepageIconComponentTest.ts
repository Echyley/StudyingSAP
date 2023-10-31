/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { HomepageIconComponent } from 'cmssmarteditcontainer/components/pages/homepageIcon/HomepageIconComponent';
import { HomepageService } from 'cmssmarteditcontainer/services';
import { ICMSPage } from 'smarteditcommons';

describe('HomepageIconComponent', () => {
    let homepageService: jasmine.SpyObj<HomepageService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    let component: HomepageIconComponent;
    beforeEach(() => {
        homepageService = jasmine.createSpyObj<HomepageService>('homepageService', [
            'getHomepageType'
        ]);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        component = new HomepageIconComponent(homepageService, cdr);
    });

    describe('ngOnChanges', () => {
        it('GIVEN cmsPage AND uriContext has change THEN it gets the type properly', async () => {
            component.cmsPage = {} as ICMSPage;
            component.uriContext = {};

            await component.ngOnChanges();

            expect(homepageService.getHomepageType).toHaveBeenCalled();
        });

        it('GIVEN no cmsPage AND no uriContext THEN it will not get the type', async () => {
            await component.ngOnChanges();

            expect(homepageService.getHomepageType).not.toHaveBeenCalled();
        });
    });

    describe('getIconClass', () => {
        it('GIVEN page is Current Homepage THEN it returns class properly', () => {
            component.type = 'current' as any;

            const iconClass = component.getIconClass();

            expect(iconClass['se-homepage-icon--current']).toBe(true);
        });

        it('GIVEN page is Old Homepage THEN it returns class properly', () => {
            component.type = 'old' as any;

            const iconClass = component.getIconClass();

            expect(iconClass['se-homepage-icon--old']).toBe(true);
        });
    });

    describe('getTooltipMessage', () => {
        it('GIVEN page is Current Homepage THEN it returns translation properly', () => {
            component.type = 'current' as any;

            const message = component.getTooltipMessage();

            expect(message).toBe('se.cms.homepage.tooltip.message.current');
        });

        it('GIVEN page is Old Homepage THEN it returns translation properly', () => {
            component.type = 'old' as any;

            const message = component.getTooltipMessage();

            expect(message).toBe('se.cms.homepage.tooltip.message.previous');
        });
    });
});
