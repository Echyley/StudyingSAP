/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, IAnnouncementService } from 'smarteditcommons';

@GatewayProxied('showAnnouncement', 'closeAnnouncement')
export class AnnouncementService extends IAnnouncementService {}
