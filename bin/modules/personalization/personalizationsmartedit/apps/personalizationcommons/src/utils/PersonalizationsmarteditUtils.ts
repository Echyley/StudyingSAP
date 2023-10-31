/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as lodash from 'lodash';
import moment from 'moment';
import { take } from 'rxjs/operators';
import { ICatalogService, L10nPipe } from 'smarteditcommons';
import { CustomizationVariation, CustomizationStatus } from '../types';
import {
    PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING,
    PERSONALIZATION_MODEL_STATUS_CODES,
    PERSONALIZATION_VIEW_STATUS_MAPPING_CODES
} from './PersonalizationsmarteditConstants';

@Injectable()
export class PersonalizationsmarteditUtils {
    constructor(
        private translateService: TranslateService,
        private l10nPipe: L10nPipe,
        private catalogService: ICatalogService
    ) {}

    public pushToArrayIfValueExists(array: any, sKey: string, sValue: string): void {
        if (sValue) {
            array.push({
                key: sKey,
                value: sValue
            });
        }
    }

    public getVariationCodes(variations: any): any {
        if (typeof variations === undefined || variations === null) {
            return [];
        }
        const allVariationsCodes = variations
            .map((elem: any) => elem.code)
            .filter((elem: any) => typeof elem !== 'undefined');
        return allVariationsCodes;
    }

    public getVariationKey(customizationId: string, variations: any): any {
        if (customizationId === undefined || variations === undefined) {
            return [];
        }

        const allVariationsKeys = variations.map((variation: any) => ({
            variationCode: variation.code,
            customizationCode: customizationId,
            catalog: variation.catalog,
            catalogVersion: variation.catalogVersion
        }));
        return allVariationsKeys;
    }

    public getSegmentTriggerForVariation(variation: any): any {
        const triggers = variation.triggers || [];
        const segmentTriggerArr = triggers.filter(
            (trigger: any) => trigger.type === 'segmentTriggerData'
        );

        if (segmentTriggerArr.length === 0) {
            return {};
        }

        return segmentTriggerArr[0];
    }

    public isPersonalizationItemEnabled(item: any): any {
        return item.status === PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;
    }

    public getEnablementTextForCustomization(customization: any, keyPrefix: string): string {
        keyPrefix = keyPrefix || 'personalization';
        if (this.isPersonalizationItemEnabled(customization)) {
            return this.translateService.instant(keyPrefix + '.customization.enabled') as string;
        } else {
            return this.translateService.instant(keyPrefix + '.customization.disabled') as string;
        }
    }

    public getEnablementTextForVariation(variation: any, keyPrefix: string): string {
        keyPrefix = keyPrefix || 'personalization';

        if (this.isPersonalizationItemEnabled(variation)) {
            return this.translateService.instant(keyPrefix + '.variation.enabled') as string;
        } else {
            return this.translateService.instant(keyPrefix + '.variation.disabled') as string;
        }
    }

    public getEnablementActionTextForVariation(variation: any, keyPrefix: string): string {
        keyPrefix = keyPrefix || 'personalization';

        if (this.isPersonalizationItemEnabled(variation)) {
            return this.translateService.instant(
                keyPrefix + '.variation.options.disable'
            ) as string;
        } else {
            return this.translateService.instant(keyPrefix + '.variation.options.enable') as string;
        }
    }

    public getActivityStateForCustomization(customization: any): string {
        if (customization.status === PERSONALIZATION_MODEL_STATUS_CODES.ENABLED) {
            const startDate = new Date(customization.enabledStartDate);
            const endDate = new Date(customization.enabledEndDate);
            const startDateIsValid = moment(startDate).isValid();
            const endDateIsValid = moment(endDate).isValid();
            if (
                (!startDateIsValid && !endDateIsValid) ||
                (startDateIsValid && !endDateIsValid && moment().isAfter(startDate, 'minute')) ||
                (!startDateIsValid && endDateIsValid && moment().isBefore(endDate, 'minute')) ||
                moment().isBetween(startDate, endDate, 'minute', '[]')
            ) {
                return 'perso__status--enabled';
            } else {
                return 'perso__status--ignore';
            }
        } else {
            return 'perso__status--disabled';
        }
    }

