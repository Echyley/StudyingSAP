/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { MessageComponent } from 'smarteditcommons';
import { SystemEventService } from '../../../services';
import { EventMessageComponent } from './EventMessageComponent';

describe('EventMessageComponent', () => {
    let component: EventMessageComponent;
    let fixture: ComponentFixture<EventMessageComponent>;

    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let nativeElement: HTMLElement;

    function createMessageComponent() {
        fixture = TestBed.createComponent(EventMessageComponent);
        component = fixture.componentInstance;
        component.type = 'warning';
        component.title = 'message title';
        component.description = 'message description';

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    beforeEach(async () => {
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe',
            'publishAsync'
        ]);
        systemEventService.subscribe.and.returnValue(function () {});
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot()],
            declarations: [MessageComponent, EventMessageComponent],
            providers: [{ provide: SystemEventService, useValue: systemEventService }]
        }).compileComponents();
        createMessageComponent();
    });

    it('Should create Message component correctly', () => {
        expect(component).toBeDefined();
    });

    it('title and description are set as expected', () => {
        component.show = true;
        fixture.detectChanges();
        const title: HTMLElement = nativeElement.querySelector('.y-message-info-title');
        expect(title).toBeTruthy();
        expect(title.textContent).toContain(component.title);

        const description: HTMLElement = nativeElement.querySelector('.y-message-info-description');
        expect(description).toBeTruthy();
        expect(description.textContent).toContain(component.description);
    });
});
