/// <reference types="jquery" />
import { ChangeDetectorRef, ElementRef, OnDestroy, OnInit } from '@angular/core';
import { AbstractDecorator, CrossFrameEventService } from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../service';
export declare class PersonalizationsmarteditCombinedViewComponentLightUpDecorator extends AbstractDecorator implements OnInit, OnDestroy {
    private personalizationsmarteditContextService;
    private crossFrameEventService;
    private cdr;
    letterForElement: string;
    classForElement: string;
    private $element;
    private allBorderClassess;
    private unSubscribeRegOverlayRerender;
    constructor(personalizationsmarteditContextService: PersonalizationsmarteditContextService, crossFrameEventService: CrossFrameEventService, cdr: ChangeDetectorRef, yjQuery: JQueryStatic, element: ElementRef);
    ngOnInit(): void;
    ngOnDestroy(): void;
    private calculate;
}
