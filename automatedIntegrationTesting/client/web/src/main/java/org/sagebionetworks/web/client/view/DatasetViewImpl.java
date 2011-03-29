package org.sagebionetworks.web.client.view;

import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.web.client.presenter.DatasetRow;
import org.sagebionetworks.web.client.widget.licenseddownloader.LicensedDownloader;
import org.sagebionetworks.web.client.widget.table.QueryServiceTable;
import org.sagebionetworks.web.client.widget.table.QueryServiceTableResourceProvider;
import org.sagebionetworks.web.shared.FileDownload;
import org.sagebionetworks.web.shared.LicenseAgreement;
import org.sagebionetworks.web.shared.QueryConstants.ObjectType;
import org.sagebionetworks.web.shared.QueryConstants.WhereOperator;
import org.sagebionetworks.web.shared.WhereCondition;

import com.google.gwt.cell.client.widget.PreviewDisclosurePanel;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DatasetViewImpl extends Composite implements DatasetView {

	private final int DESCRIPTION_SUMMARY_LENGTH = 50; // characters for summary

	public interface Binder extends UiBinder<Widget, DatasetViewImpl> {
	}

	@UiField
	FlowPanel overviewPanel;
	@UiField
	SpanElement titleSpan;
	@UiField
	FlexTable middleFlexTable;
	@UiField
	FlexTable rightFlexTable;
	@UiField
	SimplePanel tablePanel;
	@UiField
	SimplePanel downloadPanel;

	private Presenter presenter;
	private PreviewDisclosurePanel previewDisclosurePanel;
	private QueryServiceTable queryServiceTable;
	private final LicensedDownloader datasetLicensedDownloader;
	private boolean disableDownloads;

	@Inject
	public DatasetViewImpl(Binder uiBinder, final PreviewDisclosurePanel previewDisclosurePanel, QueryServiceTableResourceProvider queryServiceTableResourceProvider, LicensedDownloader dsLicensedDownloader) {		
		disableDownloads = false;
		initWidget(uiBinder.createAndBindUi(this));
		this.previewDisclosurePanel = previewDisclosurePanel;
		this.datasetLicensedDownloader = dsLicensedDownloader;
		setupDatasetLicensedDownloaderCallbacks();

		// layers table
		queryServiceTable = new QueryServiceTable(queryServiceTableResourceProvider, ObjectType.layer, false, 300, 300);
		tablePanel.add(queryServiceTable.asWidget());
		
		// download dataset button
		Button downloadDatasetButton = new Button("Download Dataset", new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				datasetLicensedDownloader.showWindow();			
			}
		}); 		
		downloadPanel.add(downloadDatasetButton);	
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void showErrorMessage(String message) {
		Window.alert(message);
	}

	@Override
	public void setDatasetRow(DatasetRow row) {
		// Set the where clause
		List<WhereCondition> whereList = new ArrayList<WhereCondition>();
		whereList.add(new WhereCondition("dataset.id", WhereOperator.EQUALS, row.getId()));
		this.queryServiceTable.setWhereCondition(whereList);
		// Clear everything
		clearAllFields();
		titleSpan.setInnerText(row.getName());

		// set descriptions
		String description = row.getDescription();
		if(description == null){
			description = "No Description";
		}
		int summaryLength = description.length() >= DESCRIPTION_SUMMARY_LENGTH ? DESCRIPTION_SUMMARY_LENGTH
				: description.length();
		previewDisclosurePanel.init("Expand",
				description.substring(0, summaryLength), description);
		overviewPanel.add(previewDisclosurePanel);

		// First row
		int rowIndex = 0;
		addRowToTable(rowIndex++, "Disease(s):", "Aging", middleFlexTable);
		// Second row
		addRowToTable(rowIndex++, "Species:", "Human, Mouse", middleFlexTable);
		// Third
		addRowToTable(rowIndex++, "Study size:", "200", middleFlexTable);
		// Forth
		addRowToTable(rowIndex++, "Tissue type(s):", "Brain", middleFlexTable);

		// Now fill out the right
		rowIndex = 0;
		// Fill in the right from the datast
		if (row.getCreatedOn() != null) {
			addRowToTable(rowIndex++, "Posted:", DateTimeFormat
					.getMediumDateTimeFormat().format(row.getCreatedOn()),
					rightFlexTable);
		}
		if (row.getModifiedColumn() != null) {
			addRowToTable(rowIndex++, "Modified:", DateTimeFormat
					.getMediumDateTimeFormat().format(row.getModifiedColumn()),
					rightFlexTable);
		}
		addRowToTable(rowIndex++, "Creator:", row.getCreator(), rightFlexTable);
		addRowToTable(rowIndex++, "Status:", row.getStatus(), rightFlexTable);

	}

	/**
	 * Add a row to the provided FlexTable.
	 * 
	 * @param key
	 * @param value
	 * @param table
	 */
	private static void addRowToTable(int row, String key, String value,
			FlexTable table) {
		table.setText(row, 0, key);
		table.getCellFormatter().addStyleName(row, 0, "boldRight");
		table.setText(row, 1, value);
	}

	private void clearAllFields() {
		titleSpan.setInnerText("");
		middleFlexTable.clear();
		rightFlexTable.clear();
	}

	@Override
	public void setLicenseAgreement(LicenseAgreement agreement) {		
		datasetLicensedDownloader.setLicenseAgreement(agreement);		
	}

	@Override
	public void requireLicenseAcceptance(boolean requireLicense) {
		datasetLicensedDownloader.setRequireLicenseAcceptance(requireLicense);		
	}

	@Override
	public void disableLicensedDownloads(boolean disable) {
		this.disableDownloads = true;
	}

	@Override
	public void setDatasetDownloads(List<FileDownload> downloads) {
		datasetLicensedDownloader.setDownloadUrls(downloads);
	}

	/*
	 * Private Methods
	 */
	private void setupDatasetLicensedDownloaderCallbacks() {
		// give the LicensedDownloader something to call when the view accepts the license
		datasetLicensedDownloader.setLicenseAcceptedCallback(new AsyncCallback<Void>() {
			// called when the user agrees to the license 
			@Override
			public void onSuccess(Void result) {
				// let presenter know so it can persist this
				presenter.licenseAccepted();
			}

			// not used
			@Override
			public void onFailure(Throwable caught) { }

		});
	}
	
}
