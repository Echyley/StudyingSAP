/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { Component, Injector, NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { TranslateModule } from '@ngx-translate/core';
import { IAnnouncementService } from 'smarteditcommons';
import { AnnouncementComponent } from 'smarteditcontainer/components/announcements/AnnouncementComponent';
import { IAnnouncement } from 'smarteditcontainer/services';

@Component({
    selector: 'announcement-mock',
    template: ` <div>This is an announcement component<br />message: {{ message }}</div> `
})
class MockAnnouncementComponent {
    message = 'Component Based Announcement Message';
}

@NgModule({
    declarations: [MockAnnouncementComponent],
    imports: [CommonModule],
    entryComponents: [MockAnnouncementComponent]
})
class MockModule {}

const simpleAnnouncement: IAnnouncement = {
    id: 'announcement-0',
    messageTitle: 'This is the message title',
    message: 'This is a simple announcement',
    timeout: 5000
};

const nonCloseableAnnouncement: IAnnouncement = {
    id: 'announcement-1',
    message: 'This is a non closeable announcement',
    closeable: false,
    timeout: 5000
};

const componentBasedAnnouncement: IAnnouncement = {
    id: 'announcement-2',
    component: MockAnnouncementComponent
};

describe('AnnouncementComponent', () => {
    let component: AnnouncementComponent;
    let fixture: ComponentFixture<AnnouncementComponent>;
    let nativeElement: HTMLElement;
    let announcementService: jasmine.SpyObj<IAnnouncementService>;
    let injector: jasmine.SpyObj<Injector>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            imports: [BrowserAnimationsModule, TranslateModule.forRoot(), MockModule],
            declarations: [AnnouncementComponent],
            providers: [
                { provide: IAnnouncementService, useValue: announcementService },
                { provide: Injector, useValue: injector }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(AnnouncementComponent, {
                set: {
                    templateUrl: '/base/src/components/announcements/AnnouncementComponent.html'
                }
            })
            .compileComponents()
            .then(() => {
                fixture = TestBed.createComponent(AnnouncementComponent);
                component = fixture.componentInstance;
                nativeElement = fixture.debugElement.nativeElement;
            });
    }));

    it('Should create Announcement Component correctly', () => {
        component.announcement = simpleAnnouncement;
        fixture.detectChanges();
        expect(component).toBeDefined();
    });

    it('WHEN a non-closeable announcement is displayed THEN it does not have a close button', () => {
        component.announcement = nonCloseableAnnouncement;
        fixture.detectChanges();
        expect(nativeElement.querySelector('span.se-announcement-close')).not.toBeTruthy();
        expect(
            nativeElement.querySelector('.se-announcement-content div span').textContent
        ).toContain(nonCloseableAnnouncement.message);
    });

    it('WHEN a closeable announcement is displayed THEN it has a close button', () => {
        component.announcement = simpleAnnouncement;
        fixture.detectChanges();
        expect(nativeElement.querySelector('span.se-announcement-close')).toBeDefined();
    });

    it('WHEN a simple announcement is displayed THEN it prints appropriate data from the message attribute', () => {
        component.announcement = simpleAnnouncement;
        fixture.detectChanges();
        expect(
            nativeElement.querySelector('.se-announcement-content div span').textContent
        ).toContain(simpleAnnouncement.message);
    });

    it('WHEN a components announcement in displayed THEN it prints appropriate message from component class', () => {
        component.announcement = componentBasedAnnouncement;
        fixture.detectChanges();
        expect(nativeElement.querySelector('announcement-mock div').textContent).toContain(
            'Component Based Announcement Message'
        );
    });
});
