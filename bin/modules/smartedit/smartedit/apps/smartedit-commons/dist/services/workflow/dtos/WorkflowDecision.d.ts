import { TypedMap } from '@smart/utils';
/**
 * Interface used by {@link WorkflowService} to query workflow decision.
 */
export interface WorkflowDecision {
    code: string;
    name: TypedMap<string>;
    description: TypedMap<string>;
}
