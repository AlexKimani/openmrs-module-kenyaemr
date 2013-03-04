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
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Calendar;

/**
 *
 */
public class VisitMenuFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam("patient") Patient patient, @FragmentParam(value = "visit", required = false) Visit visit) {

		model.addAttribute("patient", patient);
		model.addAttribute("visit", visit);

		// Get now time, accurate only to minute
		Calendar now = Calendar.getInstance();
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);

		Visit newVisit = new Visit();
		newVisit.setPatient(patient);
		newVisit.setStartDatetime(now.getTime());
		newVisit.setVisitType(Context.getVisitService().getVisitTypeByUuid(MetadataConstants.OUTPATIENT_VISIT_TYPE_UUID));
		model.addAttribute("newCurrentVisit", newVisit);
	}
}