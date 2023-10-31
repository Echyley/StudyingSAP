/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SlotUnsharedService } from 'cmscommons/services/SlotUnsharedService';
import { CmsitemsRestService, PageContentSlotsService } from 'smarteditcommons';

describe('SlotUnsharedStatusService', () => {
    let service: SlotUnsharedService;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let pageContentSlotsService: jasmine.SpyObj<PageContentSlotsService>;

    const mockSlotId = 'MOCK_SLOT_ID';
    const mockSlotUuid = 'mockSlotUuid';

    beforeEach(() => {
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'delete'
        ]);
        pageContentSlotsService = jasmine.createSpyObj<PageContentSlotsService>(
            'pageContentSlotsService',
            ['isSlotShared', 'getSlotStatus']
        );
        service = new SlotUnsharedService(cmsitemsRestService, pageContentSlotsService);
    });

    describe('isSlotUnshared', () => {
        it('should resolve to true when retrieved slot status is equal OVERRIDE', async () => {
            pageContentSlotsService.getSlotStatus.and.returnValue(Promise.resolve('OVERRIDE'));

            const result = await service.isSlotUnshared(mockSlotId);

            expect(result).toEqual(true);
            expect(pageContentSlotsService.getSlotStatus).toHaveBeenCalledWith(mockSlotId);
        });

        it('should resolve to false when retrieved slot status is different than OVERRIDE', async () => {
            pageContentSlotsService.getSlotStatus.and.returnValue(Promise.resolve('PAGE'));

            const result = await service.isSlotUnshared(mockSlotId);

            expect(result).toEqual(false);
            expect(pageContentSlotsService.getSlotStatus).toHaveBeenCalledWith(mockSlotId);
        });
    });

    describe('isSlotShared', () => {
        it('should call pageContentSlotsService and return value from that service', async () => {
            pageContentSlotsService.isSlotShared.and.returnValue(Promise.resolve(true));

            const result = await service.isSlotShared(mockSlotId);

            expect(result).toEqual(true);
            expect(pageContentSlotsService.isSlotShared).toHaveBeenCalledWith(mockSlotId);
        });
    });

    describe('revertToSharedSlot', () => {
        it('should call cmsItemsRestService with given slot UUID', async () => {
            await service.revertToSharedSlot(mockSlotUuid);

            expect(cmsitemsRestService.delete).toHaveBeenCalledWith(mockSlotUuid);
        });
    });
});
