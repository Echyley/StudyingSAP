/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.impex.util;

import de.hybris.platform.servicelayer.impex.ProcessMode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Util class for creating CompanyAddresses impex files for test purposes
 */
public final class CompanyAddressesTestImpexFileGenerator
{
	private static final String ADDRESS_HEADER = "%s Address;owner(Company.uid)[unique=true];streetname[unique=true];streetnumber[unique=true];postalcode[unique=true];town;country(isocode);";
	private static final String ADDRESS_VALUE_LINE = ";testCompany;Nymphenburger Strasse;%s;D-80636;Münchennn;DE;";

	private CompanyAddressesTestImpexFileGenerator()
	{
		//private constructor
	}

	/**
	 * Creates impex file for test purposes, that imports Company (along with required prerequisites: Language, Country) and
	 * Addresses related to this Company.
	 *
	 * @param numberAddressesPerCompany number of Address to import that will be assigned to imported Company.
	 * @param filePath                  specifies path and file name of impex file that will be created
	 * @param addressProcessMode        specifies process mode for importing Addresses
	 */
	public static void createCompanyAddressesTestImpexFile(final int numberAddressesPerCompany, final String filePath,
	                                                       final AddressProcessMode addressProcessMode)
	{
		createCompanyAddressesTestImpexFile(numberAddressesPerCompany, filePath, addressProcessMode, false);
	}

	/**
	 * Creates impex file for test purposes, that imports Company (along with required prerequisites: Language, Country) and
	 * Addresses related to this Company. When processing Addresses, firstly it imports (<code>numberAddressesPerCompany</code>-1) x Addresses,
	 * then removes first two imported Address items, and finally imports the last one Address.
	 *
	 * @param numberAddressesPerCompany number of Addresses to import. Final number of Addresses assigned to Company will be reduced by 2.
	 * @param filePath                  specifies path and file name of impex file that will be created
	 * @param addressProcessMode        specifies process mode for importing Addresses
	 */
	public static void createCompanyAddressesTestImpexFileWithRemovingFirstTwoAddresses(final int numberAddressesPerCompany,
	                                                                                    final String filePath,
	                                                                                    final AddressProcessMode addressProcessMode)
	{
		createCompanyAddressesTestImpexFile(numberAddressesPerCompany, filePath, addressProcessMode, true);
	}

	private static void createCompanyAddressesTestImpexFile(final int numberAddressesPerCompany, final String filePath, final
	AddressProcessMode addressProcessMode, final boolean withRemovingFirstTwoAddresses)
	{
		try (
				final FileWriter fileWriter = new FileWriter(Path.of("").toAbsolutePath() + filePath);
				final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		)
		{
			writeCompanyAddressesPrerequisites(bufferedWriter);
			writeEmptyLine(bufferedWriter);
			writeCompanies(bufferedWriter);
			writeEmptyLine(bufferedWriter);

			if (withRemovingFirstTwoAddresses)
			{
				writeAddressesWithRemovingFirstTwo(bufferedWriter, numberAddressesPerCompany, addressProcessMode);
			}
			else
			{
				writeAddresses(bufferedWriter, numberAddressesPerCompany, addressProcessMode);
			}
		}
		catch (final IOException ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	private static void writeCompanyAddressesPrerequisites(final BufferedWriter bufferedWriter) throws IOException
	{
		bufferedWriter.write("INSERT_UPDATE Language;isocode[unique=true];active;;;;;;");
		bufferedWriter.newLine();
		bufferedWriter.write(";de;true;;;;;;");
		writeEmptyLine(bufferedWriter);
		bufferedWriter.write("INSERT_UPDATE Country;isocode[unique=true];name[lang=de];name[lang=en];active;");
		bufferedWriter.newLine();
		bufferedWriter.write(";DE;Deutschland;Germany;true;");
	}

	private static void writeCompanies(final BufferedWriter bufferedWriter) throws IOException
	{
		bufferedWriter.write(
				"INSERT_UPDATE Company;uid[unique=true];buyer;manufacturer;supplier;carrier;country(isocode);locname[lang=de];locname[lang=en];;;;;;;;;");
		bufferedWriter.newLine();
		bufferedWriter.write(";testCompany;true;false;true;true;DE;hybris GmbH;hybris GmbH;;;;;;;;;");
	}

	private static void writeAddresses(final BufferedWriter bufferedWriter, final int numberOfAddressesPerCompany,
	                                   final AddressProcessMode addressProcessMode)
			throws IOException
	{
		writeAddressHeader(bufferedWriter, addressProcessMode.toString());
		bufferedWriter.newLine();
		for (int addressesNumber = 1; addressesNumber <= numberOfAddressesPerCompany; addressesNumber++)
		{
			bufferedWriter.write(String.format(ADDRESS_VALUE_LINE, addressesNumber));
			bufferedWriter.newLine();
		}
	}

	private static void writeAddressesWithRemovingFirstTwo(final BufferedWriter bufferedWriter,
	                                                       final int numberOfAddressesPerCompany,
	                                                       final AddressProcessMode addressProcessMode)
			throws IOException
	{
		final int lastAddressToImport = 1;
		final int numberOfAddressesToRemove = 2;

		if (numberOfAddressesPerCompany < numberOfAddressesToRemove)
		{
			throw new IllegalArgumentException(
					"Number of imported addresses cannot be less than number of addresses to remove(2)");
		}

		//Inserting (numberOfAddressPerCompany-1) x Address items
		writeAddressHeader(bufferedWriter, addressProcessMode.toString());
		bufferedWriter.newLine();
		for (int addressesNumber = 1; addressesNumber <= numberOfAddressesPerCompany - lastAddressToImport; addressesNumber++)
		{
			bufferedWriter.write(String.format(ADDRESS_VALUE_LINE, addressesNumber));
			bufferedWriter.newLine();
		}
		bufferedWriter.newLine();

		//Removing first two imported Address items
		writeAddressHeader(bufferedWriter, ProcessMode.REMOVE.toString());
		bufferedWriter.newLine();
		for (int i = 1; i <= numberOfAddressesToRemove; i++)
		{
			bufferedWriter.write(String.format(ADDRESS_VALUE_LINE, i));
			bufferedWriter.newLine();
		}
		bufferedWriter.newLine();

		//Inserting last Address item
		writeAddressHeader(bufferedWriter, addressProcessMode.toString());
		bufferedWriter.newLine();
		bufferedWriter.write(String.format(ADDRESS_VALUE_LINE, numberOfAddressesPerCompany));
	}

	private static void writeAddressHeader(final BufferedWriter bufferedWriter, final String processMode) throws IOException
	{
		bufferedWriter.write(String.format(ADDRESS_HEADER, processMode));
	}

	private static void writeEmptyLine(final BufferedWriter bufferedWriter) throws IOException
	{
		bufferedWriter.newLine();
		bufferedWriter.newLine();
	}

	public enum AddressProcessMode
	{
		INSERT, INSERT_UPDATE;
	}
}
