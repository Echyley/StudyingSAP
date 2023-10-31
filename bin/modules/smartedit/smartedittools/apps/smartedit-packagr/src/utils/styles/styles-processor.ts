/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { readFile } from '../readFile';
import { resolveBasePath } from '../path';
import { join } from 'path';
import { getRewriteUrlPlugin } from './less-plugin';
import { UrlToRewrite } from '../../configuration';
import { postcssRewriteUrlPlugin } from './postcss-plugin';

const sass = require('sass');
const less = require('less');
const postcss = require('postcss');
const cleanCss = new (require('clean-css'))();

export class StylesProcessor {
    private supportedSassExtension = ['.scss', '.css', '.sass'];
    private lessExtension = '.less';

    constructor(private globalStyles = '') {}

    public tildeImporter = (url: string) => {
        if (url.charAt(0) === '~') {
            return {
                file: join('node_modules', url.substr(1))
            };
        }

        return null;
    };

    public compileSass(styles: string, filePath: string) {
        const data = this.globalStyles + styles;

        if (!data) {
            return '';
        }

        const basePath = resolveBasePath();

        return sass.renderSync({
            data,
            importer: this.tildeImporter,
            includePaths: [filePath, basePath]
        }).css;
    }

    public compileInnerSass(styles: string, filePath: string) {
        const css = this.compileSass(styles, filePath);
        return cleanCss.minify(css).styles;
    }

    public async compileLess(styles: string, filePath: string) {
        const basePath = resolveBasePath();

        return less.render(styles, {
            paths: [filePath, basePath],
            plugins: [getRewriteUrlPlugin(filePath)]
        });
    }

    public isSassOrCssFile(path: string) {
        return this.supportedSassExtension.some((extension: string) => {
            return path.endsWith(extension);
        });
    }

    public isLessFile(path: string) {
        return path.endsWith(this.lessExtension);
    }

    public async rewriteUrls(css: string, urlsToRewrite: UrlToRewrite[]) {
        if (urlsToRewrite.length === 0) {
            return css;
        }

        const result = await postcss(postcssRewriteUrlPlugin({ urlsToRewrite })).process(css, {
            from: undefined
        });

        return result.css;
    }
}

export const createStyleProcessor = async (globalStylesFile?: string) => {
    const globalStyle = globalStylesFile ? await readFile(globalStylesFile) : '';
    return new StylesProcessor(globalStyle);
};
