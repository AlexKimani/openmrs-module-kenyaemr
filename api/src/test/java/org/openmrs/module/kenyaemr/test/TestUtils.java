/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.test;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.openmrs.*;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.TsvReportRenderer;

import java.io.IOException;
import java.util.*;

/**
 * Utility methods for unit tests
 */
@Ignore
public class TestUtils {

	/**
	 * Convenience method to create a new date
	 * @param year the year
	 * @param month the month
	 * @param day the day
	 * @return the date
	 * @throws IllegalArgumentException if date values are not valid
	 */
	public static Date date(int year, int month, int day) {
		return date(year, month, day, 0, 0, 0);
	}

	/**
	 * Convenience method to create a new date with time
	 * @param year the year
	 * @param month the month
	 * @param day the day
	 * @param hour the hour
	 * @param minute the minute
	 * @param second the second
	 * @return the date
	 * @throws IllegalArgumentException if date values are not valid
	 */
	public static Date date(int year, int month, int day, int hour, int minute, int second) {
		Calendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, second);
		cal.setLenient(false);
		return cal.getTime();
	}

	/**
	 * Create and save a visit
	 * @param patient the patient
	 * @param type the visit type
	 * @param start the start date
	 * @param stop the stop date
	 * @return the aved visit
	 */
	public static Visit saveVisit(Patient patient, VisitType type, Date start, Date stop) {
		Visit visit = new Visit();
		visit.setPatient(patient);
		visit.setVisitType(type);
		visit.setStartDatetime(start);
		visit.setStopDatetime(stop);
		return Context.getVisitService().saveVisit(visit);
	}

	/**
	 * Create and save an encounter
	 * @param patient the patient
	 * @param type the encounter type
	 * @param date the encounter date
	 * @return the saved encounter
	 */
	public static Encounter saveEncounter(Patient patient, EncounterType type, Date date) {
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setProvider(Context.getUserService().getUser(1)); // Super user
		encounter.setLocation(Context.getLocationService().getLocation(1)); // Unknown Location
		encounter.setEncounterType(type);
		encounter.setEncounterDatetime(date);
		return Context.getEncounterService().saveEncounter(encounter);
	}

	/**
	 * Create and save an encounter
	 * @param patient the patient
	 * @param form the form
	 * @param date the encounters date
	 * @return the saved encounter
	 */
	public static Encounter saveEncounter(Patient patient, Form form, Date date) {
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setProvider(Context.getUserService().getUser(1)); // Super user
		encounter.setLocation(Context.getLocationService().getLocation(1)); // Unknown Location
		encounter.setEncounterType(form.getEncounterType());
		encounter.setForm(form);
		encounter.setEncounterDatetime(date);
		return Context.getEncounterService().saveEncounter(encounter);
	}

	/**
	 * Enroll a patient in a program
	 * @param patient the patient
	 * @param program the program
	 * @param date the enroll date
	 * @return the patient program
	 */
	public static PatientProgram enrollInProgram(Patient patient, Program program, Date date) {
		return enrollInProgram(patient, program, date, null);
	}

	/**
	 * Enroll a patient in a program
	 * @param patient the patient
	 * @param program the program
	 * @param start the enroll date
	 * @param completed the completed date
	 * @return the patient program
	 */
	public static PatientProgram enrollInProgram(Patient patient, Program program, Date start, Date completed) {
		PatientProgram pp = new PatientProgram();
		pp.setPatient(patient);
		pp.setProgram(program);
		pp.setDateEnrolled(start);
		pp.setDateCompleted(completed);
		return Context.getProgramWorkflowService().savePatientProgram(pp);
	}

	/**
	 * Save a numeric obs
	 * @param patient the patient
	 * @param concept the concept
	 * @param val the numeric value
	 * @param date the date
	 * @return the obs
	 */
	public static Obs saveObs(Patient patient, Concept concept, double val, Date date) {
		Obs obs = new Obs(patient, concept, date, null);
		obs.setValueNumeric(val);
		return Context.getObsService().saveObs(obs, null);
	}

	/**
	 * Save a datetime obs
	 * @param patient the patient
	 * @param concept the concept
	 * @param val the datetime value
	 * @param date the date
	 * @return the obs
	 */
	public static Obs saveObs(Patient patient, Concept concept, Date val, Date date) {
		Obs obs = new Obs(patient, concept, date, null);
		obs.setValueDatetime(val);
		return Context.getObsService().saveObs(obs, null);
	}

	/**
	 * Save a coded obs
	 * @param patient the patient
	 * @param concept the concept
	 * @param val the datetime value
	 * @param date the date
	 * @return the obs
	 */
	public static Obs saveObs(Patient patient, Concept concept, Concept val, Date date) {
		Obs obs = new Obs(patient, concept, date, null);
		obs.setValueCoded(val);
		return Context.getObsService().saveObs(obs, null);
	}

	/**
	 * Saves a drug order
	 * @param patient the patient
	 * @param concept the drug concept
	 * @param start the start date
	 * @param end the end date
	 * @return the drug order
	 */
	public static DrugOrder saveDrugOrder(Patient patient, Concept concept, Date start, Date end) {
		DrugOrder order = new DrugOrder();
		order.setOrderType(Context.getOrderService().getOrderType(2));
		order.setPatient(patient);
		order.setOrderer(Context.getUserService().getUser(1));
		order.setConcept(concept);
		order.setStartDate(start);
		order.setDiscontinued(end != null);
		order.setDiscontinuedDate(end);
		return (DrugOrder) Context.getOrderService().saveOrder(order);
	}

	/**
	 * Saves a regimen order
	 * @param patient the patient
	 * @param concepts the drug concepts
	 * @param start the start date
	 * @param end the end date
	 * @return the drug order
	 */
	public static RegimenOrder saveRegimenOrder(Patient patient, Collection<Concept> concepts, Date start, Date end) {
		Set<DrugOrder> orders = new LinkedHashSet<DrugOrder>();

		for (Concept concept : concepts) {
			DrugOrder order = new DrugOrder();
			order.setOrderType(Context.getOrderService().getOrderType(2));
			order.setPatient(patient);
			order.setOrderer(Context.getUserService().getUser(1));
			order.setConcept(concept);
			order.setStartDate(start);
			order.setDiscontinued(end != null);
			order.setDiscontinuedDate(end);
			orders.add((DrugOrder) Context.getOrderService().saveOrder(order));
		}

		return new RegimenOrder(orders);
	}

	/**
	 * Saves an untyped global property
	 * @param property the property name
	 * @param value the property value
	 * @return the global property
	 */
	public static GlobalProperty saveGlobalProperty(String property, String value) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(property);
		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(property);
		}
		gp.setPropertyValue(String.valueOf(value));
		return Context.getAdministrationService().saveGlobalProperty(gp);
	}

	/**
	 * Saves a typed global property
	 * @param property the property name
	 * @param value the property value
	 * @return the global property
	 */
	public static GlobalProperty saveGlobalProperty(String property, Object value, Class<? extends CustomDatatype> datatypeClass) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(property);
		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(property);
		}
		gp.setValue(value);
		gp.setDatatypeClassname(datatypeClass.getName());
		return Context.getAdministrationService().saveGlobalProperty(gp);
	}

	/**
	 * Asserts that the given regimen contains only the given drug orders
	 * @param reg
	 * @param drugOrders
	 */
	public static void assertRegimenContainsDrugOrders(RegimenOrder reg, DrugOrder... drugOrders) {
		Assert.assertEquals(drugOrders.length, reg.getDrugOrders().size());
		for (DrugOrder o : drugOrders) {
			Assert.assertTrue(reg.getDrugOrders().contains(o));
		}
	}

	/**
	 * Prints an object as JSON
	 * @param object the object
	 * @throws IOException
	 */
	public static void printJson(Object object) throws IOException {
		System.out.println(new ObjectMapper().writeValueAsString(object));
	}
}