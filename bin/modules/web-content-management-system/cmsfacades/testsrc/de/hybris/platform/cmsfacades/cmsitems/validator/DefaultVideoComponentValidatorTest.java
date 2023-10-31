/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.validator;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED_L10N;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.ThumbnailSelectorOptions;
import de.hybris.platform.cms2.model.contents.components.VideoComponentModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultVideoComponentValidatorTest
{
    @InjectMocks
    private DefaultVideoComponentValidator validator;

    @Mock
    private LanguageFacade languageFacade;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private ValidationErrorsProvider validationErrorsProvider;

    private ValidationErrors validationErrors = new DefaultValidationErrors();

    @Before
    public void setup()
    {
        final LanguageData language = new LanguageData();
        language.setRequired(true);
        language.setIsocode(Locale.ENGLISH.toLanguageTag());
        when(languageFacade.getLanguages()).thenReturn(Arrays.asList(language));
        when(commonI18NService.getLocaleForIsoCode(Locale.ENGLISH.toLanguageTag())).thenReturn(Locale.ENGLISH);
        when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
    }

    @Test
    public void testValidateWithoutRequiredAttributeAddErrors()
    {
        final MediaModel media = new MediaModel();

        final VideoComponentModel itemModel = new VideoComponentModel();
        itemModel.setVideo(media, Locale.ENGLISH);
        validator.validate(itemModel);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

        assertEquals(1, errors.size());

        assertThat(errors.get(0).getField(), is(VideoComponentModel.VIDEO));
        assertThat(errors.get(0).getErrorCode(), is(FIELD_REQUIRED_L10N));
    }


    @Test
    public void testValidateWithVideoModelAddNoError()
    {
        final MediaModel media = new MediaModel();
        media.setCode("test");

        final VideoComponentModel itemModel = new VideoComponentModel();
        itemModel.setVideo(media, Locale.ENGLISH);
        validator.validate(itemModel);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateThumbnailFieldWhenWithoutMediasWithError()
    {
        final MediaModel media = new MediaModel();
        media.setCode("test");

        final MediaContainerModel mediaContainer = new MediaContainerModel();
        List<MediaModel> medias = new ArrayList<>();
        mediaContainer.setMedias(medias);

        final VideoComponentModel itemModel = new VideoComponentModel();
        itemModel.setVideo(media, Locale.ENGLISH);
        itemModel.setThumbnail(mediaContainer, Locale.ENGLISH);
        itemModel.setThumbnailSelector(ThumbnailSelectorOptions.UPLOAD_THUMBNAIL);
        validator.validate(itemModel);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();
        assertEquals(1, errors.size());

        assertThat(errors.get(0).getField(), is(VideoComponentModel.THUMBNAIL));
        assertThat(errors.get(0).getErrorCode(), is(FIELD_REQUIRED_L10N));
    }

    @Test
    public void testValidateThumbnailFieldWithMediasWithoutError()
    {
        final MediaModel media = new MediaModel();
        media.setCode("testMedia");

        final MediaContainerModel mediaContainer = new MediaContainerModel();
        List<MediaModel> medias = new ArrayList<>();
        medias.add(media);
        mediaContainer.setMedias(medias);

        final VideoComponentModel itemModel = new VideoComponentModel();
        itemModel.setThumbnail(mediaContainer, Locale.ENGLISH);
        itemModel.setVideo(media, Locale.ENGLISH);
        itemModel.setThumbnailSelector(ThumbnailSelectorOptions.UPLOAD_THUMBNAIL);
        validator.validate(itemModel);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateThumbnailFieldNoBackgroundWithoutError()
    {
        final MediaModel media = new MediaModel();
        media.setCode("testMedia");

        final MediaContainerModel mediaContainer = new MediaContainerModel();
        List<MediaModel> medias = new ArrayList<>();
        mediaContainer.setMedias(medias);

        final VideoComponentModel itemModel = new VideoComponentModel();
        itemModel.setVideo(media, Locale.ENGLISH);
        itemModel.setThumbnail(mediaContainer, Locale.ENGLISH);
        itemModel.setThumbnailSelector(ThumbnailSelectorOptions.NO_THUMBNAIL);
        validator.validate(itemModel);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

        assertTrue(errors.isEmpty());
    }
}
