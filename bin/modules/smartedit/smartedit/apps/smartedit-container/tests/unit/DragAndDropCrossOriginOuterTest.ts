/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as lo from 'lodash';
import 'jasmine';
import {
    CrossFrameEventService,
    DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME,
    IMousePosition,
    SMARTEDIT_DRAG_AND_DROP_EVENTS,
    SMARTEDIT_IFRAME_DRAG_AREA
} from 'smarteditcommons';
import { DragAndDropCrossOrigin, IframeManagerService } from 'smarteditcontainer/services';
import { domHelper, jQueryHelper, ElementForJQuery } from 'testhelpers';

describe('DragAndDropCrossOriginOuter', () => {
    let iframeManagerService: jasmine.SpyObj<IframeManagerService>;
    let yjQuery: JQueryStatic;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;

    let service: DragAndDropCrossOrigin;

    let onDragStart: (eventId: string) => Promise<any>;
    let onDragEnd: (eventId: string) => Promise<any>;
    let dragoverCallback: (event: JQuery.Event) => void;
    let dropCallback: (event: JQuery.Event) => void;
    let throttledSendMousePosition: (mousePosition: IMousePosition) => void;

    let event: jasmine.SpyObj<JQuery.Event>;

    let dragArea: jasmine.SpyObj<JQuery<ElementForJQuery>>;

    let iframe: jasmine.SpyObj<ElementForJQuery>;
    let iframeWrapper: jasmine.SpyObj<JQuery<Element>>;

    const width = 1800;
    const height = 1000;
    const iframeOffset = { top: 5, left: 3 };
    const dragAreaOffset = { top: 50, left: 30 } as JQuery.Coordinates;
    const pageX = 300;
    const pageY = 500;

    beforeEach(() => {
        event = domHelper.event();
        (event as any).pageX = pageX;
        (event as any).pageY = pageY;

        throttledSendMousePosition = jasmine.createSpy('throttledSendMousePosition');

        spyOn(lo, 'throttle').and.returnValue(throttledSendMousePosition as any);

        iframe = domHelper.element('iframe', { width, height, offset: iframeOffset });
        iframeWrapper = jQueryHelper.wrap('iframeWrapper', iframe);

        dragArea = jQueryHelper.wrap('dragArea');
        dragArea.offset.and.returnValue(dragAreaOffset);

        yjQuery = jQueryHelper.jQuery((selector) => {
            if (selector === '#' + SMARTEDIT_IFRAME_DRAG_AREA) {
                return dragArea;
            }
            throw new Error(`unexpected string selector: ${selector}`);
        });

        iframeManagerService = jasmine.createSpyObj('iframeManagerService', [
            'getIframe',
            'isCrossOrigin'
        ]);
        iframeManagerService.getIframe.and.returnValue(iframeWrapper as any);

        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', [
            'publish',
            'subscribe'
        ]);

        service = new DragAndDropCrossOrigin(yjQuery, crossFrameEventService, iframeManagerService);

        service.initialize();
    });

    it('initialize will subscribe to 4 events', () => {
        expect(crossFrameEventService.subscribe).toHaveBeenCalledTimes(4);
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START,
            jasmine.any(Function)
        );
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END,
            jasmine.any(Function)
        );
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME.START,
            jasmine.any(Function)
        );
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME.END,
            jasmine.any(Function)
        );
    });

    describe('callbacks', () => {
        beforeEach(() => {
            onDragStart = crossFrameEventService.subscribe.calls.argsFor(0)[1];
            onDragEnd = crossFrameEventService.subscribe.calls.argsFor(1)[1];

            crossFrameEventService.subscribe.calls.reset();

            expect(onDragStart).toBeDefined();
            expect(onDragEnd).toBeDefined();
        });

        it('onDragEnd will stop if not cross origin', () => {
            onDragEnd('eventId');

            expect(dragArea.off).not.toHaveBeenCalled();
        });

        it('onDragEnd in cross origin, dragover and drop listeners are removed from the drag area', () => {
            iframeManagerService.isCrossOrigin.and.returnValue(true);

            onDragEnd('eventId');

            expect(dragArea.off as any).toHaveBeenCalledWith('dragover');
            expect(dragArea.off as any).toHaveBeenCalledWith('drop');
            expect(dragArea.hide).toHaveBeenCalled();
        });

        it('onDragStart will stop if not cross origin', () => {
            iframeManagerService.isCrossOrigin.and.returnValue(false);

            onDragStart('eventId');

            expect(crossFrameEventService.publish).not.toHaveBeenCalled();
        });

        describe('onDragStart in cross origin', () => {
            beforeEach(() => {
                iframeManagerService.isCrossOrigin.and.returnValue(true);

                onDragStart('eventId');
            });

            it('dragover and drop listeners are removed from the drag area', () => {
                expect(dragArea.off as any).toHaveBeenCalledWith('dragover');
                expect(dragArea.off as any).toHaveBeenCalledWith('drop');
            });

            it('dragover and drop listeners are added to the drag area', () => {
                expect(dragArea.on as any).toHaveBeenCalledWith('dragover', jasmine.any(Function));
                expect(dragArea.on as any).toHaveBeenCalledWith('drop', jasmine.any(Function));
            });

            it('the drag area will acquire same dimensions and position as the storefront iframe', () => {
                expect(dragArea.width as any).toHaveBeenCalledWith(width);
                expect(dragArea.height as any).toHaveBeenCalledWith(height);
                expect(dragArea.css as any).toHaveBeenCalledWith(iframeOffset);
            });

            describe('dragoverCallback and dropCallback', () => {
                beforeEach(() => {
                    dragoverCallback = (dragArea.on.calls.argsFor(0) as any[])[1];
                    dropCallback = (dragArea.on.calls.argsFor(1) as any[])[1];

                    expect(dragoverCallback).toBeDefined();
                    expect(dropCallback).toBeDefined();
                });

                it('dragoverCallback will sendMousePosition', () => {
                    dragoverCallback(event);

                    expect(throttledSendMousePosition).toHaveBeenCalledWith({ x: 270, y: 450 });
                });

                it('dropCallback will send event with mouse position', () => {
                    dropCallback(event);

                    expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                        SMARTEDIT_DRAG_AND_DROP_EVENTS.DROP_ELEMENT,
                        {
                            x: 270,
                            y: 450
                        }
                    );
                });
            });
        });
    });
});
