import { OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { IPageVersion, PageVersionSelectionService } from 'smarteditcommons';
export declare class VersionItemContextComponent implements OnInit {
    private pageVersionSelectionService;
    pageVersion$: Observable<IPageVersion>;
    EMPTY_DESCRIPTION_MSG_KEY: string;
    constructor(pageVersionSelectionService: PageVersionSelectionService);
    ngOnInit(): void;
    deselectPageVersion(): void;
}
