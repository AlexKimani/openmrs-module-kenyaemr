<%
	config.require("encounter")

	def encounter = config.encounter
	def title = ui.format(encounter.form ?: encounter.encounterType)

	if (config.showEncounterDate) {
		title += " (" + kenyaUi.formatDateTime(encounter.encounterDatetime) + ")"
	}

	def providers = encounter.providersByRoles.values().collectAll { ui.format(it) }.flatten().join(", ")

	def form = encounter.form ? kenyaEmrUi.simpleForm(encounter.form, ui) : [ iconProvider : "kenyaemr", icon : "forms/generic.png" ]
%>

<div class="ke-stack-item ke-clickable encounter-item">
	<input type="hidden" name="encounterId" value="${ encounter.encounterId }"/>
	${ ui.includeFragment("kenyaui", "widget/icon", [ iconProvider: form.iconProvider, icon: form.icon, useViewOverlay: true, tooltip: "View Encounter" ]) }
	<b>${ title }</b> by ${ providers }<br/>
	<span style="color: gray">
		Entered by ${ ui.format(encounter.creator) } on ${ kenyaUi.formatDateTime(encounter.dateCreated) }<% if (encounter.dateChanged) { %>, last edit by ${ ui.format(encounter.changedBy) } on ${ kenyaUi.formatDateTime(encounter.dateChanged) }<% } %>
	</span>
	<div style="clear: both"></div>
</div>