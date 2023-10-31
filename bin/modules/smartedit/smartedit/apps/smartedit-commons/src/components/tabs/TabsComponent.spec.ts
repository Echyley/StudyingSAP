/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
import { CommonModule } from '@angular/common';
import { Component, Inject, Injector, NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FundamentalNgxCoreModule } from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';
import { Tab, TabsComponent, TAB_DATA, IUserTrackingService, TabComponent } from 'smarteditcommons';
import { TooltipComponent } from 'smarteditcommons/components/tooltip/TooltipComponent';
import { TestComponent } from './mockComponent/TestComponent';

const SELECTOR_TAB_CONTENT = 'tab-content';

@Component({
    selector: SELECTOR_TAB_CONTENT,
    template: `<p>tab1 component</p>`
})
class Tab1Component {
    constructor(@Inject(TAB_DATA) public tabData: any) {}
}

@Component({
    selector: SELECTOR_TAB_CONTENT,
    template: `<p>tab2 component</p>`
})
class Tab2Component {
    constructor(@Inject(TAB_DATA) public tabData: any) {}
}

@NgModule({
    declarations: [Tab1Component, Tab2Component, TestComponent],
    imports: [CommonModule],
    entryComponents: [Tab1Component, Tab2Component, TestComponent]
})
class MockModule {}

describe('TabsComponent', () => {
    let tabsComponent: TabsComponent<any>;
    let fixture: ComponentFixture<TabsComponent<any>>;
    let nativeElement: HTMLElement;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;
    let inject: jasmine.SpyObj<Injector>;

    async function createTabsComponentContext() {
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot(), MockModule, FundamentalNgxCoreModule],
            declarations: [TabsComponent, TabComponent, TooltipComponent],
            providers: [{ provide: IUserTrackingService, useValue: userTrackingService }],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(TabsComponent, {
                set: {
                    templateUrl: 'base/src/components/tabs/TabsComponent.html'
                }
            })
            .overrideComponent(TabComponent, {
                set: {
                    templateUrl: '/base/src/components/tabs/TabComponent.html',
                    providers: [{ provide: Injector, useValue: inject }]
                }
            })
            .compileComponents();

        fixture = TestBed.createComponent(TabsComponent);
        tabsComponent = fixture.componentInstance;

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    beforeEach(async () => {
        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);
        await createTabsComponentContext();
    });

    afterEach(() => {
        TestBed.resetTestingModule();
    });

    it('Should create Tabs Component correctly', () => {
        expect(tabsComponent).toBeDefined();
    });

    it(
        'Given the Editor modifies a tab - ' +
            'WHEN there has validation error - ' +
            'THEN an error will be display in the error tab',
        () => {
            const tab1 = {
                id: 'tab1',
                hasErrors: true,
                active: true,
                message: 'there has an error',
                title: 'tab1Header',
                component: TestComponent,
                disabled: false
            } as Tab;

            const tab2 = {
                id: 'tab2',
                hasErrors: false,
                active: true,
                message: '',
                title: 'tab2Header',
                component: TestComponent,
                disabled: true
            } as Tab;

            tabsComponent.tabsList = [tab1, tab2];

            tabsComponent.numTabsDisplayed = 2;
            tabsComponent.selectedTab = tab1;
            fixture.detectChanges();

            expect(nativeElement.querySelector('.sm-tab-error')).toBeTruthy();
        }
    );

    describe('tabs Test', () => {
        const tab1 = {
            id: 'tab1',
            hasErrors: false,
            active: true,
            title: 'tab1Header',
            component: Tab1Component,
            disabled: false
        } as Tab;

        const tab2 = {
            id: 'tab2',
            hasErrors: false,
            active: true,
            title: 'tab2Header',
            component: Tab2Component,
            disabled: true
        } as Tab;

        const tab3 = {
            id: 'tab3',
            hasErrors: true,
            active: true,
            title: 'tab3Header',
            message: 'there has an error',
            component: TestComponent,
            disabled: true
        } as Tab;

        const tab4 = {
            id: 'tab4',
            hasErrors: false,
            active: true,
            title: 'tab4Header',
            component: TestComponent,
            disabled: true
        } as Tab;

        beforeEach(() => {
            tabsComponent.tabsList = [tab1, tab2, tab3, tab4];

            tabsComponent.numTabsDisplayed = 3;
            tabsComponent.selectedTab = tab1;
            fixture.detectChanges();
        });

        it('will display all the visible tabs', () => {
            const tabsetSelect = '.se-tabset__tab a';
            expect(nativeElement.querySelectorAll(tabsetSelect).length).toBe(2);
            expect(nativeElement.querySelectorAll(tabsetSelect)[0].innerHTML).toContain(
                'tab1Header'
            );
            expect(nativeElement.querySelectorAll(tabsetSelect)[1].innerHTML).toContain(
                'tab2Header'
            );
        });
        it('will change content when clicking other tab or clicking in tab in the drop-down', () => {
            nativeElement.querySelectorAll('se-tab').forEach((element: HTMLElement) => {
                if (!element.hidden) {
                    expect(element.innerText).toContain('tab1 component');
                }
            });

            tabsComponent.selectedTab = tab2;
            fixture.detectChanges();

            nativeElement.querySelectorAll('se-tab').forEach((element: HTMLElement) => {
                if (!element.hidden) {
                    expect(element.innerText).toContain('tab2 component');
                }
            });

            tabsComponent.selectedTab = tab3;
            fixture.detectChanges();

            nativeElement.querySelectorAll('se-tab').forEach((element: HTMLElement) => {
                if (!element.hidden) {
                    expect(element.innerText).toContain('test component');
                }
            });
        });
        it('will show error on drop-down and MORE header', () => {
            expect(nativeElement.querySelector('su-select .sm-tab-error')).toBeTruthy();
        });
        it('can reset errors in tab and in MORE header', () => {
            tabsComponent.tabsList = tabsComponent.tabsList.map((tab) => ({
                ...tab,
                hasErrors: false
            }));
            fixture.detectChanges();
            expect(nativeElement.querySelector('su-select .sm-tab-error')).toBeNull();
        });
    });
});
