/// <reference types="jquery" />
import { OnInit } from '@angular/core';
import { PersonalizationsmarteditUtils } from 'personalizationcommons';
import { ContextualMenuItemData } from 'smarteditcommons';
import { PersonalizationsmarteditComponentHandlerService, PersonalizationsmarteditContextualMenuService, PersonalizationsmarteditContextService } from '../../service';
export declare class ShowActionListComponent implements OnInit {
    private contextualMenuItem;
    private persoContextService;
    private persoUtils;
    private persoComponentHandlerService;
    private persoContextualMenuService;
    isContextualMenuShowActionListEnabled: boolean;
    selectedItems: any;
    containerSourceId: string;
    containerId: any;
    private containerIdExists;
    private $element;
    private COMPONENT_SELECTOR;
    constructor(contextualMenuItem: ContextualMenuItemData, persoContextService: PersonalizationsmarteditContextService, persoUtils: PersonalizationsmarteditUtils, persoComponentHandlerService: PersonalizationsmarteditComponentHandlerService, persoContextualMenuService: PersonalizationsmarteditContextualMenuService, yjQuery: JQueryStatic);
    ngOnInit(): void;
    getLetterForElement(index: number): string;
    getClassForElement(index: number): string;
    initItem(item: any): void;
    isCustomizationFromCurrentCatalog(customization: string): boolean;
}
