/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { MuteBooleanComponent } from 'cmssmarteditcontainer/components/genericEditor/video';
import { GenericEditorField, GenericEditorWidgetData, SystemEventService } from 'smarteditcommons';

describe('MuteBooleanComponent', () => {
    const qualifier = 'qualifier';

    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let widgetData: GenericEditorWidgetData<any>;

    let component: MuteBooleanComponent;
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
                hideFieldWidget: false,
                defaultValue: 'false'
            } as GenericEditorField,
            model: {},
            qualifier,
            isFieldDisabled: null
        };
    });

    describe('onDependencyValueChangedEvent', () => {
        let callback: (eventId: string, data: any) => void;

        beforeEach(() => {
            component = new MuteBooleanComponent(systemEventService, widgetData);
            callback = systemEventService.subscribe.calls.argsFor(0)[1];
        });
        it('GIVEN data WHEN dependency field change the value equal to dependsOnValue THEN it should show the component', () => {
            callback(null, 'off');

            expect(component.widget.field.hideFieldWidget).toBe(false);
        });

        it('GIVEN data WHEN dependency field change the value not equal to dependsOnValue THEN it should hide the component', () => {
            callback(null, 'on');

            expect(component.widget.field.hideFieldWidget).toBe(true);
        });
    });
});
