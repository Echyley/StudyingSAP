/// <reference types="jquery" />
import { ElementRef, EventEmitter, OnDestroy, OnInit, Renderer2, TemplateRef } from '@angular/core';
import { WindowUtils } from '../../utils';
import { SliderPanelConfiguration } from './interfaces';
import { SliderPanelServiceFactory } from './SliderPanelServiceFactory';
export declare const CSS_CLASSNAMES: {
    SLIDERPANEL_ANIMATED: string;
    SLIDERPANEL_SLIDEPREFIX: string;
};
export declare class SliderPanelComponent implements OnInit, OnDestroy {
    private renderer;
    private element;
    private windowUtils;
    private yjQuery;
    private sliderPanelServiceFactory;
    sliderPanelConfiguration: SliderPanelConfiguration;
    sliderPanelHide: () => Promise<any>;
    sliderPanelShow: () => Promise<any>;
    sliderPanelHideChange: EventEmitter<() => Promise<any>>;
    sliderPanelShowChange: EventEmitter<() => Promise<any>>;
    isShownChange: EventEmitter<boolean>;
    content: TemplateRef<any>;
    isShown: boolean;
    sliderPanelDismissAction: () => void;
    slideClassName: string;
    private sliderPanelService;
    private uniqueId;
    private inlineStyling;
    constructor(renderer: Renderer2, element: ElementRef, windowUtils: WindowUtils, yjQuery: JQueryStatic, sliderPanelServiceFactory: SliderPanelServiceFactory);
    ngOnInit(): void;
    ngOnDestroy(): void;
    hideSlider(): Promise<void>;
    showSlider(): Promise<void>;
    isSaveDisabled(): boolean;
    private addScreenResizeEventHandler;
}
