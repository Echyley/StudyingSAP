/// <reference types="jquery" />
import { OnDestroy, ElementRef, AfterViewInit } from '@angular/core';
import { AbstractDecorator, CrossFrameEventService } from 'smarteditcommons';
import { PersonalizationsmarteditContextService, PersonalizationsmarteditComponentHandlerService } from '../service';
export declare class PersonalizationsmarteditComponentLightUpDecorator extends AbstractDecorator implements OnDestroy, AfterViewInit {
    private personalizationsmarteditContextService;
    private personalizationsmarteditComponentHandlerService;
    private crossFrameEventService;
    private $element;
    private CONTAINER_TYPE;
    private ACTION_ID_ATTR;
    private PARENT_CONTAINER_SELECTOR;
    private PARENT_CONTAINER_WITH_ACTION_SELECTOR;
    private COMPONENT_SELECTOR;
    /** Event when selecting a customization.  */
    private unRegisterCustomizeContextSynchronized;
    constructor(personalizationsmarteditContextService: PersonalizationsmarteditContextService, personalizationsmarteditComponentHandlerService: PersonalizationsmarteditComponentHandlerService, crossFrameEventService: CrossFrameEventService, element: ElementRef, yjQuery: JQueryStatic);
    ngAfterViewInit(): void;
    ngOnDestroy(): void;
    /**
     * There is an issue that the DOM instance of $element has been created but it has not been appended to the DOM yet and hence it is not possible to access its parent element.
     * By using timeout we ensure that the element has been already appended to DOM.
     */
    private delayToggleCssClasses;
    private toggleCssClasses;
    private isVariationComponentSelected;
    private isComponentSelected;
}
