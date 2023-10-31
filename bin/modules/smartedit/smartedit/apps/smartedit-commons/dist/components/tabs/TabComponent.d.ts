import './TabsComponent.scss';
import { InjectionToken, Injector, OnChanges, SimpleChanges } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Tab } from './types';
export declare const TAB_DATA: InjectionToken<TabData<any>>;
export interface TabData<T = any> {
    model: T;
    tabId: string;
    tab: Tab;
}
export declare class TabComponent<T> implements OnChanges {
    private injector;
    tab: Tab;
    model: T;
    scopeStream: BehaviorSubject<{
        model: T;
        tabId: string;
        tab: Tab;
    }>;
    tabInjector: Injector;
    constructor(injector: Injector);
    ngOnChanges(changes: SimpleChanges): void;
    ngOnInit(): void;
}
