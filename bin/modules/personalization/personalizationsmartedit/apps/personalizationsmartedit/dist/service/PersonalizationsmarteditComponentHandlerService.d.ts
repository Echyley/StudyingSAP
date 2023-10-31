/// <reference types="jquery" />
import { IPageInfoService, IRestServiceFactory, CrossFrameEventService, IComponentHandlerService } from 'smarteditcommons';
export interface IPageContentSlot {
    pageId: string;
    position: string;
    slotId: string;
    slotShared?: boolean;
    slotStatus?: string;
}
export interface IPageContentSlotsResponse {
    pageContentSlotList: IPageContentSlot[];
}
export declare class PersonalizationsmarteditComponentHandlerService {
    private crossFrameEventService;
    private yjQuery;
    private componentHandlerService;
    private pageInfoService;
    private pageContentSlots;
    private readonly resource;
    constructor(restServiceFactory: IRestServiceFactory, crossFrameEventService: CrossFrameEventService, yjQuery: JQueryStatic, componentHandlerService: IComponentHandlerService, pageInfoService: IPageInfoService);
    getParentContainerForComponent(component: JQuery<HTMLElement> | JQuery): JQuery;
    getParentContainerIdForComponent(component: JQuery<HTMLElement> | JQuery): string;
    getPageContentSlots(): Promise<IPageContentSlot[]>;
    getParentContainerSourceIdForComponent(component: JQuery<HTMLElement> | JQuery): string;
    getParentSlotForComponent(component: JQuery<HTMLElement> | JQuery): JQuery;
    getParentSlotIdForComponent(component: HTMLElement | JQuery): string;
    getOriginalComponent(componentId: string, componentType: string): JQuery;
    isExternalComponent(componentId: string, componentType: string): Promise<boolean>;
    getCatalogVersionUuid(component: HTMLElement | JQuery): string;
    getAllSlotsSelector(): string;
    getFromJQuerySelector(selector: JQuery): JQuery;
    getFromSelector(selector: string | HTMLElement): JQuery;
    getFromHTMLSelector(selector: HTMLElement): JQuery;
    getContainerSourceIdForContainerId(containerId: string): string;
    isSlotShared(slotId: string): Promise<boolean>;
    private reloadPageContentSlots;
}
