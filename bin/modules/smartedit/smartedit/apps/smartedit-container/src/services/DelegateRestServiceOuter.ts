/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    GatewayProxied,
    IRestOptions,
    Page,
    Pageable,
    Payload,
    SearchParams,
    IRestServiceFactory
} from 'smarteditcommons';
/*
 * internal service to proxy calls from inner RESTService to the outer restServiceFactory and the 'real' IRestService
 */

/** @internal */
@GatewayProxied()
export class DelegateRestService {
    constructor(private restServiceFactory: IRestServiceFactory) {}

    delegateForSingleInstance<T>(
        methodName: string,
        params: string | Payload,
        uri: string,
        identifier: string,
        metadataActivated: boolean,
        options?: IRestOptions
    ): Promise<T> {
        const restService = this.restServiceFactory.get<T>(uri, identifier);
        if (metadataActivated) {
            restService.activateMetadata();
        }
        return restService.getMethodForSingleInstance(methodName)(params, options);
    }

    delegateForArray<T>(
        methodName: string,
        params: string | Payload,
        uri: string,
        identifier: string,
        metadataActivated: boolean,
        options?: IRestOptions
    ): Promise<T[]> {
        const restService = this.restServiceFactory.get<T>(uri, identifier);
        if (metadataActivated) {
            restService.activateMetadata();
        }
        return restService.getMethodForArray(methodName)(params, options);
    }

    delegateForPage<T>(
        pageable: Pageable,
        uri: string,
        identifier: string,
        metadataActivated: boolean,
        options?: IRestOptions
    ): Promise<Page<T>> {
        const restService = this.restServiceFactory.get<T>(uri, identifier);
        if (metadataActivated) {
            restService.activateMetadata();
        }
        return restService.page(pageable, options);
    }

    delegateForQueryByPost<T>(
        payload: Payload,
        params: SearchParams,
        uri: string,
        identifier: string,
        metadataActivated: boolean,
        options?: IRestOptions
    ): Promise<T> {
        const restService = this.restServiceFactory.get<T>(uri, identifier);
        if (metadataActivated) {
            restService.activateMetadata();
        }
        return restService.queryByPost(payload, params, options);
    }
}
