/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ICMSComponent } from 'cmscommons';
import { IHiddenComponentMenu } from 'cmssmartedit/services/IHiddenComponentMenu';
import { SlotContainerService } from 'cmssmartedit/services/SlotContainerService';
import {
    IContextualMenuButton,
    IContextualMenuConfiguration,
    IContextualMenuService,
    TypedMap
} from 'smarteditcommons';

/**
 * This service is used to retrieve menu items that are available to be used with hidden components.
//  */
@Injectable()
export class HiddenComponentMenuService {
    private readonly MENU_ITEM_EXTERNAL = 'externalcomponentbutton';
    private readonly MENU_ITEM_CLONE = 'clonecomponentbutton';
    private readonly MENU_ITEM_REMOVE = 'se.cms.remove';

    private allowedItems: TypedMap<boolean> = {};

    constructor(
        private contextualMenuService: IContextualMenuService,
        private slotContainerService: SlotContainerService
    ) {
        this.setDefaultItemsAllowed();
    }

    /**
     * This method is used to set the list of items that can be used with hidden components.
     *
     * @param itemsToAllow The ID of the menu items that can be used with hidden components.
     *
     */
    public allowItemsInHiddenComponentMenu(itemsToAllow: string[]): void {
        itemsToAllow.forEach((item) => {
            this.allowedItems[item] = true;
        });
    }

    /**
     * This method removes a provided set of allowed menu items if previously allowed.
     *
     * @param itemsToDisallow An array containing the ID's of the menu items that cannot be used any longer with hidden
     * components.
     *
     */
    public removeAllowedItemsInHiddenComponentMenu(itemsToDisallow: string[]): void {
        itemsToDisallow.forEach((item) => {
            delete this.allowedItems[item];
        });
    }

    /**
     * This method retrieves the list of IDs of the menu items that can be used with hidden components.
     *
     * @returns The list of IDs of the menu items that can be used with hidden components.
     *
     */
    public getAllowedItemsInHiddenComponentMenu(): string[] {
        return Object.keys(this.allowedItems);
    }

    /**
     * This method is used to retrieve the menu items available to be used in the provided component. To do so,
     * this method retrieves contextual menu items available for the provided component and filters out the ones that cannot
     * be used in hidden components. For example, assuming that a visible component has 'drag and drop' and 'remove'
     * contextual menu items, if the component is hidden it should only have the remove button available, since the
     * drag and drop operation is meaningless if the component is hidden. Hence, this service will retrieve only
     * the remove item.
     *
     * @param component The hidden component for which to retrieve its menu items.
     * @param slotId The SmartEdit id of the slot where the component is located.
     *
     * @returns Promise that resolves to an array of contextual menu items available for the component
     * provided.
     */
    public async getItemsForHiddenComponent(
        component: ICMSComponent,
        slotId: string
    ): Promise<IHiddenComponentMenu> {
        this.validateComponent(component);

        const configuration = await this.buildComponentInfo(slotId, component);
        return this.getAllowedItemsForComponent(component, configuration);
    }

    private validateComponent(component: ICMSComponent): void {
        if (!component) {
            throw new Error('HiddenComponentMenuService - Component cannot be null.');
        }
        if (!component.uid) {
            throw new Error('HiddenComponentMenuService - Component needs a uid.');
        }
        if (!component.typeCode) {
            throw new Error('HiddenComponentMenuService - Component needs a type code.');
        }
        if (!component.uuid) {
            throw new Error('HiddenComponentMenuService - Component needs a uuid.');
        }
    }

    private setDefaultItemsAllowed(): void {
        this.allowItemsInHiddenComponentMenu([
            this.MENU_ITEM_EXTERNAL,
            this.MENU_ITEM_CLONE,
            this.MENU_ITEM_REMOVE
        ]);
    }

    private async buildComponentInfo(
        slotId: string,
        component: ICMSComponent
    ): Promise<IContextualMenuConfiguration> {
        const componentContainer = await this.slotContainerService.getComponentContainer(
            slotId,
            component.uuid
        );

        return {
            componentType: component.typeCode,
            componentId: component.uid,
            componentAttributes: {
                smarteditCatalogVersionUuid: component.catalogVersion,
                smarteditComponentId: component.uid,
                smarteditComponentType: component.componentType,
                smarteditComponentUuid: component.uuid,
                smarteditElementUuid: null
            },
            containerType: componentContainer ? componentContainer.containerType : null,
            containerId: componentContainer ? componentContainer.containerId : null,
            element: null,
            isComponentHidden: true,
            slotId,
            iLeftBtns: 0
        };
    }

    private async getAllowedItemsForComponent(
        component: ICMSComponent,
        configuration: IContextualMenuConfiguration
    ): Promise<IHiddenComponentMenu> {
        const menuItems = this.contextualMenuService.getContextualMenuByType(component.typeCode);

        const allowedActionsPromises = menuItems
            .filter(
                (item: IContextualMenuButton) => this.allowedItems[item.key] && !!item.condition
            )
            .map(async (item) => {
                let isEnabled: boolean;
                try {
                    isEnabled = await item.condition(configuration);
                } catch {
                    isEnabled = false;
                }
                return { isEnabled, item };
            });

        const allowedActions = (await Promise.all(allowedActionsPromises))
            .filter(({ isEnabled }) => isEnabled)
            .map(({ item }) => item);

        return { buttons: allowedActions, configuration };
    }
}
