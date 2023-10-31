/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IExperience } from 'smarteditcommons';
export class SeData {
    public pageId: any;
    public seExperienceData: IExperience;
    public seConfigurationData: any;

    constructor() {
        this.pageId = null;
        this.seExperienceData = null;
        this.seConfigurationData = null;
    }
}
