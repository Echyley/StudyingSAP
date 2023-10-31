/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
'use strict';
import { XMLParser, XMLBuilder } from 'fast-xml-parser';
const path = require('path');
const afs = require('fs').promises;

// Variables
const args = process.argv.slice(2);
const getArgByName = (argName) => {
    const arg = args.find((arg) => arg.indexOf(`-${argName}=`) === 0);
    return arg && arg.substr(arg.indexOf('=') + 1);
};
const EXTENSION_NAME = getArgByName('extName') || null;
const LEGACY_WEB_FEATURES = getArgByName('legacyWebFeatures')
    ? JSON.parse(getArgByName('legacyWebFeatures'))
    : null;

if (!EXTENSION_NAME || !LEGACY_WEB_FEATURES) {
    console.error('Error: -extName and -legacyWebFeatures must be defined !');
    console.error(
        'Example: update-buildcallbacks.js -extName=myExtension -legacyWebFeatures={INNER:..., OUTER:..., COMMONS:...}'
    );
    process.exit(1);
}

/**
 * Update the extension buildcallbacks.xml:
 * - The macrodef *_before_build is updated to call the macro `<build_smartedit_extension ....>`
 * - The macrodef *_before_clean is updated to cleanup the extension legacy webroot folder.
 */
const updateBuildCallbacksXml = async () => {
    const buildcallbacksXmlFile = 'buildcallbacks.xml';
    try {
        const parser = new XMLParser();
        const builder = new XMLBuilder();
        const buildcallbacks = await afs.readFile(buildcallbacksXmlFile);
        let xml = await parser.parse(buildcallbacks);
        if (xml.project) {
            addTestExtension(xml.project);
            if (xml.project.macrodef) {
                const macroDefs = xml.project.macrodef;
                setXmlMacroDefBeforeBuild(macroDefs);
                setXmlMacroDefBeforeClean(macroDefs);
                setXmlMacroDefBeforeYunit(macroDefs);
            }
            xml = builder.build(xml);
            await afs.writeFile(buildcallbacksXmlFile, xml);
        }
    } catch (e) {
        console.error(`Unable to update ${buildcallbacksXmlFile}`);
        console.error(e);
    }
};

const setXmlMacroDefBeforeBuild = (macroDefs) => {
    const macroDef = getOrCreateMacroDefByName(macroDefs, `${EXTENSION_NAME}_before_build`);
    macroDef.sequential = {
        build_smartedit_extension: {
            $: {
                extensionName: EXTENSION_NAME,
                appNames: Object.values(LEGACY_WEB_FEATURES)
            }
        }
    };
};

const setXmlMacroDefBeforeClean = (macroDefs) => {
    const macroDef = getOrCreateMacroDefByName(macroDefs, `${EXTENSION_NAME}_before_clean`);
    macroDef.sequential = {
        if: {
            available: {
                $: {
                    file:
                        '${ext.smartedittools.path}${file.separator}common${file.separator}temp${file.separator}node_modules',
                    type: 'dir'
                }
            },
            then: {
                delete: [
                    {
                        $: {
                            dir: '${ext.%EXTENSION_NAME%.path}${file.separator}web${file.separator}webroot${file.separator}%EXTENSION_NAME%${file.separator}js'.replace(
                                /%EXTENSION_NAME%/g,
                                EXTENSION_NAME
                            )
                        }
                    },
                    {
                        $: {
                            dir: '${ext.%EXTENSION_NAME%.path}${file.separator}web${file.separator}webroot${file.separator}css'.replace(
                                /%EXTENSION_NAME%/g,
                                EXTENSION_NAME
                            )
                        }
                    }
                ]
            }
        }
    };
};

const setXmlMacroDefBeforeYunit = (macroDefs) => {
    const macroDef = getOrCreateMacroDefByName(macroDefs, `${EXTENSION_NAME}_before_yunit`);
    macroDef.sequential = {
        if: {
            istrue: {
                $: {
                    value: '${testclasses.web}'
                }
            },
            then: {
                test_smartedit_extension: {
                    $: {
                        extensionName: EXTENSION_NAME,
                        appNames: Object.values(LEGACY_WEB_FEATURES)
                    }
                }
            }
        }
    };
};

const getOrCreateMacroDefByName = (macroDefs, macroName) => {
    let macroDef = macroDefs.find((macro) => macro.$.name === macroName);
    if (!macroDef) {
        macroDef = {
            $: {
                name: macroName
            }
        };
        macroDefs.push(macroDef);
    }
    return macroDef;
};

const addTestExtension = (project) => {
    project.target = {
        $: {
            name: `test_${EXTENSION_NAME}`
        },
        test_smartedit_extension: {
            $: {
                extensionName: EXTENSION_NAME,
                appNames: Object.values(LEGACY_WEB_FEATURES)
            }
        }
    };
};

(async () => {
    console.log('Updating buildcallbacks.xml...');
    await updateBuildCallbacksXml();
})();
