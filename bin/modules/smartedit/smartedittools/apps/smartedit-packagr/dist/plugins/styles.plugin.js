"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.styles = void 0;
const path = require('path');
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
const styles = (stylesProcessor, config) => {
    // Files must be processed in order. However, processing of files can be finished in different order.
    // This array keeps track of the files in the original order.
    const rawStyles = [];
    const addFileContent = (fileId, content) => {
        const styleFileFound = rawStyles.find((file) => {
            return file.id === fileId;
        });
        if (!styleFileFound) {
            throw new Error('Error processing style file. Please check bundler configuration and try again.');
        }
        styleFileFound.content = content;
    };
    return {
        name: 'styles',
        async transform(code, id) {
            const filePath = path.dirname(id);
            try {
                if (stylesProcessor.isSassOrCssFile(id)) {
                    rawStyles.push({ id });
                    const content = await stylesProcessor.compileSass(code, filePath);
                    addFileContent(id, content);
                    return '';
                }
                else if (stylesProcessor.isLessFile(id)) {
                    rawStyles.push({ id });
                    const result = await stylesProcessor.compileLess(code, filePath);
                    addFileContent(id, result.css);
                    return '';
                }
            }
            catch (err) {
                throw new Error(`Cannot process styles file at ${filePath}. Error: ` + err);
            }
            return null;
        },
        async generateBundle() {
            let styles = rawStyles.reduce((acc, current) => {
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
exports.styles = styles;
