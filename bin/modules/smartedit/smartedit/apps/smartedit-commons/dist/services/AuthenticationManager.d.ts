import { IAuthenticationManagerService } from '@smart/utils';
import { SmarteditRoutingService } from './routing';
export declare class AuthenticationManager extends IAuthenticationManagerService {
    private readonly routing;
    constructor(routing: SmarteditRoutingService);
    onLogout(): void;
    onUserHasChanged(): void;
}
