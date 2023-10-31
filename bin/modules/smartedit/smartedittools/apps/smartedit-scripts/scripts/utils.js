/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
'use strict';

const path = require('path');
const afs = require('fs').promises;

const createFolder = async (path) => {
    await afs.mkdir(path, { recursive: true });
};

const copyDir = async (src, dest) => {
    if ((await afs.lstat(src)).isDirectory()) {
        const entries = await afs.readdir(src, { withFileTypes: true });
        await createFolder(dest);
        for (const entry of entries) {
            const srcPath = path.join(src, entry.name);
            const destPath = path.join(dest, entry.name);
            if (entry.isDirectory()) {
                await copyDir(srcPath, destPath);
            } else {
                await afs.copyFile(srcPath, destPath);
            }
        }
    } else {
        await afs.copyFile(src, dest);
    }
};

exports.createFolder = createFolder;
exports.copyDir = copyDir;
