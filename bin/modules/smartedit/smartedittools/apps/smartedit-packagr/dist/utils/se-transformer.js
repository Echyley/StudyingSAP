"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.smartEditTransformerFactory = exports.smartEditTransformer = void 0;
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const ts = require("typescript");
/**
 * If a class has one of these decorators then it should be annotated with @ngInject
 */
const decoratorsRequiringNgInject = new Set([
    'SeInjectable',
    'SeDirective',
    'SeModule',
    /**
     * For cross-framework dependency injection some Angular services must be annotated
     * with @ngInject
     */
    'Injectable'
]);
/**
 * If a class has one of these decorators then the class must be "augmented" with more information
 * so that the SmartEdit dependency injection can do some downgrading for custom registration.
 */
const decoratorsRequiringAugmentation = new Set(['Injectable', 'Component']);
/**
 * Object that holds information about a decorator.
 */
class DecoratorData {
    constructor(decoratorName, metadata) {
        this.decoratorName = decoratorName;
        this.metadata = metadata;
    }
}
const getDecoratorName = (node) => {
    if (ts.isIdentifier(node)) {
        return node.getText();
    }
    if (node.getChildCount() === 0) {
        return null;
    }
    return node.forEachChild((child) => {
        return getDecoratorName(child);
    });
};
const getDecoratorObject = (node) => {
    if (ts.isObjectLiteralExpression(node)) {
        return node;
    }
    if (node.getChildCount() === 0) {
        return null;
    }
    return node.forEachChild((child) => {
        return getDecoratorObject(child);
    });
};
const augmentDecorator = (node, decoratorToAugment) => {
    const className = ts.forEachChild(node, (child) => {
        if (ts.isIdentifier(child)) {
            return child.getText();
        }
    });
    /**
     * Creates the equivalent statement:
     * @example
     * (window as any).__smartedit__.addDecoratorPayload(
     *  'Component',
     *  'TacosComponent',
     *  {
     *    selector: 'se-tacos',
     *    template: '<span>me gusta</span>
     *  }
     * )
     */
    const statement = ts.createExpressionStatement(ts.createCall(ts.createPropertyAccess(ts.createPropertyAccess(ts.createParen(ts.createAsExpression(ts.createIdentifier('window'), ts.createKeywordTypeNode(ts.SyntaxKind.AnyKeyword))), ts.createIdentifier('__smartedit__')), ts.createIdentifier('addDecoratorPayload')), undefined, [
        ts.createStringLiteral(decoratorToAugment.decoratorName),
        ts.createStringLiteral(className),
        decoratorToAugment.metadata
    ]));
    return statement;
};
const addNgInject = (node, context) => {
    const visitor = (child) => {
        if (ts.isIdentifier(child)) {
            return ts.createIdentifier(`/* @ngInject */ ${child.getText()}`);
        }
        return child;
    };
    return ts.visitEachChild(node, visitor, context);
};
const buildResultNode = (statement, node) => {
    return statement ? [statement, node] : node;
};
const transformClass = (node, context) => {
    let requiresNgInject = false;
    let decoratorToAugment = null;
    ts.forEachChild(node, (child) => {
        if (!ts.isDecorator(child)) {
            return;
        }
        const decoratorName = getDecoratorName(child);
        if (!decoratorName) {
            return;
        }
        if (decoratorsRequiringNgInject.has(decoratorName)) {
            requiresNgInject = true;
        }
        if (!decoratorsRequiringAugmentation.has(decoratorName)) {
            return;
        }
        const metadata = getDecoratorObject(child);
        if (metadata) {
            const mutableMetadata = ts.getMutableClone(metadata);
            decoratorToAugment = new DecoratorData(decoratorName, mutableMetadata);
        }
    });
    let statement = null;
    if (decoratorToAugment) {
        statement = augmentDecorator(node, decoratorToAugment);
    }
    if (requiresNgInject) {
        return buildResultNode(statement, addNgInject(node, context));
    }
    return buildResultNode(statement, node);
};
/**
 *
 * @param statements
 */
const smartEditTransformer = () => {
    return (context) => {
        return (sourceFile) => {
            const visitor = (node) => {
                if (ts.isClassDeclaration(node) && node.decorators && node.decorators.length) {
                    return transformClass(node, context);
                }
                return ts.visitEachChild(node, (child) => visitor(child), context);
            };
            return ts.visitNode(sourceFile, visitor);
        };
    };
};
exports.smartEditTransformer = smartEditTransformer;
const smartEditTransformerFactory = (config) => {
    return () => {
        if (config && config.skipInstrumentation) {
            return {};
        }
        return {
            before: exports.smartEditTransformer()
        };
    };
};
exports.smartEditTransformerFactory = smartEditTransformerFactory;
