/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/** @internal */
import { CacheAction } from '../../../services/cache/cache-action';

export const RarelyChangingContentName = 'RarelyChangingContent';

export const rarelyChangingContent = new CacheAction(RarelyChangingContentName);
