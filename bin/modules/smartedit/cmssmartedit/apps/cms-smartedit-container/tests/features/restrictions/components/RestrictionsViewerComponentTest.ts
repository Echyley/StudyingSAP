/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CMSRestriction } from 'cmscommons';
import { RestrictionsViewerComponent } from 'cmssmarteditcontainer/components/pages/restrictionsViewer/RestrictionsViewerComponent';
import { LogService, ModalService } from 'smarteditcommons';

describe('RestrictionsViewerComponent', () => {
    let component: RestrictionsViewerComponent;
    let modalService: jasmine.SpyObj<ModalService>;
    let logService: jasmine.SpyObj<LogService>;

    const mockRestrictions: CMSRestriction[] = [
        {
            description: 'Display for users: Anonymous (anonymous);',
            name: 'Anonymous User Restriction',
            type: { en: 'User Restriction' },
            typeCode: 'CMSUserRestriction'
        }
    ];
    const preventDefault = jasmine.createSpy();
    const mockEvent = {
        preventDefault
    } as unknown as Event;

    beforeEach(() => {
        modalService = jasmine.createSpyObj<ModalService>('modalService', ['open']);
        modalService.open.and.returnValue({
            afterClosed: {
                toPromise: () => Promise.resolve()
            }
        } as any);
        logService = jasmine.createSpyObj<LogService>('logService', ['warn']);

        component = new RestrictionsViewerComponent(modalService, logService);
        component.restrictions = mockRestrictions;
    });

    it('WHEN showRestrictions is called THEN it should call modalService', () => {
        component.showRestrictions(mockEvent);

        expect(preventDefault).toHaveBeenCalled();
        expect(modalService.open).toHaveBeenCalledWith({
            component: jasmine.any(Function) as any,
            data: mockRestrictions,
            templateConfig: {
                title: 'se.cms.restrictionsviewer.title',
                isDismissButtonVisible: true
            },
            config: {
                dialogPanelClass: 'modal-dialog'
            }
        });
    });

    it('WHEN showRestrictions is called and modalService promise is rejected THEN it should call logService', async () => {
        modalService.open.and.returnValue({
            afterClosed: {
                toPromise: () => Promise.reject()
            }
        } as any);
        component = new RestrictionsViewerComponent(modalService, logService);
        component.restrictions = mockRestrictions;

        await component.showRestrictions(mockEvent);
        // error from showRestrictions method is properly handled by .catch
        // so in fact method doesn't throw an error
        expect(logService.warn).toHaveBeenCalled();
    });
});
