/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    PageEditorModalConfigService,
    PageEditorModalService
} from 'cmssmarteditcontainer/components/pages/services';
import { GenericEditorModalService } from 'cmssmarteditcontainer/services';
import { CrossFrameEventService, LogService, SystemEventService, ICMSPage } from 'smarteditcommons';

describe('PageEditorModalService', () => {
    let genericEditorModalService: jasmine.SpyObj<GenericEditorModalService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let pageEditorModalConfigService: jasmine.SpyObj<PageEditorModalConfigService>;
    let logService: jasmine.SpyObj<LogService>;

    let service: PageEditorModalService;
    beforeEach(() => {
        genericEditorModalService = jasmine.createSpyObj<GenericEditorModalService>(
            'genericEditorModalService',
            ['open']
        );

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['publish']
        );

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);

        pageEditorModalConfigService = jasmine.createSpyObj<PageEditorModalConfigService>(
            'pageEditorModalConfigService',
            ['create']
        );

        logService = jasmine.createSpyObj<LogService>('logService', ['debug']);

        service = new PageEditorModalService(
            genericEditorModalService,
            crossFrameEventService,
            systemEventService,
            pageEditorModalConfigService,
            logService
        );
    });

    it('GIVEN modal is opened WHEN it is saved THEN it publishes Content Catalog Update Event properly', async () => {
        const mockUpdatedCmsPage = {} as ICMSPage;
        pageEditorModalConfigService.create.and.returnValue(Promise.resolve<any>({}));
        genericEditorModalService.open.and.returnValue(Promise.resolve(mockUpdatedCmsPage));

        await service.open(null);

        expect(systemEventService.publishAsync).toHaveBeenCalledWith(
            'EVENT_CONTENT_CATALOG_UPDATE',
            mockUpdatedCmsPage
        );
    });

    it('GIVEN modal is opened WHEN it is cancelled THEN it logs event', async () => {
        pageEditorModalConfigService.create.and.returnValue(Promise.resolve<any>({}));
        genericEditorModalService.open.and.returnValue(Promise.reject());

        await service.open(null);

        expect(logService.debug).toHaveBeenCalled();
        expect(systemEventService.publishAsync).not.toHaveBeenCalled();
    });
});
