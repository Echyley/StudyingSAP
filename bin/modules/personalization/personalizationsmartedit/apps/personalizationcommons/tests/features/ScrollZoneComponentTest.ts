/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ViewContainerRef, EmbeddedViewRef, SimpleChange } from '@angular/core';
import { ScrollZoneComponent } from 'personalizationcommons';

describe('ScrollZoneComponentTest', () => {
    const viewContainerRef = jasmine.createSpyObj<ViewContainerRef>('viewContainerRef', [
        'createEmbeddedView'
    ]);

    const scrollZoneBodyDom = {} as Node;

    const viewRef = ({
        destroy: jasmine.createSpy('embeddedViewRef.destroy'),
        rootNodes: [scrollZoneBodyDom]
    } as unknown) as EmbeddedViewRef<any>;

    const _document = {
        body: jasmine.createSpyObj('document.body', ['appendChild'])
    } as Document;

    let component: ScrollZoneComponent;
    beforeEach(() => {
        viewContainerRef.createEmbeddedView.and.returnValue(viewRef);

        component = new ScrollZoneComponent(viewContainerRef, _document);
    });

    describe('ngOnChanges', () => {
        it('should create Scroll Zone Body', () => {
            component.ngOnChanges({
                scrollZoneVisible: new SimpleChange(false, true, false)
            });

            expect(_document.body.appendChild).toHaveBeenCalledWith(scrollZoneBodyDom);

            expect(component.scrollZoneTop).toBe(true);
            expect(component.scrollZoneBottom).toBe(true);
        });

        it('should destroy Scroll Zone Body', () => {
            // create
            component.ngOnChanges({
                scrollZoneVisible: new SimpleChange(false, true, false)
            });

            // destroy
            component.ngOnChanges({
                scrollZoneVisible: new SimpleChange(true, false, false)
            });

            expect(viewRef.destroy).toHaveBeenCalledTimes(1);
        });
    });
});
