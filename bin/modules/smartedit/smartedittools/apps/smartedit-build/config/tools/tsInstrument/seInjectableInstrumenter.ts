/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { readFileSync, statSync, utimesSync, writeFileSync } from 'fs';
import * as ts from 'typescript';
import * as lodash from 'lodash';

/**
 * @SeInjectabe TypeScript instrumenter
 * This module visit the TypeScript source code to search for DI-specific class decorators like @SeInjectable() and @SeComponent;
 * It will instrument the class to as to be usable by Smartedit DI and, more precisely, by underlying
 * AngularJS 1.6 DI
 *
 */
module.exports = function (fileNames: string[], injectableDecorators: string[]) {
    const IGNORE_DECORATORS = ['SeModule', 'NgModule'];
    const DELEGATING_HINT = '/* @ngInject */';

    const program = ts.createProgram(fileNames, {
        target: ts.ScriptTarget.ES2016,
        module: ts.ModuleKind.CommonJS
    });
    const checker = program.getTypeChecker(); // do not remove, it's necessary to have type check
    for (const sourceFile of program.getSourceFiles()) {
        ts.forEachChild(sourceFile, walkNode);
    }

    interface DecoratorData {
        name: string;
        originalArguments: any;
        arguments: any;
        shouldReplaceTemplateUrl: boolean;
    }
    function walkNode(node: ts.Declaration) {
        if (!isNodeExported(node)) {
            return;
        }

        if (node.kind === ts.SyntaxKind.ClassDeclaration && ts.isClassDeclaration(node)) {
            const classDeclaration = node as ts.ClassDeclaration;

            if (classDeclaration.decorators && classDeclaration.decorators.length) {
                const classDecorators = classDeclaration.decorators
                    .filter((decorator) => ts.isCallExpression(decorator.expression))
                    .map(serializeDecorator);
                if (
                    lodash.intersection(
                        classDecorators.map((d) => d.name),
                        injectableDecorators
                    ).length
                ) {
                    const fileName: string = node.getSourceFile().fileName;
                    const className: string = node.name.getFullText().trim();
                    const intersectDecorators = classDecorators.filter(
                        (c) => injectableDecorators.indexOf(c.name) > -1
                    );

                    const source = getTransformedSource(
                        node,
                        fileName,
                        className,
                        intersectDecorators
                    );
                    const stats = statSync(fileName);

                    writeFileSync(fileName, source);
                    utimesSync(fileName, stats.atime, stats.mtime);
                }
            }
        } else if (node.kind === ts.SyntaxKind.ModuleDeclaration) {
            // This is a namespace, visit its children
            ts.forEachChild(node, walkNode);
        }
    }

    function getTemplaterUrlRegexp() {
        return /templateUrl\s*:\s*['"`](.*)['"`]/gm;
    }

    function getRequiredReadyTemplaterUrlRegexp() {
        // references an absolute URL
        return /templateUrl:[\s\S]*http[s]*:\/\/.+/;
    }

    function serializeDecorator(decorator: ts.Decorator): DecoratorData {
        const name = checker.getSymbolAtLocation(decorator.expression.getFirstToken()).getName();
        const originalArguments = decorator.expression
            .getFullText()
            .replace(/^[a-zA-Z]+\(/, '')
            .replace(/\)$/, '');
        const shouldReplaceTemplateUrl =
            name === 'Component' &&
            getTemplaterUrlRegexp().test(decorator.expression.getFullText()) &&
            !getRequiredReadyTemplaterUrlRegexp().test(decorator.expression.getFullText());

        return {
            name,
            originalArguments,
            arguments: shouldReplaceTemplateUrl
                ? originalArguments.replace(getTemplaterUrlRegexp(), "template: require('$1'),")
                : originalArguments,
            shouldReplaceTemplateUrl
        };
    }

    function getTransformedSource(
        node: ts.Node,
        fileName: string,
        className: string,
        classDecorators: DecoratorData[]
    ): string {
        let fullText: string = readFileSync(fileName).toString();

        classDecorators.forEach((d) => {
            if (d.arguments && !isDecoratorIgnored(d.name)) {
                const index = fullText.indexOf(node.getFullText());
                fullText =
                    fullText.substr(0, index) +
                    `\r(window as any).__smartedit__.addDecoratorPayload('${d.name}', '${className}', ${d.arguments});` +
                    fullText.substr(index);
            }
            if (d.shouldReplaceTemplateUrl) {
                // replace templateUrl by template + require
                fullText = fullText.replace(d.originalArguments, d.arguments);
            }
        });

        if (classDecorators.find((d) => d.name.startsWith('Se') || d.name === 'Injectable')) {
            return fullText.replace(
                new RegExp('(export class ' + className + ')', 'g'),
                `export class ${DELEGATING_HINT} ${className}`
            );
        } else {
            return fullText;
        }
    }

    function isDecoratorIgnored(decoratorName: string): boolean {
        return IGNORE_DECORATORS.indexOf(decoratorName) > -1;
    }

    function isNodeExported(node: ts.Declaration): boolean {
        return (
            (ts.getCombinedModifierFlags(node) & ts.ModifierFlags.Export) !== 0 ||
            (!!node.parent && node.parent.kind === ts.SyntaxKind.SourceFile)
        );
    }
};
