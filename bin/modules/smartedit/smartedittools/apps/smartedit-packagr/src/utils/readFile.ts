/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { resolvePath } from './path';

const fs = require('fs');
const util = require('util');

const baseReadFile = util.promisify(fs.readFile);

export function readFile(path: string): Promise<string> {
    const filePath = resolvePath(path);
    return baseReadFile(filePath, 'utf8');
}
