/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.masterdetail.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.masterdetail.MDDetailLogic;
import com.hybris.backoffice.masterdetail.MDMasterLogic;
import com.hybris.backoffice.masterdetail.SettingItem;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMasterDetailSettingServiceTest
{

	@InjectMocks
	private DefaultMasterDetailSettingService masterDetailSettingService;

	@Test
	public void shouldAddAllItemsWhenRegisterMaster()
	{
		final MDMasterLogic master = mock(MDMasterLogic.class);
		final String id1 = "1";
		final String id2 = "2";
		final MDDetailLogic detail1 = mockMDDetailLogic(id1, 1);
		final MDDetailLogic detail2 = mockMDDetailLogic(id2, 2);
		masterDetailSettingService.registerDetail(detail1);
		masterDetailSettingService.registerDetail(detail2);

		masterDetailSettingService.registerMaster(master);

		verify(master).addItems(argThat(new ArgumentMatcher<List<SettingItem>>()
		{
			@Override
			public boolean matches(final List<SettingItem> itemList)
			{
				if (itemList instanceof List)
				{
					return itemList.size() == 2 && itemList.get(0).getId().equals(id1) && itemList.get(1).getId().equals(id2);
				}
				return false;
			}
		}));
	}

	@Test
	public void shouldReturnTrueIfDetailSaveSuccess()
	{
		final MDMasterLogic master = mock(MDMasterLogic.class);
		final String id = "1";
		final MDDetailLogic detail = mockMDDetailLogic(id, 1);
		when(detail.save()).thenReturn(true);
		masterDetailSettingService.registerDetail(detail);
		masterDetailSettingService.registerMaster(master);

		final boolean isSuccess = masterDetailSettingService.saveDetail(id);

		verify(detail).save();
		assertThat(isSuccess).isTrue();
	}

	@Test
	public void shouldReturnFalseIfDetailSaveFailure()
	{
		final MDMasterLogic master = mock(MDMasterLogic.class);
		final String id = "1";
		final MDDetailLogic detail = mockMDDetailLogic(id, 1);
		when(detail.save()).thenReturn(false);
		masterDetailSettingService.registerDetail(detail);
		masterDetailSettingService.registerMaster(master);

		final boolean isSuccess = masterDetailSettingService.saveDetail(id);

		assertThat(isSuccess).isFalse();
		verify(detail).save();
	}

	@Test
	public void shouldReturnFalseIfDetailNotExistWhenSaveDetail()
	{
		final MDMasterLogic master = mock(MDMasterLogic.class);
		final String id = "1";
		final MDDetailLogic detail = mockMDDetailLogic(id, 1);
		masterDetailSettingService.registerDetail(detail);
		masterDetailSettingService.registerMaster(master);

		final boolean isSuccess = masterDetailSettingService.saveDetail("2");

		assertThat(isSuccess).isFalse();
		verify(detail, never()).save();
	}

	@Test
	public void shouldNotCallMasterOrDetailLogicalAfterRest()
	{
		final MDMasterLogic master = mock(MDMasterLogic.class);
		final String id = "1";
		final MDDetailLogic detail = mockMDDetailLogic(id, 1);
		masterDetailSettingService.registerDetail(detail);
		masterDetailSettingService.registerMaster(master);
		masterDetailSettingService.enableSave(true);
		masterDetailSettingService.saveDetail(id);

		verify(master, atMost(1)).enableSave(true);
		verify(detail, atMost(1)).save();

		masterDetailSettingService.reset();

		masterDetailSettingService.enableSave(true);
		masterDetailSettingService.saveDetail(id);

		verify(master, atMost(1)).enableSave(true);
		verify(detail, atMost(1)).save();
	}

	@Test
	public void shouldExecuteEnableSaveAfterMasterRegistered()
	{
		final MDMasterLogic master = mock(MDMasterLogic.class);
		masterDetailSettingService.enableSave(false);
		verify(master, never()).enableSave(false);
		masterDetailSettingService.registerMaster(master);
		masterDetailSettingService.enableSave(false);
		verify(master).enableSave(false);
	}

	@Test
	public void shouldExecuteResetDetailAfterDetailRegistered()
	{
		final String id = "1";
		final MDDetailLogic detail = mockMDDetailLogic(id, 1);
		masterDetailSettingService.resetDetail(id);
		verify(detail, never()).reset();
		masterDetailSettingService.registerDetail(detail);
		masterDetailSettingService.resetDetail(id);
		verify(detail).reset();
	}

	@Test
	public void shouldExecuteUpdateItemAfterMasterRegisteredWhenDetailDataChanged()
	{
		final SettingItem settingItem = mock(SettingItem.class);
		final MDMasterLogic master = mock(MDMasterLogic.class);
		masterDetailSettingService.detailDataChanged(settingItem);
		verify(master, never()).updateItem(settingItem);
		masterDetailSettingService.registerMaster(master);
		masterDetailSettingService.detailDataChanged(settingItem);
		verify(master).updateItem(settingItem);
	}

	@Test
	public void shouldReturnCorrectIfDetailChangedOrNot()
	{
		final String id1 = "1";
		final String id2 = "2";
		final MDDetailLogic detail1 = mockMDDetailLogic(id1, 1);
		final MDDetailLogic detail2 = mockMDDetailLogic(id2, 2);
		when(detail1.isDataChanged()).thenReturn(true);
		when(detail2.isDataChanged()).thenReturn(false);
		masterDetailSettingService.registerDetail(detail1);
		masterDetailSettingService.registerDetail(detail2);
		assertThat(masterDetailSettingService.isDetailDataChanged(id1)).isTrue();
		assertThat(masterDetailSettingService.isDetailDataChanged(id2)).isFalse();
	}

	@Test
	public void shouldReturnCorrectIfRefreshUINeedOrNot()
	{
		final String id1 = "1";
		final String id2 = "2";
		final MDDetailLogic detail1 = mockMDDetailLogic(id1, 1);
		final MDDetailLogic detail2 = mockMDDetailLogic(id2, 2);
		when(detail1.needRefreshUI()).thenReturn(true);
		when(detail2.needRefreshUI()).thenReturn(false);
		masterDetailSettingService.registerDetail(detail1);
		masterDetailSettingService.registerDetail(detail2);
		assertThat(masterDetailSettingService.needRefreshUI(id1)).isTrue();
		assertThat(masterDetailSettingService.needRefreshUI(id2)).isFalse();
	}

	private MDDetailLogic mockMDDetailLogic(final String id, final int position)
	{
		final MDDetailLogic detail = mock(MDDetailLogic.class);
		final SettingItem settingItem = mock(SettingItem.class);
		when(settingItem.getPosition()).thenReturn(position);
		when(settingItem.getId()).thenReturn(id);
		when(detail.getSettingItem()).thenReturn(settingItem);
		return detail;
	}
}
