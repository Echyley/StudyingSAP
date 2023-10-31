/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleengineservices.jobs.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengineservices.model.RuleEngineCronJobModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.Mockito;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleEngineJobExecutionSynchronizerUnitTest
{
	@Mock
	private RulesModuleDao rulesModuleDao;

	@Mock
	private ModelService modelService;

	@Mock
	private RuleEngineCronJobModel cronJob;

	@Captor
	private ArgumentCaptor<List<? extends ItemModel>> saveItemsCaptor;

	@InjectMocks
	private DefaultRuleEngineJobExecutionSynchronizer synchronizer;

	@Test
	public void shouldAcquireLock()
	{
		Mockito.lenient().when(cronJob.getSrcModuleName()).thenReturn("sourceModuleName");
		Mockito.lenient().when(cronJob.getTargetModuleName()).thenReturn("targetModuleName");

		final AbstractRulesModuleModel sourceModule = mock(AbstractRulesModuleModel.class);
		Mockito.lenient().when(rulesModuleDao.findByName("sourceModuleName")).thenReturn(sourceModule);

		final AbstractRulesModuleModel targetModule = mock(AbstractRulesModuleModel.class);
		Mockito.lenient().when(rulesModuleDao.findByName("targetModuleName")).thenReturn(targetModule);

		Mockito.lenient().when(sourceModule.getLockAcquired()).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(targetModule.getLockAcquired()).thenReturn(Boolean.FALSE);

		final boolean result = synchronizer.acquireLock(cronJob);

		assertTrue(result);

		verify(cronJob).setLockAcquired(true);
		verify(sourceModule).setLockAcquired(true);
		verify(targetModule).setLockAcquired(true);
		verify(modelService).saveAll(saveItemsCaptor.capture());
		assertThat(saveItemsCaptor.getValue(), containsInAnyOrder(cronJob, sourceModule, targetModule));
	}

	@Test
	public void shouldTryAcquireLockButFail()
	{
		Mockito.lenient().when(cronJob.getSrcModuleName()).thenReturn("sourceModuleName");
		Mockito.lenient().when(cronJob.getTargetModuleName()).thenReturn("targetModuleName");

		final AbstractRulesModuleModel sourceModule = mock(AbstractRulesModuleModel.class);
		Mockito.lenient().when(rulesModuleDao.findByName("sourceModuleName")).thenReturn(sourceModule);

		final AbstractRulesModuleModel targetModule = mock(AbstractRulesModuleModel.class);
		Mockito.lenient().when(rulesModuleDao.findByName("targetModuleName")).thenReturn(targetModule);

		Mockito.lenient().when(sourceModule.getLockAcquired()).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(targetModule.getLockAcquired()).thenReturn(Boolean.FALSE);

		final boolean result = synchronizer.acquireLock(cronJob);

		assertFalse(result);

		verify(cronJob, never()).setLockAcquired(true);
		verify(sourceModule, never()).setLockAcquired(true);
		verify(targetModule, never()).setLockAcquired(true);
		verify(modelService, never()).saveAll(anyCollection());
	}

	@Test
	public void shouldReleaseLock()
	{
		Mockito.lenient().when(cronJob.getSrcModuleName()).thenReturn("sourceModuleName");
		Mockito.lenient().when(cronJob.getTargetModuleName()).thenReturn("targetModuleName");

		final AbstractRulesModuleModel sourceModule = mock(AbstractRulesModuleModel.class);
		Mockito.lenient().when(rulesModuleDao.findByName("sourceModuleName")).thenReturn(sourceModule);

		final AbstractRulesModuleModel targetModule = mock(AbstractRulesModuleModel.class);
		Mockito.lenient().when(rulesModuleDao.findByName("targetModuleName")).thenReturn(targetModule);

		synchronizer.releaseLock(cronJob);

		verify(cronJob).setLockAcquired(false);
		verify(sourceModule).setLockAcquired(false);
		verify(targetModule).setLockAcquired(false);
		verify(modelService).saveAll(saveItemsCaptor.capture());
		assertThat(saveItemsCaptor.getValue(), containsInAnyOrder(cronJob, sourceModule, targetModule));
	}
}
