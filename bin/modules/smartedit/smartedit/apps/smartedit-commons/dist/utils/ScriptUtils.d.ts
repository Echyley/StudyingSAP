import { SeFactory } from '../di';
export declare class ScriptUtils {
    injectJS(): {
        getInjector: () => any;
        execute: (conf: {
            srcs: string[];
            callback: SeFactory;
            index?: number;
        }) => void;
    };
}
export declare const scriptUtils: ScriptUtils;
