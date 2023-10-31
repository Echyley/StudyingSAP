/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { GatewayProxied, IEditorModalService } from 'smarteditcommons';

@GatewayProxied('open', 'openAndRerenderSlot', 'openGenericEditor')
@Injectable()
export class EditorModalService extends IEditorModalService {}
