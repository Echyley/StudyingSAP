/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ICMSComponent } from 'cmscommons';

/**
 * Service used to determine if a component is shared.
 */
export abstract class IComponentSharedService {
    /**
     * This method is used to determine if a component is shared.
     * A component is considered shared if it is used in two or more content slots.
     */
    isComponentShared(component: string | ICMSComponent): Promise<boolean> {
        'proxyFunction';
        return null;
    }
}
