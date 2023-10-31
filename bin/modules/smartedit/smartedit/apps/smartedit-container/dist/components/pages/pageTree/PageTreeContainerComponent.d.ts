import { OnInit, Type } from '@angular/core';
import { IPageTreeService } from 'smarteditcommons';
export declare class PageTreeContainerComponent implements OnInit {
    private readonly pageTreeService;
    component: Type<any>;
    constructor(pageTreeService: IPageTreeService);
    ngOnInit(): Promise<void>;
}
