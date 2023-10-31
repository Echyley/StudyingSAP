/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
'use strict';

const path = require('path');
const { promises: fs } = require('fs');

// Variables
const BASE_EXTENSION_PATH = process.argv[2];
const RUSH_TEMPLATE_FILE_PATH = process.argv[3];
const RUSH_FILE_PATH = path.join(BASE_EXTENSION_PATH, 'rush.json');

const readJSON = async (file) => {
    try {
        return JSON.parse(await fs.readFile(file));
    } catch (err) {
        console.error(`Error reading JSON file ${file}.`);
        throw err;
    }
};

const writeJSON = (file, data) => {
    return fs.writeFile(file, JSON.stringify(data, null, 2));
};

const updateRushJson = async () => {
    const rushJsonTemplate = await readJSON(RUSH_TEMPLATE_FILE_PATH);
    const rushJson = await readJSON(RUSH_FILE_PATH);

    rushJson.rushVersion = rushJsonTemplate.rushVersion;
    rushJson.pnpmVersion = rushJsonTemplate.pnpmVersion;

    await writeJSON(RUSH_FILE_PATH, rushJson);
};

(async () => {
    await updateRushJson();
})().catch((e) => {
    console.error(e);
    process.exit(1);
});
