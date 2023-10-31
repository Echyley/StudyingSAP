/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ThumbnailSelectComponent,
    ThumbnailSelectOption
} from 'cmssmarteditcontainer/components/genericEditor/video';
import { GenericEditorField, GenericEditorWidgetData, SystemEventService } from 'smarteditcommons';

describe('ThumbnailSelectComponent', () => {
    const qualifier = 'qualifier';

    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let widgetData: GenericEditorWidgetData<string>;
    let model: string;

    let component: ThumbnailSelectComponent;
    beforeEach(() => {
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe',
            'publishAsync'
        ]);

        widgetData = {
            id: 'id',
            field: {
                dependsOnField: 'test',
                dependsOnValue: 'off',
                hideFieldWidget: false
            } as GenericEditorField,
            model: 'testModel',
            qualifier,
            isFieldDisabled: null
        };
    });

    describe('ngOnInit', () => {
        it('GIVEN option model WHEN initialized THEN it should subscribe the option changed event', () => {
            component = new ThumbnailSelectComponent(systemEventService, widgetData);

            expect(systemEventService.subscribe).toHaveBeenCalled();
        });
    });

    describe('onDependsOnValueChanged', () => {
        let callback: (eventId: string, data: any) => void;

        beforeEach(() => {
            component = new ThumbnailSelectComponent(systemEventService, widgetData);
            callback = systemEventService.subscribe.calls.argsFor(0)[1];
        });
        it('GIVEN dependency filed change value WHEN current value not equal the dependsOnValue THEN it will update the currentThumbnailSelectedValue as null', () => {
            callback(null, 'on');
            component = new ThumbnailSelectComponent(systemEventService, widgetData);

            expect(systemEventService.publishAsync).toHaveBeenCalledWith(jasmine.any(String), null);
        });

        it('GIVEN dependency filed change value WHEN current value equal the dependsOnValue THEN it will send the event to child field with current select option', () => {
            callback(null, 'off');
            component = new ThumbnailSelectComponent(systemEventService, widgetData);

            expect(systemEventService.publishAsync).toHaveBeenCalled();
        });
    });
});
