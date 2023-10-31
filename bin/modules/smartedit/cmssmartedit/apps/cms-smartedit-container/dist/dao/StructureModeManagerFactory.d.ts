export declare class ModeManager {
    private modes;
    constructor(modes: string[]);
    validateMode(mode: string): boolean;
}
export declare class StructureModeManagerFactory {
    createModeManager(modes: string[]): ModeManager;
}
