import { TranslateService } from '@ngx-translate/core';
import { ComponentAttributes } from '../di';
import { IComponentHandlerService } from './IComponentHandlerService';
import { ICatalogService, IPageInfoService, ISharedDataService, IEditorModalService } from './interfaces';
import { PageContentSlotsService } from './PageContentSlotsService';
/**
 * Provides methods to interact with the backend for shared slot information.
 */
export declare class SlotSharedService {
    private readonly pageContentSlotsService;
    private readonly pageInfoService;
    private readonly translateService;
    private readonly editorModalService;
    private readonly componentHandlerService;
    private readonly catalogService;
    private readonly sharedDataService;
    private disableShareSlotStatus;
    constructor(pageContentSlotsService: PageContentSlotsService, pageInfoService: IPageInfoService, translateService: TranslateService, editorModalService: IEditorModalService, componentHandlerService: IComponentHandlerService, catalogService: ICatalogService, sharedDataService: ISharedDataService);
    /**
     * Checks if the slot is shared and returns true in case slot is shared and returns false if it is not.
     * Based on this service method the slot shared button is shown or hidden for a particular slotId
     */
    isSlotShared(slotId: string): Promise<boolean>;
    /**
     * Checks whether the given slot is global icon slot or not
     * Returns true if either of the below conditions are true.
     * If the current catalog is multicountry related and if the slot is external slot.
     * If the current catalog is multicountry related and the slot is not external slot but the current page is from parent catalog.
     */
    isGlobalSlot(slotId: string, slotType: string): Promise<boolean>;
    /**
     * Sets the status of the disableSharedSlot feature
     */
    setSharedSlotEnablementStatus(status: boolean): void;
    /**
     * Checks the status of the disableSharedSlot feature
     *
     */
    areSharedSlotsDisabled(): boolean;
    /**
     * Replaces the global slot (multicountry related) based on the options selected in the "Replace Slot" generic editor.
     *
     * @returns A promise that resolves when replace slot operation is completed.
     */
    replaceGlobalSlot(componentAttributes: ComponentAttributes): Promise<any>;
    /**
     * Replaces the shared slot (non-multicountry related) based on the options selected in the "Replace Slot" generic editor
     *
     * @returns A promise that resolves when replace slot operation is completed.
     */
    replaceSharedSlot(componentAttributes: ComponentAttributes): Promise<any>;
    private constructCmsItemParameter;
    private validateComponentAttributes;
    private isCurrentPageFromParentCatalog;
}
