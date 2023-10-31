<h1 align="center">SmartEdit Utils</h1>
The @smart/utils library contains core functionality needed to run SmartEdit. It is only meant to be used internally. External partners and customers are discouraged from using this library directly, as it is likely to change without warning. Instead, consider making use of functionality exposed by smartedit-commons, smartedit-container, or smartedit libraries.

## Prerequisites

To execute the @smart/utils library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the @smart/utils will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the @smart/utils library folder. The @smart/utils library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Building

The @smart/utils library can be built using the following command:

```bash
npm run build
```

After the build is done, the output will be in the `dist/` folder. At this point, the library is ready to be consumed by (smartedit-master)[../smartedit-master] or other libraries in SmartEdit.

## Running Unit Tests

The following command can be used to run the @smart/utils unit tests in the background:

```bash
npm run test
```

It is also possible to run unit tests in a browser window. This is useful when debugging tests (it is possible to set break points). To do so, execute the following command:

```bash
npm run test:dev
```

## Generating documentation

The following command can be used to generate the @smart/utils documentation:

```bash
npm run docs
```

## tsconfig.json notes

```js
skipTemplateCodegen; // Supress creation of .ngfactory.js and .ngstyle.js, these will be created where library is consumed https://angular.io/guide/aot-compiler#skiptemplatecodegen

strictMetadataEmit; // Intended when publishing libraries, used to report metadata errors immediately rather than producing metadata.json https://angular.io/guide/aot-compiler#strictmetadataemit

fullTemplateTypeCheck; // Checks variable binding in angular templates https://angular.io/guide/aot-compiler#binding-expression-validation
```
