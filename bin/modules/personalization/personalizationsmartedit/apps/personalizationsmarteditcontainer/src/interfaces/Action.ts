/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export interface BaseAction {
    type: string;
    code: string;
}

export interface Action {
    action: BaseAction;
    status: string;
}