    public getActivityStateForVariation(customization: any, variation: any): string {
        if (variation.enabled) {
            return this.getActivityStateForCustomization(customization);
        } else {
            return 'perso__status--disabled';
        }
    }

    public isItemVisible(item: any): boolean {
        return item.status !== PERSONALIZATION_MODEL_STATUS_CODES.DELETED;
    }

    public getVisibleItems(items: any): any {
        return items.filter((item) => this.isItemVisible(item));
    }

    public getValidRank(
        items: CustomizationVariation[],
        item: CustomizationVariation,
        increaseValue: number
    ): number {
        const from = items.indexOf(item);
        const delta = increaseValue < 0 ? -1 : 1;

        let increase = from + increaseValue;

        while (increase >= 0 && increase < items.length && !this.isItemVisible(items[increase])) {
            increase += delta;
        }

        increase = increase >= items.length ? items.length - 1 : increase;
        increase = increase < 0 ? 0 : increase;

        return items[increase].rank;
    }

    public getStatusesMapping(): CustomizationStatus[] {
        const statusesMapping = [];

        statusesMapping.push({
            code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL,
            text: 'personalization.context.status.all',
            modelStatuses: [
                PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
                PERSONALIZATION_MODEL_STATUS_CODES.DISABLED
            ]
        });

        statusesMapping.push({
            code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ENABLED,
            text: 'personalization.context.status.enabled',
            modelStatuses: [PERSONALIZATION_MODEL_STATUS_CODES.ENABLED]
        });

        statusesMapping.push({
            code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.DISABLED,
            text: 'personalization.context.status.disabled',
            modelStatuses: [PERSONALIZATION_MODEL_STATUS_CODES.DISABLED]
        });

        return statusesMapping;
    }

    public getClassForElement(index: any): string {
        const wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
        return PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
    }

    public getLetterForElement(index: any): string {
        const wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
        return String.fromCharCode('a'.charCodeAt(0) + wrappedIndex).toUpperCase();
    }

    public getCommerceCustomizationTooltip(variation: any, prefix: string, suffix: string): string {
        prefix = prefix || '';
        suffix = suffix || '\n';
        let result = '';
        lodash.forEach(variation.commerceCustomizations, (propertyValue, propertyKey) => {
            result +=
                prefix +
                this.translateService.instant(
                    'personalization.modal.manager.commercecustomization.' + propertyKey
                ) +
                ': ' +
                propertyValue +
                suffix;
        });
        return result;
    }

    public getCommerceCustomizationTooltipHTML(variation: any): string {
        return this.getCommerceCustomizationTooltip(variation, '<div>', '</div>');
    }

    public isItemFromCurrentCatalog(item: any, seData: any): boolean {
        const cd = seData.seExperienceData.catalogDescriptor;
        return item.catalog === cd.catalogId && item.catalogVersion === cd.catalogVersion;
    }

    public hasCommerceActions(variation: any): boolean {
        return variation.numberOfCommerceActions > 0;
    }

    public async getCatalogVersionNameByUuid(catalogVersionUuid: string): Promise<string> {
        const catalogVersion = await this.catalogService.getCatalogVersionByUuid(
            catalogVersionUuid
        );
        let catalogVersionName = await this.l10nPipe
            .transform(catalogVersion.catalogName)
            .pipe(take(1))
            .toPromise();
        catalogVersionName = catalogVersionName + ' (' + catalogVersion.version + ')';
        return catalogVersionName;
    }

    public async getAndSetCatalogVersionNameL10N(customization: any): Promise<void> {
        customization.catalogVersionNameL10N = await this.getCatalogVersionNameByUuid(
            customization.catalog + '/' + customization.catalogVersion
        );
    }

    public uniqueArray(array1: any[], array2: any[], fieldName?: string): any[] {
        fieldName = fieldName || 'code';
        array2.forEach((instance: any) => {
            if (!array1.some((el) => el[fieldName] === instance[fieldName])) {
                array1.push(instance);
            }
        });
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        return array1;
    }
}
