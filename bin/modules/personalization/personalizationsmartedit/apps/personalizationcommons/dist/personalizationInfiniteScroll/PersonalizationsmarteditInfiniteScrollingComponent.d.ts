import { OnInit } from '@angular/core';
export declare class PersonalizationsmarteditInfiniteScrollingComponent implements OnInit {
    dropDownContainerClass: string;
    dropDownClass: string;
    distance: number;
    fetchPage: () => Promise<any>;
    initiated: boolean;
    ngOnInit(): void;
    ngOnChanges(): void;
    nextPage(): void;
    private init;
}
