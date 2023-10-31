<h1 align="center">SmartEdit Bootstrap</h1>
The smartedit-bootstrap library is designed to bootstrap the [SmartEdit](../smartedit) application and its extensions into a page in the SmartEdit container's iframe.

## Prerequisites

To execute the smartedit-bootstrap library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the smartedit-bootstrap will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the smartedit-bootstrap library folder. The smartedit-bootstrap library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Building

The smartedit-bootstrap library can be built using the following command:

```bash
npm run build
```

After the build is done, the output will be in the `dist/` folder. At this point, the library is ready to be consumed by the (smartedit-master)[../smartedit-master].
