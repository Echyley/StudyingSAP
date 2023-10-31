/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as lodash from 'lodash';
import {
    DropdownPopulatorFetchPageResponse,
    DropdownPopulatorInterface,
    DropdownPopulatorItemPayload,
    DropdownPopulatorPagePayload,
    DropdownPopulatorPayload,
    GenericEditorOption,
    GenericEditorStackService,
    LanguageService,
    UriDropdownPopulator
} from 'smarteditcommons';

/**
 * Implementation of Dropdown Populator for "CMSItemDropdown" Structure Type.
 * Methods of this service will be used to populate the dropdown rendered by GenericEditorDropdownComponent.
 */
@Injectable()
export class CMSItemDropdownDropdownPopulator extends DropdownPopulatorInterface {
    private CMS_ITEMS_URI = '/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/cmsitems';

    constructor(
        private genericEditorStackService: GenericEditorStackService,
        languageService: LanguageService,
        translateService: TranslateService,
        private uriDropdownPopulator: UriDropdownPopulator
    ) {
        super(lodash, languageService, translateService);
    }

    public async fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]> {
        this.preparePayload(payload);
        const items = await this.uriDropdownPopulator.fetchAll(payload);

        return this.getNonNestedComponents(payload.field.editorStackId, items);
    }

    public async fetchPage(
        payload: DropdownPopulatorPagePayload
    ): Promise<DropdownPopulatorFetchPageResponse> {
        this.preparePayload(payload);
        const page = await this.uriDropdownPopulator.fetchPage(payload);
        page.response = this.getNonNestedComponents(payload.field.editorStackId, page.response);

        return page;
    }

    public getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption> {
        this.preparePayload(payload);

        return this.uriDropdownPopulator.getItem(payload);
    }

    private preparePayload(payload: DropdownPopulatorItemPayload): void {
        payload.field.uri = this.CMS_ITEMS_URI;
    }

    private getNonNestedComponents(
        editorStackId: string,
        components: GenericEditorOption[]
    ): GenericEditorOption[] {
        // Get the IDs of the components that are already opened in the editor's stack.
        const componentsInStack = this.genericEditorStackService
            .getEditorsStack(editorStackId)
            .filter((componentInStack) => componentInStack.component.uuid)
            .map((componentInStack) => componentInStack.component.uuid);

        components = components.filter(
            (componentInList) =>
                !componentInList.uuid || !componentsInStack.includes(componentInList.uuid)
        );

        return components;
    }
}
