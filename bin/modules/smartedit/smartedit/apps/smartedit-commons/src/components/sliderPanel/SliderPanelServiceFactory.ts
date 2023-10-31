/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject } from '@angular/core';
import { YJQUERY_TOKEN } from 'smarteditcommons/services';
import { SliderPanelConfiguration } from './interfaces';
import { SliderPanelService } from './SliderPanelService';

export class SliderPanelServiceFactory {
    constructor(@Inject(YJQUERY_TOKEN) private yjQuery: JQueryStatic) {}

    /**
     * Set and returns a new instance of the slider panel.
     */
    getNewServiceInstance(
        element: JQuery,
        window: Window,
        configuration: SliderPanelConfiguration
    ): SliderPanelService {
        return new SliderPanelService(element, window, configuration, this.yjQuery);
    }
}
