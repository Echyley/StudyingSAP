/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration;

import de.hybris.platform.customerticketingc4cintegration.data.Note;

import java.util.Comparator;

/**
 * Filters notes by CreatedOn field.
 */
public class NotesComparator implements Comparator<Note>
{
    @Override
    public int compare(Note o1, Note o2)
    {
        return o1.getCreatedOn().compareTo(o2.getCreatedOn());
    }
}
