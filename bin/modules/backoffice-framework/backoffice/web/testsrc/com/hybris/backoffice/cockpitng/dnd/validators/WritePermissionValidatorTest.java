/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.cockpitng.dnd.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hybris.backoffice.cockpitng.dataaccess.facades.permissions.DefaultPlatformPermissionFacadeStrategy;
import com.hybris.cockpitng.dnd.DefaultDragAndDropContext;
import com.hybris.cockpitng.dnd.DropOperationData;
import com.hybris.cockpitng.validation.model.ValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationSeverity;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WritePermissionValidatorTest
{
    @InjectMocks
    private WritePermissionValidator validator;

    @Mock
    private DefaultDragAndDropContext context;

    @Mock
    private DropOperationData operationData;

    @Mock
    private CategoryModel draggedCategory;

    @Mock
    private CatalogVersionModel catalogVersion;

    @Mock
    private UserService userService;

    @Mock
    private CatalogVersionService catalogVersionService;

    @Mock
    private DefaultPlatformPermissionFacadeStrategy permissionFacadeStrategy;

    private ArrayList<CatalogVersionModel> allWritableCatalogVersions = new ArrayList<>();
    private ArrayList<CatalogVersionModel> userWritableCatalogVersions = new ArrayList<>();
    private UserModel currentUser = mock(UserModel.class);

    @Before
    public void setup() throws Exception
    {
        allWritableCatalogVersions.add(catalogVersion);
        when(operationData.getDragged()).thenReturn(draggedCategory);
        when(draggedCategory.getCatalogVersion()).thenReturn(catalogVersion);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(catalogVersionService.getAllCatalogVersions()).thenReturn(allWritableCatalogVersions);
        when(catalogVersionService.getAllWritableCatalogVersions(currentUser)).thenReturn(userWritableCatalogVersions);
        when(permissionFacadeStrategy.canChangeInstance(any())).thenReturn(true);
    }

    @Test
    public void shouldApplicableWhenDraggedClassIsCategory()
    {
        final boolean result = validator.isApplicable(operationData, context);

        assertThat(result).isTrue();
    }

    @Test
    public void shouldApplicableWhenDraggedClassIsProduct()
    {
        when(operationData.getDragged()).thenReturn(mock(ProductModel.class));
        final boolean result = validator.isApplicable(operationData, context);

        assertThat(result).isTrue();
    }

    @Test
    public void shouldNotBeApplicableWhenDraggedClassIsNotCategory()
    {
        when(operationData.getDragged()).thenReturn(mock(CatalogVersionModel.class));

        final boolean result = validator.isApplicable(operationData, context);

        assertThat(result).isFalse();
    }

    @Test
    public void shouldNotGetValidationInfoWhenCurrentUserIsAdmin()
    {
        when(userService.isAdmin(currentUser)).thenReturn(true);

        final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

        assertThat(validationInfos).isEmpty();
    }

    @Test
    public void shouldGetValidationErrorWhenCurrentUserDoNotHaveWritePermission()
    {
        when(userService.isAdmin(currentUser)).thenReturn(false);

        final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

        assertThat(validationInfos).hasSize(1);
        assertThat(validationInfos.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.ERROR);
    }

    @Test
    public void shouldNotGetValidationInfoWhenCurrentUserHaveWritePermission()
    {
        when(userService.isAdmin(currentUser)).thenReturn(false);
        userWritableCatalogVersions.add(catalogVersion);

        final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

        assertThat(validationInfos).isEmpty();
    }

    @Test
    public void shouldGetValidationErrorWhenCurrentUserDoNotHaveProductWritePermission()
    {
        final ProductModel draggedProduct = mock(ProductModel.class);
        when(userService.isAdmin(currentUser)).thenReturn(false);
        when(operationData.getDragged()).thenReturn(draggedProduct);
        when(draggedProduct.getCatalogVersion()).thenReturn(mock(CatalogVersionModel.class));

        final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

        assertThat(validationInfos).hasSize(1);
        assertThat(validationInfos.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.ERROR);
    }

    @Test
    public void shouldGetValidationErrorWhenCurrentUserDoNotHaveTypeWritePermission()
    {
        when(permissionFacadeStrategy.canChangeInstance(draggedCategory)).thenReturn(false);

        final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

        assertThat(validationInfos).hasSize(1);
        assertThat(validationInfos.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.ERROR);
    }

    @Test
    public void shouldGetValidationErrorWhenCurrentUserDoNotHaveTypeWritePermissionEvenIfHasTheCatalogVersionWritePermission()
    {
        when(permissionFacadeStrategy.canChangeInstance(draggedCategory)).thenReturn(false);
        userWritableCatalogVersions.add(catalogVersion);

        final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

        assertThat(validationInfos).hasSize(1);
        assertThat(validationInfos.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.ERROR);
    }
}
