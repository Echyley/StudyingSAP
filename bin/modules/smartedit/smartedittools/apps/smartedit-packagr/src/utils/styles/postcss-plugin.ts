/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as postcss from 'postcss';
import { UrlToRewrite } from '../../configuration';
import { copyToAssetsFolder } from '../assets';
import { resolvePath } from '../path';

const path = require('path');
const postcssHelpers = require('postcss-helpers');
const minimatch = require('minimatch');

/**
 * This file contains a plugin used in PostCSS to rewrite URLs found while processing .css files. This might be helpful when importing a
 * stylesheet from an external library; the URLs in the stylesheet probably are not relative to the current URL and they might not be
 * properly referenced when used in SmartEdit. By rewriting the URL we ensure that it is relative to the current library and that the
 * assets can be found whenever needed in SmartEdit.
 *
 * The plugin accepts an object of type PostcssRewriteUrlPluginOptions as input, which contains an array with the rules to rewrite URLs.
 * Each rule contains a pattern that the plugin will use to identify the URLs that need rewriting and a path to the location where the
 * assets being referenced reside. For example, the following rule is telling the plugin to rewrite any URL matching '../../img/*.png'
 * and that the assets are actually found in the path 'node_modules/example/img/'.
 *  {
 *      urlMatcher: '../../img/*.png',
 *      assetsLocation: 'node_modules/example/img/'
 *  }
 *
 * Please note that whenever a URL is rewritten, the corresponding asset is copied over to the /dist/assets folder.
 *
 * Notes:
 * - This is not a Rollup plugin. Instead, it is a PostCSS plugin.
 * - In practice, it is recommended to rewrite URLs with this approach for .css or .sass files only. Urls in .less files should be
 *   processed by the less-plugin.
 * - This is executed after SASS files have been compiled.
 */

export interface PostcssRewriteUrlPluginOptions {
    urlsToRewrite: UrlToRewrite[];
}

const isUrl = (declarationValue: string) => {
    return declarationValue.match(postcssHelpers.regexp.URLS);
};

const findMatchingRewritingRule = (url: any, urlsToRewrite: UrlToRewrite[]) => {
    const urlPath = url && url.path();
    if (!urlPath) {
        return null;
    }

    return urlsToRewrite.find((rewriteRule: UrlToRewrite) => {
        return minimatch(urlPath, rewriteRule.urlMatcher);
    });
};

// the previous 'plugin' method is deleted in postcss:v8
export const postcssRewriteUrlPlugin = (options: any) => {
    return {
        postcssPlugin: 'postcssRewriteUrlPlugin',
        Once(root: postcss.Root, result: postcss.Result) {
            root.walkDecls((decl: postcss.Declaration) => {
                if (isUrl(decl.value)) {
                    const urlHelper = postcssHelpers.createUrlsHelper(decl.value);
                    const uri = urlHelper.URIS[0];
                    const rewritingRule = findMatchingRewritingRule(uri, options.urlsToRewrite);
                    if (rewritingRule) {
                        // Find matching assets
                        const assetName = path.basename(uri.path());
                        const assetPath = resolvePath(
                            path.join(rewritingRule.assetsLocation, assetName)
                        );
                        // Copy assets
                        const newPath = copyToAssetsFolder(assetPath);
                        // Rewrite URL
                        decl.value = `url("${newPath}")`;
                    }
                }
            });
        }
    };
};
