/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
log.info('input resource (Media code): ' + cronjob.job.input.code)
changeDetectionService.consumeChanges([change])
log.info('Consumed: ' + change)
true
