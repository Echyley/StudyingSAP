import { TypedMap } from '@smart/utils';
export interface BootstrapPayload {
    modules: any[];
    constants?: TypedMap<string>;
}
