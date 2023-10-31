/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
  package com.hybris.backoffice.search.daos.impl;

  import static org.mockito.Mockito.doReturn;
  import static org.mockito.Mockito.mock;
  import static org.mockito.Mockito.verify;
  import static org.mockito.Matchers.any;
  import static org.junit.Assert.assertEquals;
 
  import org.junit.Test;
  import org.junit.runner.RunWith;
  import org.mockito.Spy;
  import org.mockito.junit.MockitoJUnitRunner;
  import org.mockito.Mock;
  import org.mockito.InjectMocks;

  import java.util.List;
  import java.util.ArrayList;
  import com.google.common.collect.Lists;

  import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
  import de.hybris.platform.servicelayer.search.FlexibleSearchService;
  import de.hybris.platform.core.model.ItemModel;
  import de.hybris.platform.servicelayer.search.SearchResult;


 @RunWith(MockitoJUnitRunner.class)
  public class DefaultItemModelSearchDAOTest
  {  
    @Mock
    private FlexibleSearchService flexibleSearchService;
      
    @InjectMocks
    @Spy
    private DefaultItemModelSearchDAO defaultItemModelSearchDAO;
 
     @Test
     public void shouldReturnNewArrayListWhenItemsIsEmpty()
     {
       final String typeCode = "123";
       final List<Long> itemsPks = new ArrayList<>(){{
         add(1234L);
       }};
       doReturn(mock(SearchResult.class)).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
       assertEquals(Lists.newArrayList(), defaultItemModelSearchDAO.findAll(typeCode, itemsPks));
     }

    @Test
    public void shouldReturnItemsWhenItemsIsEmpty()
    {
      final String typeCode = "123";
      final List<Long> itemsPks = new ArrayList<>(){{
        add(1234L);
      }};
      final SearchResult searchResult = mock(SearchResult.class);
      final List<ItemModel> items = new ArrayList<>() {{
          add(new ItemModel());
       }};
      doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
      doReturn(items).when(searchResult).getResult();
      defaultItemModelSearchDAO.findAll(typeCode, itemsPks);
      verify(defaultItemModelSearchDAO).orderItemsByPkList(any(), any());
    }
 
  }
