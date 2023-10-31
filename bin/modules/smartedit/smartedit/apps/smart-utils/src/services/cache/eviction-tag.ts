/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc object
 * @name @smartutils.object:EvictionTag
 * @description
 * A {@link @smartutils.object:@Cached @Cached} annotation is tagged with 0 to n EvictionTag, each EvictionTag possibly referencing other evictionTags.
 * <br/>An EvictionTag enables a method cache to be evicted 2 different ways:
 * <ul>
 * <li> An event with the same name as the tag is raised.</li>
 * <li> {@link @smartutils.services:CacheService#methods_evict evict} method of {@link @smartutils.services:CacheService cacheService} is invoked with the tag.</li>
 * </ul>
 */
export class EvictionTag {
    /**
     * @ngdoc property
     * @name name
     * @propertyOf @smartutils.object:EvictionTag
     * @description
     * event upon which the cache should be cleared.
     */
    public event: string;

    /**
     * @ngdoc property
     * @name relatedTags
     * @propertyOf @smartutils.object:EvictionTag
     * @description
     * other EvictionTag instances grouped under this evictionTag.
     */
    public relatedTags?: EvictionTag[];

    constructor(args: { event: string; relatedTags?: EvictionTag[] }) {
        this.event = args.event;
        this.relatedTags = args.relatedTags;
    }
}
