/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media.impl;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.data.MediaFolderData;
import de.hybris.platform.cmsfacades.media.service.CMSMediaFolderService;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultMediaFolderFacadeTest
{
	private static final String QUALIFIER = "qualifier";

	@InjectMocks
	private DefaultCMSMediaFolderFacade mediaFolderFacade;

	@Mock
	private CMSMediaFolderService cmsMediaFolderService;
	@Mock
	private PermissionCRUDService permissionCRUDService;
	@Mock
	private MediaStorageConfigService mediaStorageConfigService;

	@Mock
	private SearchResult<MediaFolderModel> mediaFolderSearchResult;
	@Mock
	private PageableData pageableData;

	private List<MediaFolderModel> folders;

	@Before
	public void setUp() throws ImpExException
	{
		List<String> securedFolders = Arrays.asList("cronjob", "email-body", "hmc", "email-attachments");
		MediaFolderModel cronjob = new MediaFolderModel();
		cronjob.setQualifier("cronjob");
		MediaFolderModel emailBody = new MediaFolderModel();
		emailBody.setQualifier("email-body");
		MediaFolderModel hmc = new MediaFolderModel();
		hmc.setQualifier("hmc");
		MediaFolderModel images = new MediaFolderModel();
		images.setQualifier("images");
		MediaFolderModel root = new MediaFolderModel();
		root.setQualifier("root");
		MediaFolderModel documents = new MediaFolderModel();
		documents.setQualifier("documents");
		folders = Arrays.asList(cronjob, emailBody,hmc,images,root,documents);

		when(mediaStorageConfigService.getSecuredFolders()).thenReturn(securedFolders);
		when(permissionCRUDService.canReadType(MediaFolderModel._TYPECODE)).thenReturn(true);

	}

	@Test
	public void shouldGetMediaFoldersFilterOutSecuredFolders()
	{
		when(mediaFolderSearchResult.getResult()).thenReturn(folders);
		when(mediaFolderSearchResult.getTotalCount()).thenReturn(200);
		when(mediaFolderSearchResult.getRequestedCount()).thenReturn(6);
		when(mediaFolderSearchResult.getRequestedStart()).thenReturn(0);
		when(cmsMediaFolderService.findMediaFolders("", pageableData)).thenReturn(mediaFolderSearchResult);

		SearchResult<MediaFolderData> mediaFolderDataSearchResult = mediaFolderFacade.findMediaFolders("", pageableData);

		assertEquals(3, mediaFolderDataSearchResult.getResult().size());

		assertThat(mediaFolderDataSearchResult.getResult(), hasItems( //
				allOf(hasProperty(QUALIFIER, equalTo("images"))), //
				allOf(hasProperty(QUALIFIER, equalTo("root"))),
				allOf(hasProperty(QUALIFIER, equalTo("documents")))
		));
	}

// Comments this case for M300 mocktio integration, please see CXEC-4604

}
