/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TypedMap } from 'smarteditcommons';

export class BaseContextualMenuComponent {
    protected remainOpenMap: TypedMap<boolean> = {};

    isHybrisIcon(icon: string): boolean {
        return icon && icon.indexOf('hyicon') >= 0;
    }

    /*
     setRemainOpen receives a key name and a boolean value
     the button name needs to be unique across all buttons so it won' t collide with other button actions.
     */
    setRemainOpen(key: string, remainOpen: boolean): void {
        this.remainOpenMap[key] = remainOpen;
    }

    showOverlay(active: boolean): boolean {
        if (active) {
            return true;
        }

        return Object.keys(this.remainOpenMap).reduce(
            (isOpen: boolean, key: string) => isOpen || this.remainOpenMap[key],
            false
        );
    }
}
