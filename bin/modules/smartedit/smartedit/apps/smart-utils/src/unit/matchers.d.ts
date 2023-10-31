/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
declare namespace jasmine {
    interface Matchers<T> {
        toJsonEqual(expected: any, expectationFailOutput?: any): boolean;
    }
}
