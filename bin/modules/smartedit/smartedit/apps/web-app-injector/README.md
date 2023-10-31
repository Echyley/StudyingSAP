<h1 align="center">Web Application Injector</h1>
The web-app-injector is a JavaScript library that must be included in all pages so that they can be edited using the SmartEdit application. It includes functionality to enable communication between the storefront and the SmartEdit container (via window.postMessage) and to load JavaScript and CSS files to extend the SmartEdit functionality (via [$script.js](https://github.com/ded/script.js/)).

## Prerequisites

To execute the web-app-injector library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the web-app-injector will have a copy of node_modules in it.

> ## 🚧 Important Notes
>
> Whenever a change is made in `web-app-injector`, **you must run** `ant build` and include changes in a commit
>
> Never execute `npm install` directly on the web-app-injector library folder. The web-app-injector library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Building

The web-app-injector can be built using the following command:

```bash
npm run build
```

After the build is done, the output will be in the `dist/` folder. At this point, the `dist/webApplicationInjector.js` can be used.

## Loading the Web Application Injector

To be able to edit a page in SmartEdit it must include the `webApplicationInjector.js`. You can do so adding the following script tag in the header of the page to be edited:

```html
<script src="path/to/webApplicationInjector.js"></script>
```
