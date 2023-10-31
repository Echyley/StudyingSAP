/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit module as entry point.
 * @param id The module identifier used when loading it into Smartedit.
 */
export const SeEntryModule = function (id: string) {
    return function (moduleConstructor: any): any {
        window.__smartedit__.modules[id] = moduleConstructor;
        return moduleConstructor;
    };
};
