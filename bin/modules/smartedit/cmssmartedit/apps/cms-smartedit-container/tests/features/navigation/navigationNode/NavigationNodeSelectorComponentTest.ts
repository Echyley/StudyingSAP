/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import {
    NavigationNodeSelectorComponent,
    NavigationNodeUidsDTO
} from 'cmssmarteditcontainer/components/navigation';
import {
    GenericEditorWidgetData,
    ICatalogService,
    SystemEventService,
    IUriContext,
    GenericEditorAttribute,
    CmsitemsRestService,
    CMSItem
} from 'smarteditcommons';

describe('NavigationNodeSelectorComponent', () => {
    let component: NavigationNodeSelectorComponent;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;
    let injectedData: GenericEditorWidgetData<CMSItem>;

    let unsubscribe: jasmine.Spy;
    const mockCmsItem = {
        uid: 'itemUid'
    } as CMSItem;
    const mockContext = {
        context: 'context'
    } as IUriContext;

    beforeEach(() => {
        injectedData = {
            field: {
                editable: true
            },
            qualifier: 'navigationNode',
            model: {
                navigationNode: 'navigationUuid'
            }
        } as unknown as GenericEditorWidgetData<CMSItem>;
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'retrieveUriContext'
        ]);
        catalogService.retrieveUriContext.and.returnValue(Promise.resolve(mockContext));

        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'getById'
        ]);
        cmsitemsRestService.getById.and.returnValue(Promise.resolve(mockCmsItem));

        unsubscribe = jasmine.createSpy();
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe'
        ]);
        systemEventService.subscribe.and.returnValue(unsubscribe);

        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        component = new NavigationNodeSelectorComponent(
            injectedData,
            catalogService,
            cmsitemsRestService,
            systemEventService,
            cdr
        );
    });

    describe('initialization', () => {
        it('WHEN component is constructed it should have assigned field from injected data', () => {
            expect(component.isReady).toEqual(false);
            expect(component.field).toEqual({ editable: true } as GenericEditorAttribute);
            expect(component.cmsItem).toEqual({
                navigationNode: 'navigationUuid'
            } as unknown as CMSItem);
            expect(component.qualifier).toEqual('navigationNode');
        });

        it('GIVEN qualifier is defined in cmsItem WHEN ngOnInit is called THEN it should assign uriContext, nodeUid and subscribe to event', async () => {
            await component.ngOnInit();

            expect(component.uriContext).toEqual(mockContext);
            expect(component.isReady).toEqual(true);
            expect(component.nodeUid).toEqual('itemUid');

            expect(catalogService.retrieveUriContext).toHaveBeenCalled();
            expect(cmsitemsRestService.getById).toHaveBeenCalledWith('navigationUuid');
            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'selected_node',
                jasmine.any(Function)
            );
        });

        it('GIVEN qualifier does not exists in cmsItem WHEN ngOnInit is called THEN it should assign uriContext, nodeUid and subscribe to event', async () => {
            component.qualifier = 'not existing';
            await component.ngOnInit();

            expect(component.uriContext).toEqual(mockContext);
            expect(component.isReady).toEqual(true);
            expect(component.nodeUid).toEqual(null);

            expect(catalogService.retrieveUriContext).toHaveBeenCalled();
            expect(cmsitemsRestService.getById).not.toHaveBeenCalled();
            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'selected_node',
                jasmine.any(Function)
            );
        });
    });

    it('WHEN component is destroyed THEN it should unsubscribe from an event', async () => {
        await component.ngOnInit();

        component.ngOnDestroy();

        expect(unsubscribe).toHaveBeenCalled();
    });

    it('WHEN remove method is called THEN it should remove field matching given qualifier from cmsItem object', async () => {
        await component.ngOnInit();

        component.remove();

        expect(component.cmsItem).toEqual({} as CMSItem);
    });

    it('WHEN subscribed event is called THEN it should change the cmsItem value on qualifier field and update nodeUid', async () => {
        await component.ngOnInit();
        const subscribedMethod: (eventId: string, idObj: NavigationNodeUidsDTO) => void =
            systemEventService.subscribe.calls.argsFor(0)[1];

        subscribedMethod('eventId', { nodeUid: 'newNodeUid', nodeUuid: 'newModelUuid' });

        expect(component.nodeUid).toEqual('newNodeUid');
        expect(component.cmsItem[component.qualifier]).toEqual('newModelUuid');
    });
});
