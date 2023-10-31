"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.postcssRewriteUrlPlugin = void 0;
const assets_1 = require("../assets");
const path_1 = require("../path");
const path = require('path');
const postcssHelpers = require('postcss-helpers');
const minimatch = require('minimatch');
const isUrl = (declarationValue) => {
    return declarationValue.match(postcssHelpers.regexp.URLS);
};
const findMatchingRewritingRule = (url, urlsToRewrite) => {
    const urlPath = url && url.path();
    if (!urlPath) {
        return null;
    }
    return urlsToRewrite.find((rewriteRule) => {
        return minimatch(urlPath, rewriteRule.urlMatcher);
    });
};
// the previous 'plugin' method is deleted in postcss:v8
const postcssRewriteUrlPlugin = (options) => {
    return {
        postcssPlugin: 'postcssRewriteUrlPlugin',
        Once(root, result) {
            root.walkDecls((decl) => {
                if (isUrl(decl.value)) {
                    const urlHelper = postcssHelpers.createUrlsHelper(decl.value);
                    const uri = urlHelper.URIS[0];
                    const rewritingRule = findMatchingRewritingRule(uri, options.urlsToRewrite);
                    if (rewritingRule) {
                        // Find matching assets
                        const assetName = path.basename(uri.path());
                        const assetPath = path_1.resolvePath(path.join(rewritingRule.assetsLocation, assetName));
                        // Copy assets
                        const newPath = assets_1.copyToAssetsFolder(assetPath);
                        // Rewrite URL
                        decl.value = `url("${newPath}")`;
                    }
                }
            });
        }
    };
};
exports.postcssRewriteUrlPlugin = postcssRewriteUrlPlugin;
