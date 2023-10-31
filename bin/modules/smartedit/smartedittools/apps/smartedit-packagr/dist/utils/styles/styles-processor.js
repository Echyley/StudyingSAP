"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.createStyleProcessor = exports.StylesProcessor = void 0;
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const readFile_1 = require("../readFile");
const path_1 = require("../path");
const path_2 = require("path");
const less_plugin_1 = require("./less-plugin");
const postcss_plugin_1 = require("./postcss-plugin");
const sass = require('sass');
const less = require('less');
const postcss = require('postcss');
const cleanCss = new (require('clean-css'))();
class StylesProcessor {
    constructor(globalStyles = '') {
        this.globalStyles = globalStyles;
        this.supportedSassExtension = ['.scss', '.css', '.sass'];
        this.lessExtension = '.less';
        this.tildeImporter = (url) => {
            if (url.charAt(0) === '~') {
                return {
                    file: path_2.join('node_modules', url.substr(1))
                };
            }
            return null;
        };
    }
    compileSass(styles, filePath) {
        const data = this.globalStyles + styles;
        if (!data) {
            return '';
        }
        const basePath = path_1.resolveBasePath();
        return sass.renderSync({
            data,
            importer: this.tildeImporter,
            includePaths: [filePath, basePath]
        }).css;
    }
    compileInnerSass(styles, filePath) {
        const css = this.compileSass(styles, filePath);
        return cleanCss.minify(css).styles;
    }
    async compileLess(styles, filePath) {
        const basePath = path_1.resolveBasePath();
        return less.render(styles, {
            paths: [filePath, basePath],
            plugins: [less_plugin_1.getRewriteUrlPlugin(filePath)]
        });
    }
    isSassOrCssFile(path) {
        return this.supportedSassExtension.some((extension) => {
            return path.endsWith(extension);
        });
    }
    isLessFile(path) {
        return path.endsWith(this.lessExtension);
    }
    async rewriteUrls(css, urlsToRewrite) {
        if (urlsToRewrite.length === 0) {
            return css;
        }
        const result = await postcss(postcss_plugin_1.postcssRewriteUrlPlugin({ urlsToRewrite })).process(css, {
            from: undefined
        });
        return result.css;
    }
}
exports.StylesProcessor = StylesProcessor;
const createStyleProcessor = async (globalStylesFile) => {
    const globalStyle = globalStylesFile ? await readFile_1.readFile(globalStylesFile) : '';
    return new StylesProcessor(globalStyle);
};
exports.createStyleProcessor = createStyleProcessor;
