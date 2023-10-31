/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CloneableUtils, LogService } from '@smart/utils';
import * as lo from 'lodash';
import { IContextualMenuButton } from './IContextualMenuButton';
import { IDecorator } from './IDecorator';
import { InternalFeature, IFeature } from './IFeature';
import { IToolbarItem } from './IToolbarItem';

/**
 * @internal
 * @ignore
 */

export interface IFeaturesToAlias {
    [index: string]: {
        enablingCallback: () => void;
        disablingCallback: () => void;
    };
}

/**
 * The interface stipulates how to register features in the SmartEdit application and the SmartEdit container.
 * The SmartEdit implementation stores two instances of the interface across the {@link GatewayFactory gateway}: one for the SmartEdit application and one for the SmartEdit container.
 */
export abstract class IFeatureService {
    protected _featuresToAlias: IFeaturesToAlias;

    constructor(
        private readonly cloneableUtils: CloneableUtils,
        protected logService: LogService
    ) {}

    /**
     * This method registers a feature.
     * When an end user selects a perspective, all the features that are bound to the perspective
     * will be enabled when their respective enablingCallback functions are invoked
     * and all the features that are not bound to the perspective will be disabled when their respective disablingCallback functions are invoked.
     * The SmartEdit application and the SmartEdit container hold/store an instance of the implementation because callbacks cannot cross the gateway as they are functions.
     *
     * this method is meant to register a feature (identified by a key).
     * When a perspective (registered through [register]{@link IPerspectiveService#register}) is selected, all its bound features will be enabled by invocation of their respective enablingCallback functions
     * and any feature not bound to it will be disabled by invocation of its disablingCallback function.
     * Both SmartEdit and SmartEditContainer will hold a concrete implementation since Callbacks, being functions, cannot cross the gateway.
     * The function will keep a frame bound reference on a full feature in order to be able to invoke its callbacks when needed.
     *
     * @param configuration Configuration of a `IContextualMenuButton` or `IDecorator` or `IToolbarItem`.
     */
    register(configuration: IFeature): Promise<void> {
        this._validate(configuration);

        this._featuresToAlias = this._featuresToAlias || {};
        this._featuresToAlias[configuration.key] = {
            enablingCallback: configuration.enablingCallback,
            disablingCallback: configuration.disablingCallback
        };

        delete configuration.enablingCallback;
        delete configuration.disablingCallback;

        return this._registerAliases(
            this.cloneableUtils.makeCloneable(configuration) as any as InternalFeature
        );
    }

    enable(key: string): void {
        if (this._featuresToAlias && this._featuresToAlias[key]) {
            this._featuresToAlias[key].enablingCallback();
        } else {
            this._remoteEnablingFromInner(key);
        }
    }

    disable(key: string): void {
        if (this._featuresToAlias && this._featuresToAlias[key]) {
            this._featuresToAlias[key].disablingCallback();
        } else {
            this._remoteDisablingFromInner(key).catch((reason) => {
                this.logService.debug(`IFeatureService - disable: ${JSON.stringify(reason)}`);
            });
        }
    }

    /**
     * @returns A promise of property value or null if property does not exist
     */
    getFeatureProperty(
        featureKey: string,
        propertyName: keyof IFeature
    ): Promise<string | string[] | (() => void)> {
        'proxyFunction';
        return null;
    }

    /**
     * This method registers toolbar items as features. It is a wrapper around [register]{@link IFeatureService#register}.
     *
     * @param configuration Configuration that represents the toolbar action item to be registered.
     */
    addToolbarItem(toolbar: IToolbarItem): Promise<void> {
        'proxyFunction';
        return null;
    }

    /**
     * This method registers decorator and delegates to the
     *  {@link /smartedit/injectables/DecoratorService.html#enable enable}
     *  {@link /smartedit/injectables/DecoratorService.html#disable disable} methods.
     * This method is not a wrapper around {@link /smartedit/injectables/DecoratorService.html#addMappings addMappings}:
     * From a feature stand point, we deal with decorators, not their mappings to SmartEdit components.
     * We still need to have a separate invocation of {@link /smartedit/injectables/DecoratorService.html#addMappings addMappings}
     */
    addDecorator(decorator: IDecorator): Promise<void> {
        'proxyFunction';
        return null;
    }

    /**
     * This method registers contextual menu buttons.
     * It is a wrapper around {@link /smartedit/injectables/ContextualMenuService.html#addItems addItems}.
     */
    addContextualMenuButton(btn: IContextualMenuButton): Promise<void> {
        'proxyFunction';
        return null;
    }

    getFeatureKeys(): string[] {
        'proxyFunction';
        return null;
    }

    protected _remoteEnablingFromInner(key: string): Promise<void> {
        'proxyFunction';
        return null;
    }
    protected _remoteDisablingFromInner(key: string): Promise<void> {
        'proxyFunction';
        return null;
    }

    /**
     * This method registers a feature, identified by a unique key, across the {@link GatewayFactory}.
     * It is a simplified version of the register method, from which callbacks have been removed.
     */
    protected _registerAliases(configuration: InternalFeature): Promise<void> {
        'proxyFunction';
        return null;
    }

    private _validate(configuration: IFeature): void {
        if (lo.isEmpty(configuration.key)) {
            throw new Error('featureService.configuration.key.error.required');
        }
        if (lo.isEmpty(configuration.nameI18nKey)) {
            throw new Error('featureService.configuration.nameI18nKey.error.required');
        }
        if (!lo.isFunction(configuration.enablingCallback)) {
            throw new Error('featureService.configuration.enablingCallback.error.not.function');
        }
        if (!lo.isFunction(configuration.disablingCallback)) {
            throw new Error('featureService.configuration.disablingCallback.error.not.function');
        }
    }
}
