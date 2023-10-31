/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CacheAction } from '@smart/utils';

/**
 * @internal
 * @ignore
 */
export const RarelyChangingContentName = 'RarelyChangingContent';

export const rarelyChangingContent = new CacheAction(RarelyChangingContentName);
