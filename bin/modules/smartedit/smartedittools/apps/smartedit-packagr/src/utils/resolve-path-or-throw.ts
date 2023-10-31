/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { resolvePath } from './path';
import { access as accessFs } from 'fs';
import { promisify } from 'util';

const accessPromise = promisify(accessFs);

/**
 * Checks if path exists or throws error message.
 *
 * @param file
 * @param errorMessage
 */
export const resolvePathOrThrow = async (file: string, errorMessage: string): Promise<string> => {
    const absolutePath = resolvePath(file);

    try {
        await accessPromise(absolutePath);
    } catch (err) {
        throw new Error(`The file does not exists at: ${absolutePath}. ${errorMessage || ''}`);
    }

    return absolutePath;
};
