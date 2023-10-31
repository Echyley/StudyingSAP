/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const path = require('path');
const fs = require('fs');

const utils = {
    keysConfigLoop(keys, config, action) {
        keys = keys.split('.');

        const root = {
            ...(config || {})
        };

        const lastIndex = keys.length - 1;
        let current = root;

        keys.forEach((key, index) => {
            if (index === lastIndex) {
                action(current, utils.decodeDot(key));
            } else {
                current[key] = {
                    ...(current[key] || {})
                };
                current = current[key];
            }
        });

        return root;
    },
    encodeDot(string) {
        return string.replace(/\./g, `\\#`);
    },
    decodeDot(string) {
        return string.replace(/\\#/g, `.`);
    },
    getFocusedDirs(dir) {
        function isFileFocused(path) {
            const content = fs.readFileSync(path, { encoding: 'UTF-8' });

            return content.includes('fdescribe') || content.includes('fit');
        }

        return fs.readdirSync(dir).reduce((paths, file) => {
            const fullPath = path.join(dir, file);

            if (fs.lstatSync(fullPath).isDirectory()) {
                return [...paths, ...this.getFocusedDirs(fullPath)];
            } else if (/Test.ts/.test(fullPath) && isFileFocused(fullPath)) {
                return [...paths, path.dirname(fullPath)];
            } else {
                return [...paths];
            }
        }, []);
    }
};

module.exports = utils;
