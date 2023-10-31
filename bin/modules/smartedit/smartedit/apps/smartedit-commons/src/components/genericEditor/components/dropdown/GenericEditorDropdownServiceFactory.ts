/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { camelCase } from 'lodash';

import { Page, LogService } from '../../../../services';
import { SystemEventService } from '../../../../services/SystemEventService';
import { apiUtils } from '../../../../utils';
import { ActionableSearchItem } from '../../../select';
import { GenericEditorField, GenericEditorOption } from '../../types';
import { GenericEditorUtil } from './GenericEditorUtil';
import {
    DropdownPopulatorFetchPageResponse,
    DropdownPopulatorInterface,
    IDropdownPopulator,
    OptionsDropdownPopulator,
    UriDropdownPopulator
} from './populators';
import {
    GenericEditorDropdownConfiguration,
    IGenericEditorDropdownSelectedOptionEventData,
    IGenericEditorDropdownService,
    IPopulatorName
} from './types';

/**
 * The SEDropdownService handles the initialization and the rendering of the {@link SeDropdownComponent}.
 * - Angular - `CustomDropdownPopulatorsToken` Injection Token
 */
export const GenericEditorDropdownServiceFactory = (
    genericEditorUtil: GenericEditorUtil,
    logService: LogService,
    LINKED_DROPDOWN: string,
    CLICK_DROPDOWN: string,
    DROPDOWN_IMPLEMENTATION_SUFFIX: string,
    systemEventService: SystemEventService,
    optionsDropdownPopulator: OptionsDropdownPopulator,
    uriDropdownPopulator: UriDropdownPopulator,
    customDropdownPopulators?: DropdownPopulatorInterface[]
): any =>
    class implements IGenericEditorDropdownService {
        public actionableSearchItem: ActionableSearchItem;
        public isMultiDropdown: boolean;
        public initialized: boolean;
        public qualifier: string;

        /**
         * Set when GenericEditorDropdownComponent initializes SelectComponent
         */
        public reset: () => void;

        public field: GenericEditorField;
        public model: any;
        public id: string;
        public onClickOtherDropdown: (key?: string, qualifier?: string) => void;
        public items: GenericEditorOption[];
        public selection: GenericEditorOption;
        public eventId: string;
        public clickEventKey: string;
        public populator: IDropdownPopulator;
        public isPaged: boolean;
        public fetchStrategy: any;
        public populatorName: IPopulatorName;

        constructor(conf: GenericEditorDropdownConfiguration) {
            this.field = conf.field;
            this.qualifier = conf.qualifier;
            this.model = conf.model;
            this.id = conf.id;
            this.onClickOtherDropdown = conf.onClickOtherDropdown;
            this.items = [];
        }

        /**
         * Initializes the GenericEditorDropdownComponent with a Dropdown Populator instance,
         * based on the "field" attribute given in constructor.
         */
        public init(): void {
            this.populatorName = {
                options: optionsDropdownPopulator.constructor.name,
                uri: uriDropdownPopulator.constructor.name,
                propertyType: this.field.propertyType + DROPDOWN_IMPLEMENTATION_SUFFIX,
                cmsStructureType: this.field.cmsStructureType + DROPDOWN_IMPLEMENTATION_SUFFIX,
                smarteditComponentType: {
                    withQualifier:
                        this.field.smarteditComponentType +
                        this.field.qualifier +
                        DROPDOWN_IMPLEMENTATION_SUFFIX,
                    withQualifierForDowngradedService: camelCase(
                        this.field.smarteditComponentType +
                            this.field.qualifier +
                            DROPDOWN_IMPLEMENTATION_SUFFIX
                    ),
                    withoutQualifier:
                        this.field.smarteditComponentType + DROPDOWN_IMPLEMENTATION_SUFFIX
                }
            };

            this.isMultiDropdown = this.field.collection ? this.field.collection : false;

            this.triggerAction = this.triggerAction.bind(this);

            this.eventId = (this.id || '') + LINKED_DROPDOWN;
            this.clickEventKey = (this.id || '') + CLICK_DROPDOWN;
            if (this.field.dependsOn) {
                systemEventService.subscribe(this.eventId, this._respondToChange.bind(this));
            }
            systemEventService.subscribe(this.clickEventKey, this._respondToOtherClicks.bind(this));

            const populator = genericEditorUtil.resolvePopulator(
                this.field,
                this.populatorName,
                optionsDropdownPopulator,
                uriDropdownPopulator,
                customDropdownPopulators
            );
            if (!populator) {
                logService.error('No dropdown populator found');
            }
            ({ instance: this.populator, isPaged: this.isPaged } = populator);

            this.fetchStrategy = {
                fetchEntity: this.fetchEntity.bind(this)
            };

            if (this.isPaged) {
                this.fetchStrategy.fetchPage = this.fetchPage.bind(this);
            } else {
                this.fetchStrategy.fetchAll = this.fetchAll.bind(this);
            }

            this.initialized = true;
        }

        /**
         * Publishes an asynchronous event for the currently selected option.
         */
        public triggerAction(): void {
            const selectedObj = this.items.filter(
                (option: GenericEditorOption) => option.id === this.model[this.qualifier]
            )[0];
            const handle: IGenericEditorDropdownSelectedOptionEventData = {
                qualifier: this.qualifier,
                optionObject: selectedObj
            };
            systemEventService.publishAsync(this.eventId, handle);
        }

        public onClick(): void {
            systemEventService.publishAsync(this.clickEventKey, this.field.qualifier);
        }

        /**
         * Uses the configured implementation of {@link DropdownPopulatorInterface}
         * to populate the GenericEditorDropdownComponent items using [fetchAll]{@link DropdownPopulatorInterface#fetchAll}
         *
         * @returns A promise that resolves to a list of options to be populated.
         */
        public fetchAll(search?: string): PromiseLike<GenericEditorOption[]> {
            return this.populator
                .fetchAll({
                    field: this.field,
                    model: this.model,
                    selection: this.selection,
                    search
                })
                .then((options: GenericEditorOption[]) => {
                    this.items = options;
                    return this.items;
                });
        }

        /**
         * Uses the configured implementation of {@link DropdownPopulatorInterface}
         * to populate a single item [getItem]{@link DropdownPopulatorInterface#getItem}
         *
         * @returns A promise that resolves to the option that was fetched
         */
        public fetchEntity(id: string): Promise<GenericEditorOption> {
            return this.populator.getItem({
                field: this.field,
                id,
                model: this.model
            });
        }

        /**
         * @param search The search to filter options by
         * @param pageSize The number of items to be returned
         * @param currentPage The page to be returned
         *
         * Uses the configured implementation of {@link DropdownPopulatorInterface}
         * to populate the seDropdown items using [fetchPage]{@link DropdownPopulatorInterface#fetchPage}
         *
         * @returns A promise that resolves to an object containing the array of items and paging information
         */
        public fetchPage(
            search: string,
            pageSize: number,
            currentPage: number,
            selectedItems?: any
        ): Promise<DropdownPopulatorFetchPageResponse | void> {
            return this.populator
                .fetchPage({
                    field: this.field,
                    model: this.model,
                    selection: this.selection,
                    search,
                    pageSize,
                    currentPage
                })
                .then((page) => {
                    const holderProperty = apiUtils.getKeyHoldingDataFromResponse(page);
                    page.results = page[holderProperty];

                    delete page[holderProperty];
                    this.items = [...this.items, ...page.results];
                    if (this.isMultiDropdown && selectedItems) {
                        return this.limitToNonSelectedItems(page, selectedItems);
                    } else {
                        return page;
                    }
                })
                .catch((error) => {
                    logService.error(`Failed to fetch items and paging information. ${error}`);
                });
        }

        // /**
        //  * CEXC-2588: remove the items already selected to trigger the nextPage method of InifiteScrollingComponent
        //  */
        public isInclude(element: Page<any>, arr: any): boolean {
            let result = false;
            arr.forEach((item) => {
                if (item.uid === (element as any).uid) {
                    result = true;
                }
            });
            return result;
        }

        public limitToNonSelectedItems(page: any, selectedItems: any): any {
            const itemIndex = page.results.length;
            for (let i = itemIndex - 1; i >= 0; i--) {
                const item = page.results[i];
                if (this.isInclude(item, selectedItems)) {
                    page.results.splice(i, 1);
                    page.pagination.count--;
                }
            }
            return page;
        }
        /**
         * CEXC-2588 end
         */

        public _respondToChange(
            _key: string,
            handle: IGenericEditorDropdownSelectedOptionEventData
        ): void {
            if (
                this.field.dependsOn &&
                this.field.dependsOn.split(',').indexOf(handle.qualifier) > -1
            ) {
                this.selection = handle.optionObject;
                if (this.reset) {
                    this.reset();
                }
            }
        }

        /** Responds to other dropdowns clicks */
        public _respondToOtherClicks(key: string, qualifier: string): void {
            if (
                this.field.qualifier !== qualifier &&
                typeof this.onClickOtherDropdown === 'function'
            ) {
                this.onClickOtherDropdown(key, qualifier);
            }
        }
    };
