/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as rollup from 'rollup';
import { StyleConfiguration } from '../configuration';
import { StylesProcessor } from '../utils';

const path = require('path');

interface StyleFile {
    id: string;
    content?: string;
}

/**
 * Styles plugin
 *
 * This Rollup plugin is used to compile .less, .sass, or .css files. It will find any file of these types, compile them,
 * and concatenate them. Finally, it will write the result to a file called styles.css.
 *
 * Notes:
 * - This plugin can rewrite URLs. The way they are rewritten varies between .less and between .scss and .css files. More information
 *   can be found in the postcss-plugin.ts (.scss and .css files) and in the less-plugin.ts (.less files).
 */
export const styles = (
    stylesProcessor: StylesProcessor,
    config?: StyleConfiguration
): rollup.Plugin => {
    // Files must be processed in order. However, processing of files can be finished in different order.
    // This array keeps track of the files in the original order.
    const rawStyles: StyleFile[] = [];

    const addFileContent = (fileId: string, content: string) => {
        const styleFileFound = rawStyles.find((file: StyleFile) => {
            return file.id === fileId;
        });

        if (!styleFileFound) {
            throw new Error(
                'Error processing style file. Please check bundler configuration and try again.'
            );
        }

        styleFileFound.content = content;
    };

    return {
        name: 'styles',
        async transform(code: string, id: string) {
            const filePath = path.dirname(id);

            try {
                if (stylesProcessor.isSassOrCssFile(id)) {
                    rawStyles.push({ id });
                    const content = await stylesProcessor.compileSass(code, filePath);
                    addFileContent(id, content);
                    return '';
                } else if (stylesProcessor.isLessFile(id)) {
                    rawStyles.push({ id });
                    const result = await stylesProcessor.compileLess(code, filePath);
                    addFileContent(id, result.css);
                    return '';
                }
            } catch (err) {
                throw new Error(`Cannot process styles file at ${filePath}. Error: ` + err);
            }
            return null;
        },
        async generateBundle() {
            let styles = rawStyles.reduce((acc: string, current: StyleFile) => {
                return acc + current.content;
            }, '');

            if (config && config.urlsToRewrite) {
                styles = await stylesProcessor.rewriteUrls(styles, config.urlsToRewrite);
            }

            this.emitFile({
                type: 'asset',
                source: styles,
                fileName: 'styles.css'
            });
        }
    };
};
