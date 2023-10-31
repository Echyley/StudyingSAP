/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IStorageService } from 'smarteditcommons';
import { ResizerDirective } from 'smarteditcontainer/directive';

describe('ResizerDirective', () => {
    let storageService: jasmine.SpyObj<IStorageService>;
    let yjQuery: any;
    let resizer: ResizerDirective;
    let mousedownEvent: jasmine.SpyObj<MouseEvent>;
    let resizeElement: jasmine.SpyObj<any>;
    let documentElement: jasmine.SpyObj<any>;
    let iframeElement: jasmine.SpyObj<any>;
    const TRANSITION = 'transition';

    beforeEach(() => {
        yjQuery = jasmine.createSpy('yjQuery');
        resizer = new ResizerDirective(storageService, yjQuery);
        resizer.resizer = '#resize-div';
    });

    it('WHEN mousedown add event to document', () => {
        mousedownEvent = jasmine.createSpyObj('mousedownEvent', ['preventDefault']);
        const RESIZE_WIDTH = 360;
        resizeElement = jasmine.createSpyObj('resizeElement', ['css', 'width']);
        const ON_TIMES = 3;
        documentElement = jasmine.createSpyObj('documentElement', ['on']);
        iframeElement = jasmine.createSpyObj('iframeElement', ['css']);

        yjQuery.withArgs(resizer.resizer as any).and.returnValue(resizeElement);

        resizeElement.width.and.returnValue(RESIZE_WIDTH as any);
        resizeElement.css.withArgs(TRANSITION).and.returnValue('startTransition');
        resizeElement.css.withArgs(TRANSITION, 'none').and.returnValue(undefined);
        yjQuery.withArgs(document).and.returnValue(documentElement);
        yjQuery.withArgs('iframe' as any).and.returnValue(iframeElement);

        resizer.onMouseDown(mousedownEvent);

        expect(resizeElement.css).toHaveBeenCalledWith(TRANSITION);
        expect(resizeElement.css).toHaveBeenCalledWith(TRANSITION, 'none');
        expect(documentElement.on).toHaveBeenCalledTimes(ON_TIMES);
        expect(iframeElement.css).toHaveBeenCalled();
    });
});
