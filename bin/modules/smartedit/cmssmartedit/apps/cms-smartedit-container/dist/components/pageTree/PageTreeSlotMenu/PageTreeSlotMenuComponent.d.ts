import { ChangeDetectorRef, OnInit } from '@angular/core';
import { ComponentAttributes, IContextualMenuButton } from 'smarteditcommons';
import { SlotNode, PageTreeSlotMenuService } from '../../../services/pageTree';
export declare class PageTreeSlotMenuComponent implements OnInit {
    private readonly pageTreeSlotMenuService;
    private readonly cdr;
    node: SlotNode;
    items: IContextualMenuButton[];
    componentAttributes: ComponentAttributes;
    constructor(pageTreeSlotMenuService: PageTreeSlotMenuService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
}
