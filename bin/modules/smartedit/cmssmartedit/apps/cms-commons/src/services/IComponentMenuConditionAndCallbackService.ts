/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IContextualMenuConfiguration } from 'smarteditcommons';

export abstract class IComponentMenuConditionAndCallbackService {
    public externalCondition(configuration: IContextualMenuConfiguration): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    public sharedCondition(configuration: IContextualMenuConfiguration): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    public removeCondition(configuration: IContextualMenuConfiguration): Promise<boolean> {
        'proxyFunction';
        return null;
    }
    public removeCallback(
        configuration: IContextualMenuConfiguration,
        $event: Event
    ): Promise<void> {
        'proxyFunction';
        return null;
    }
    public cloneCondition(configuration: IContextualMenuConfiguration): Promise<boolean> {
        'proxyFunction';
        return null;
    }
    public cloneCallback(configuration: IContextualMenuConfiguration): Promise<void> {
        'proxyFunction';
        return null;
    }

    public editConditionForHiddenComponent(
        configuration: IContextualMenuConfiguration
    ): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    public dragCondition(configuration: IContextualMenuConfiguration): Promise<boolean> {
        'proxyFunction';
        return null;
    }
}
