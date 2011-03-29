package org.sagebionetworks.web.client.presenter;

import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.web.client.DatasetServiceAsync;
import org.sagebionetworks.web.client.view.DatasetView;
import org.sagebionetworks.web.client.widget.licenseddownloader.LicenceServiceAsync;
import org.sagebionetworks.web.shared.Dataset;
import org.sagebionetworks.web.shared.FileDownload;
import org.sagebionetworks.web.shared.LicenseAgreement;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class DatasetPresenter extends AbstractActivity implements DatasetView.Presenter{	

	private org.sagebionetworks.web.client.place.Dataset place;
	private DatasetServiceAsync service;
	private LicenceServiceAsync licenseService;
	private DatasetView view;
	private String datasetId;
	private Dataset model;
	private final static String licenseAgreementText = "<p><b><larger>Copyright 2011 Sage Bionetworks</larger></b><br/><br/></p><p>Licensed under the Apache License, Version 2.0 (the \"License\"). You may not use this file except in compliance with the License. You may obtain a copy of the License at<br/><br/></p><p>&nbsp;&nbsp;<a href=\"http://www.apache.org/licenses/LICENSE-2.0\" target=\"new\">http://www.apache.org/licenses/LICENSE-2.0</a><br/><br/></p><p>Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions andlimitations under the License.<br/><br/></p><p><strong><a name=\"definitions\">1. Definitions</a></strong>.<br/><br/></p> <p>\"License\" shall mean the terms and conditions for use, reproduction, and distribution as defined by Sections 1 through 9 of this document.<br/><br/></p> <p>\"Licensor\" shall mean the copyright owner or entity authorized by the copyright owner that is granting the License.<br/><br/></p> <p>\"Legal Entity\" shall mean the union of the acting entity and all other entities that control, are controlled by, or are under common control with that entity. For the purposes of this definition, \"control\" means (i) the power, direct or indirect, to cause the direction or management of such entity, whether by contract or otherwise, or (ii) ownership of fifty percent (50%) or more of the outstanding shares, or (iii) beneficial ownership of such entity.<br/><br/></p> <p>\"You\" (or \"Your\") shall mean an individual or Legal Entity exercising permissions granted by this License.<br/><br/></p> <p>\"Source\" form shall mean the preferred form for making modifications, including but not limited to software source code, documentation source, and configuration files.<br/><br/></p> <p>\"Object\" form shall mean any form resulting from mechanical transformation or translation of a Source form, including but not limited to compiled object code, generated documentation, and conversions to other media types.<br/><br/></p> <p>\"Work\" shall mean the work of authorship, whether in Source or Object form, made available under the License, as indicated by a copyright notice that is included in or attached to the work (an example is provided in the Appendix below).<br/><br/></p> <p>\"Derivative Works\" shall mean any work, whether in Source or Object form, that is based on (or derived from) the Work and for which the editorial revisions, annotations, elaborations, or other modifications represent, as a whole, an original work of authorship. For the purposes of this License, Derivative Works shall not include works that remain separable from, or merely link (or bind by name) to the interfaces of, the Work and Derivative Works thereof.<br/><br/></p> <p>\"Contribution\" shall mean any work of authorship, including the original version of the Work and any modifications or additions to that Work or Derivative Works thereof, that is intentionally submitted to Licensor for inclusion in the Work by the copyright owner or by an individual or Legal Entity authorized to submit on behalf of the copyright owner. For the purposes of this definition, \"submitted\" means any form of electronic, verbal, or written communication sent to the Licensor or its representatives, including but not limited to communication on electronic mailing lists, source code control systems, and issue tracking systems that are managed by, or on behalf of, the Licensor for the purpose of discussing and improving the Work, but excluding communication that is conspicuously marked or otherwise designated in writing by the copyright owner as \"Not a Contribution.\"<br/><br/></p> <p>\"Contributor\" shall mean Licensor and any individual or Legal Entity on behalf of whom a Contribution has been received by Licensor and subsequently incorporated within the Work.<br/><br/></p>";
	
	/**
	 * Everything is injected via Guice.
	 * @param view
	 * @param datasetService
	 */
	@Inject
	public DatasetPresenter(DatasetView view, DatasetServiceAsync datasetService, LicenceServiceAsync licenseService) {
		this.view = view;
		this.view.setPresenter(this);
		this.service = datasetService;
		this.licenseService = licenseService;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		// First refresh from the server
		refreshFromServer();
		// add the view to the panel
		panel.setWidget(view);
		
	}

	public void refreshFromServer() {
		// Fetch the data about this dataset from the server
		service.getDataset(this.datasetId, new AsyncCallback<Dataset>() {
			
			@Override
			public void onSuccess(Dataset result) {
				setDataset(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				setDataset(null);
				view.showErrorMessage(caught.getMessage());				
			}
		});
		
	}

	protected void setDataset(Dataset result) {
		this.model = result;
		view.setDatasetRow(new DatasetRow(result));

		// check if license agreement has been accepted
		// TODO !!!! use real username !!!!
		licenseService.hasAccepted("GET-USERNAME", result.getUri(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {				
				view.showErrorMessage("Dataset downloading unavailable. Please try reloading the page.");
				view.disableLicensedDownloads(true);
			}

			@Override
			public void onSuccess(Boolean hasAccepted) {								
				view.requireLicenseAcceptance(!hasAccepted);
				// TODO !!!! get actual license agreement text (and citation if needed) !!!!
				LicenseAgreement agreement = new LicenseAgreement();
				agreement.setLicenseHtml(licenseAgreementText);
				view.setLicenseAgreement(agreement);
				
				// set download files
				// TODO : !!!! get real downloads from dataset object !!!!		
				List<FileDownload> downloads = new ArrayList<FileDownload>();
				downloads.add(new FileDownload("http://google.com", "Lusis Dataset", "3f37dba446d160543ab5732f04726fe0"));
				view.setDatasetDownloads(downloads);
			}
		});
	}

	public void setPlace(org.sagebionetworks.web.client.place.Dataset place) {
		this.place = place;
		this.datasetId = place.toToken();
	}

	@Override
	public void licenseAccepted() {
		// TODO : !!!! get real username !!!!
		licenseService.acceptLicenseAgreement("GET-USERNAME", model.getUri(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				// tell the user that they will need to accept this again in the future?
			}

			@Override
			public void onSuccess(Boolean licenseAccepted) {
				// tell the user that they will need to accept this again in the future (if licenseAccepted == false)?
			}
		});		
	}

	@Override
	public void logDownload() {		
	}
}


