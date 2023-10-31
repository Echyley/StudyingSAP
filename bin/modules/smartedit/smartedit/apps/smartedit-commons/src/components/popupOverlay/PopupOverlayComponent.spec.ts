/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NO_ERRORS_SCHEMA, Injector, DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { PopupOverlayComponent } from './PopupOverlayComponent';
import { PopoverTrigger } from './PopupOverlayTrigger';
import { TestPopupOverlayFakeComponent, ON_SHOW } from './TestPopupOverlayFakeComponent';

const HELLO_TEXT = 'Hello from component!';
const DISPLAYED_ALWAYS = 'always displayed';
const HIDDEN_ALWAYS = 'always hidden';

describe('TestPopupOverlayFakeComponent', () => {
    let nativeElement: HTMLElement;
    let testPopupOverlayFakeComponent: TestPopupOverlayFakeComponent;
    let popupOverlayComponent: PopupOverlayComponent;
    let fixture: ComponentFixture<TestPopupOverlayFakeComponent>;
    let injector: jasmine.SpyObj<Injector>;
    let popupOverlayOnShowSpy: any;
    let popupOverlayOnHideSpy: any;
    let clickButton: DebugElement;
    let messageEle: HTMLElement;

    function action(isOpen: boolean, message = HELLO_TEXT) {
        popupOverlayComponent.handleOpenChange(isOpen);
        testPopupOverlayFakeComponent.setMessage(message);
        fixture.detectChanges();
    }

    async function createTestPopupOverlayComponentContext() {
        await TestBed.configureTestingModule({
            declarations: [TestPopupOverlayFakeComponent, PopupOverlayComponent],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(PopupOverlayComponent, {
                set: {
                    templateUrl: '/base/src/components/popupOverlay/PopupOverlayComponent.html',
                    styleUrls: ['/base/src/components/popupOverlay/PopupOverlayComponent.scss'],
                    providers: [{ provide: Injector, useValue: injector }]
                }
            })
            .compileComponents();
        fixture = TestBed.createComponent(TestPopupOverlayFakeComponent);
        testPopupOverlayFakeComponent = fixture.componentInstance;
        fixture.detectChanges();
        popupOverlayComponent = testPopupOverlayFakeComponent.popupOverlayComponentRef;
        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
        testPopupOverlayFakeComponent.message = '';
        clickButton = fixture.debugElement.query(By.css('#clickButton'));
        messageEle = nativeElement.querySelector('#message');
        popupOverlayOnShowSpy = spyOn(
            testPopupOverlayFakeComponent.popupOverlayComponentRef.popupOverlayOnShow,
            'emit'
        );
        popupOverlayOnHideSpy = spyOn(
            testPopupOverlayFakeComponent.popupOverlayComponentRef.popupOverlayOnHide,
            'emit'
        );
    }

    beforeEach(async () => {
        await createTestPopupOverlayComponentContext();
    });

    it('Should create PopupOverlay Component correctly', () => {
        expect(createTestPopupOverlayComponentContext).toBeDefined();
    });

    it('Given the trigger is click, then popup opens after click', () => {
        testPopupOverlayFakeComponent.trigger = PopoverTrigger.Click as any;
        clickButton.triggerEventHandler('click', {});
        action(true);
        expect(popupOverlayComponent.isOpen).toBeTruthy();
        expect(popupOverlayOnShowSpy).toHaveBeenCalled();
        expect((messageEle as any).innerText).toEqual(HELLO_TEXT);
    });

    it('Given the trigger is hover, then popup opens after mouse enter', () => {
        testPopupOverlayFakeComponent.trigger = PopoverTrigger.Hover as any;
        clickButton.triggerEventHandler('mouseenter', {});
        action(true);
        expect(popupOverlayComponent.isOpen).toBe(true);
        expect((messageEle as any).innerText).toEqual(HELLO_TEXT);
        expect(popupOverlayOnShowSpy).toHaveBeenCalled();
    });

    it('Given the trigger is hover, then popup opens after mouse leave', () => {
        testPopupOverlayFakeComponent.trigger = PopoverTrigger.Hover as any;
        clickButton.triggerEventHandler('mouseleave', {});
        action(false, '');
        expect(popupOverlayComponent.isOpen).toBeFalsy();
        expect(popupOverlayOnHideSpy).toHaveBeenCalled();
        expect((messageEle as any).innerText).toEqual('');
    });

    it('Given the trigger is "always displayed", the popup is always open', () => {
        testPopupOverlayFakeComponent.trigger = DISPLAYED_ALWAYS as any;
        action(true, DISPLAYED_ALWAYS);
        expect(popupOverlayComponent.isOpen).toBeTruthy();
        expect(popupOverlayOnShowSpy).toHaveBeenCalled();
        expect((messageEle as any).innerText).toEqual(DISPLAYED_ALWAYS);
    });

    it('Given the trigger is "always hidden", the popup is always hidden', () => {
        testPopupOverlayFakeComponent.trigger = HIDDEN_ALWAYS as any;
        action(false, HIDDEN_ALWAYS);
        expect(popupOverlayComponent.isOpen).toBeFalsy();
        expect((messageEle as any).innerText).toEqual(HIDDEN_ALWAYS);
    });

    it('Given the popup is opened, the callback message is visible', () => {
        action(true);
        expect((messageEle as any).innerText).toEqual(HELLO_TEXT);
        testPopupOverlayFakeComponent.handleOnShow();
        fixture.detectChanges();
        expect((messageEle as any).innerText).toEqual(ON_SHOW);
    });

    it('Given the popup is closed, the callback message is visible', () => {
        action(false, '');
        testPopupOverlayFakeComponent.hidePopup();
        fixture.detectChanges();
        expect((messageEle as any).innerText).toEqual('');
    });
});
