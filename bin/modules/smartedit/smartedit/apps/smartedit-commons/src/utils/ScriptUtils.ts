/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import $script from 'scriptjs';
import { SeFactory } from '../di';

export class ScriptUtils {
    injectJS(): {
        getInjector: () => any;
        execute: (conf: { srcs: string[]; callback: SeFactory; index?: number }) => void;
    } {
        function getInjector(): $script {
            return $script;
        }

        return {
            getInjector,
            execute(conf: { srcs: string[]; callback: SeFactory; index?: number }): void {
                const srcs = conf.srcs;
                let index = conf.index;
                const callback = conf.callback;
                if (!srcs.length) {
                    callback();
                    return;
                }
                if (index === undefined) {
                    index = 0;
                }
                if (srcs[index] !== undefined) {
                    this.getInjector()(srcs[index], () => {
                        if (index + 1 < srcs.length) {
                            this.execute({
                                srcs,
                                index: index + 1,
                                callback
                            });
                        } else if (typeof callback === 'function') {
                            callback();
                        }
                    });
                }
            }
        };
    }
}

export const scriptUtils = new ScriptUtils();
