/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcommonbol.transaction.businessobject.transfer.interf;

import java.util.Iterator;


/**
 * Some older list based business objects are working with an underlying array list but do neither implement the List
 * interface, nor do they provide direct access to the underlying list in in a common way. Still they have the method of
 * this interface in common. So they can now be accessed in a unified way using this interface.
 *
 * @param <T>
 */

public interface SimpleListAccess<T> extends Iterable<T> {

    /**
     * returns the size of the list
     *
     * @return size of the list
     */
    int size();

    /**
     * returns an iterator
     *
     * @return iterator
     */
    
    @Override
    Iterator<T> iterator();

    /**
     * checks if the list is empty
     *
     * @return returns true, only if is the list is empty
     */
    boolean isEmpty();
}
