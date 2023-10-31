/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */
import './TabsComponent.scss';
import {
    Component,
    InjectionToken,
    Injector,
    Input,
    OnChanges,
    SimpleChanges
} from '@angular/core';
import { isEqual } from 'lodash';
import { BehaviorSubject } from 'rxjs';
import { Tab } from './types';

export const TAB_DATA = new InjectionToken<TabData>('tab-data');

/**
 * A data to be injected by {@link TabComponent} to a child component.
 */
export interface TabData<T = any> {
    model: T;
    tabId: string;
    tab: Tab;
}

/**
 * The component responsible for wrapping the content of a tab within {@link TabsComponent}.
 */
@Component({
    selector: 'se-tab',
    templateUrl: './TabComponent.html'
})
export class TabComponent<T> implements OnChanges {
    @Input() tab: Tab;

    /**
     * Custom data. Neither the [\<se-tabs\>]{@link TabsComponent} component or the [\<se-tab\>]{@link TabComponent} component
     * can modify this value. The tab contents determine how to parse and use this object.
     */
    @Input() model: T;

    public scopeStream: BehaviorSubject<{
        model: T;
        tabId: string;
        tab: Tab;
    }> = new BehaviorSubject(null);
    public tabInjector: Injector;

    constructor(private injector: Injector) {}

    ngOnChanges(changes: SimpleChanges): void {
        const modelChanged: boolean =
            changes.model && !isEqual(changes.model.previousValue, changes.model.currentValue);
        const tabChanged: boolean =
            changes.tab && !isEqual(changes.tab.previousValue, changes.tab.currentValue);

        if (tabChanged || modelChanged) {
            this.scopeStream.next({ model: this.model, tabId: this.tab.id, tab: this.tab });
        }
    }

    ngOnInit(): void {
        this.tabInjector = Injector.create({
            providers: [
                {
                    provide: TAB_DATA,
                    useValue: { model: this.model, tabId: this.tab.id, tab: this.tab }
                }
            ],
            parent: this.injector
        });
    }
}
