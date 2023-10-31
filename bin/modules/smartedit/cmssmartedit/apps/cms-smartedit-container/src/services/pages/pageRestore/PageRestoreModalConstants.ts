/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CMSItemStructureField } from 'cmscommons';

export interface ErrorMapping {
    subject: string;
    errorCode: string;
}

export interface ActionableErrorMapping extends ErrorMapping {
    structure: CMSItemStructureField;
}

export interface NonActionableErrorMapping extends ErrorMapping {
    messageKey: string;
}

export const ACTIONABLE_ERRORS: ActionableErrorMapping[] = [
    {
        subject: 'name',
        errorCode: 'field.already.exist',
        structure: {
            cmsStructureType: 'ShortString',
            collection: false,
            editable: true,
            i18nKey: 'type.cmsitem.name.name',
            localized: false,
            paged: false,
            qualifier: 'name',
            required: true
        }
    },
    {
        subject: 'label',
        errorCode: 'default.page.label.already.exist',
        structure: {
            cmsStructureType: 'DuplicatePrimaryContentPage',
            collection: false,
            editable: true,
            i18nKey: 'se.cms.page.restore.content.duplicate.primaryforvariation.label',
            localized: false,
            paged: false,
            qualifier: 'label',
            required: true
        }
    },
    {
        subject: 'label',
        errorCode: 'default.page.does.not.exist',
        structure: {
            cmsStructureType: 'MissingPrimaryContentPage',
            collection: false,
            editable: true,
            i18nKey: 'se.cms.page.restore.content.noprimaryforvariation.label',
            localized: false,
            paged: false,
            qualifier: 'label',
            required: true
        }
    },
    {
        subject: 'typeCode',
        errorCode: 'default.page.already.exist',
        structure: {
            cmsStructureType: 'DuplicatePrimaryNonContentPageMessage',
            collection: false,
            editable: true,
            i18nKey: 'type.cmsitem.label.name',
            localized: false,
            paged: false,
            qualifier: 'replace',
            required: true
        }
    }
];

export const NON_ACTIONABLE_ERRORS: NonActionableErrorMapping[] = [
    {
        subject: 'typeCode',
        errorCode: 'default.page.does.not.exist',
        messageKey: 'se.cms.page.restore.noprimaryforvariation.error'
    }
];
