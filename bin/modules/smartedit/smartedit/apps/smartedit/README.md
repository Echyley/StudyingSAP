<h1 align="center">SmartEdit</h1>

The smartedit library contains an Angular application that provides a robust framework and API to add functionality to existing web pages in a pluggable way. This SmartEdit application is injected and bootstrapped into a page in the SmartEdit container's iframe by the (Web Application Injector)[../web-app-injector]. The smartedit-library provides the core SmartEdit functionality and APIs and services that can be used by extensions (such as [cms-smartedit](../../../cmssmartedit/apps/cms-smartedit)) to add new functionality.

Please note that the smartedit-library cannot be loaded directly into the storefront. It is loaded and bootstrapped indirectly through the (smartedit-bootstrap library)[../smartedit-bootstrap].

## Prerequisites

To execute the smartedit library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the smartedit will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the smartedit library folder. The smartedit library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Building

The smartedit library can be built using the following command:

```bash
npm run build
```

After the build is done, the output will be in the `dist/` folder. At this point, the library is ready to be consumed by (smartedit-master)[../smartedit-master] or other libraries.

> 📘 Note
>
> The smartedit code will be referenced dynamically in the ../smartedit-master/inner/index_master.ts file in the smartedit-master library.

## Running Unit Tests

The following command can be used to run the smartedit unit tests in the background:

```bash
npm run test
```

It is also possible to run unit tests in a browser window. This is useful when debugging tests (it allows setting break-points). To do so, execute the following command:

```bash
npm run test:dev
```

## Generating documentation

The following command can be used to generate the smartedit documentation:

```bash
npm run docs
```
