/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.theme.populator;

import com.hybris.backoffice.model.ThemeModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.smarteditwebservices.data.Theme;
import org.springframework.beans.BeanUtils;

public class SmarteditThemeModelToDataPopulator implements Populator<ThemeModel, Theme> {
    @Override
    public void populate(final ThemeModel source, final Theme target)
            throws ConversionException {

        BeanUtils.copyProperties(source, target);
    }
}
