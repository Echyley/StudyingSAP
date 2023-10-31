import { BootstrapPayload, TypedMap } from 'smarteditcommons';
declare global {
    interface InternalSmartedit {
        smartEditBootstrapped: TypedMap<boolean>;
    }
}
export declare const smarteditContainerFactory: (bootstrapPayload: BootstrapPayload) => any;
