/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Provides an abstract extensible user tracking service. Used to manage and perform actions to either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */

export abstract class IUserTrackingService {
    /// //////////////////////////////////
    // Proxied Functions : these functions will be proxied if left unimplemented
    /// //////////////////////////////////
    initConfiguration(): Promise<void> {
        'proxyFunction';
        return null;
    }

    trackingUserAction(functionality: string, key: string): void {
        'proxyFunction';
    }
}
