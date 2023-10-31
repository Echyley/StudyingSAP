import { OnInit } from '@angular/core';
import { DataTableComponentData, ICMSPage } from 'smarteditcommons';
export declare class BaseWrapperComponent implements OnInit {
    data: DataTableComponentData;
    protected item: ICMSPage;
    constructor(data: DataTableComponentData);
    ngOnInit(): void;
}
