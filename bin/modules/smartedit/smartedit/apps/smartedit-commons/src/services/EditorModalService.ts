/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied } from './gatewayProxiedAnnotation';
import { IEditorModalService } from './interfaces';
@GatewayProxied('open', 'openAndRerenderSlot', 'openGenericEditor')
export class EditorModalService extends IEditorModalService {}
