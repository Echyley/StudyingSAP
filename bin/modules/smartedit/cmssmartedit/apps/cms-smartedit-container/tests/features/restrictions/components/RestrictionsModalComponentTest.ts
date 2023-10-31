/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { DialogRef } from '@fundamental-ngx/core';
import { CMSRestriction } from 'cmscommons';
import { RestrictionsModalComponent } from 'cmssmarteditcontainer/components/pages/restrictionsViewer/RestrictionsModalComponent';
import { CmsitemsRestService } from 'smarteditcommons';

describe('RestrictionsModalComponent', () => {
    let component: RestrictionsModalComponent;
    let modalRef: jasmine.SpyObj<DialogRef>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const mockRestrictions: CMSRestriction[] = [
        {
            description: 'Display for users: Anonymous (anonymous);',
            name: 'Anonymous User Restriction',
            type: { en: 'User Restriction' },
            typeCode: 'CMSUserRestriction'
        }
    ];

    const mockRestrictionUuids: string[] = ['uuidOfRestrictionOne'];

    const data = {
        response: mockRestrictions
    } as any;

    beforeEach(() => {
        modalRef = jasmine.createSpyObj<DialogRef>('modalRef', ['dismiss']);
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'getByIdsNoCache'
        ]);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        component = new RestrictionsModalComponent(modalRef, cmsitemsRestService, cdr);
    });

    it('GIVEN provided restrictions WHEN component is initialized THEN it should assign restrictions', async () => {
        cmsitemsRestService.getByIdsNoCache.and.returnValue(Promise.resolve(data));
        modalRef.data = {
            modalData: mockRestrictionUuids
        };

        await component.ngOnInit();

        expect(component.restrictions).toEqual(mockRestrictions);
    });

    it('GIVEN no restrictions provided WHEN component is initialized THEN it should assign restrictions', async () => {
        cmsitemsRestService.getByIdsNoCache.and.returnValue(Promise.resolve<any>({ response: [] }));
        modalRef.data = {
            modalData: []
        };

        await component.ngOnInit();

        expect(component.restrictions).toEqual([]);
    });
});
