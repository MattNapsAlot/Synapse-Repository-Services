package org.sagebionetworks.web.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.IconsImageBundle;
import org.sagebionetworks.web.client.widget.licenseddownloader.LicensedDownloader;
import org.sagebionetworks.web.client.widget.statictable.StaticTable;
import org.sagebionetworks.web.client.widget.statictable.StaticTableColumn;
import org.sagebionetworks.web.shared.FileDownload;
import org.sagebionetworks.web.shared.LicenseAgreement;
import org.sagebionetworks.web.shared.TableResults;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.cell.client.widget.PreviewDisclosurePanel;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class LayerViewImpl extends Composite implements LayerView {

	public interface Binder extends UiBinder<Widget, LayerViewImpl> {
	}

	@UiField
	FlowPanel overviewPanel;
	@UiField
	SpanElement titleSpan;
	@UiField
	FlexTable rightFlexTable;
	@UiField 
	SimplePanel previewTablePanel;	
	@UiField
	SimplePanel downloadPanel;
	@UiField
	SimplePanel termsOfUseSpan;
	@UiField
	SpanElement breadcrumbDatasetSpan;
	@UiField
	SpanElement breadcrumbTitleSpan;

	private Presenter presenter;
	private PreviewDisclosurePanel previewDisclosurePanel;
	private StaticTable staticTable;
	private final LicensedDownloader licensedDownloader;
	private boolean disableDownloads = false;
	private IconsImageBundle icons;

	@Inject
	public LayerViewImpl(Binder uiBinder, IconsImageBundle icons, final PreviewDisclosurePanel previewDisclosurePanel, StaticTable staticTable, LicensedDownloader licensedDownloader) {
		initWidget(uiBinder.createAndBindUi(this));
		this.previewDisclosurePanel = previewDisclosurePanel;
		this.staticTable = staticTable;
		this.licensedDownloader = licensedDownloader;
		this.icons = icons;
		
		setupLicensedDownloaderCallbacks();
		// style FlexTables
		rightFlexTable.setCellSpacing(5);
		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void showErrorMessage(String message) {
		MessageBox.info("Message", message, null);
	}

	@Override
	public void setLayerDetails(String layerName, 
								String processingFacility, 
								String qcByDisplay,
								String qcByUrl, 
								String qcAnalysisDisplay, 
								String qcAnalysisUrl,
								Date qcDate, 
								String overviewText, 
								int nDataRowsShown,
								int totalDataRows, 
								String privacyLevel, 
								String datasetLink, 
								String platform) {
		
		// make sure displayed values are clean
		if(layerName == null) layerName = "";
		if(processingFacility == null) processingFacility = "";
		if(qcByDisplay == null) qcByDisplay = "";
		if(qcByUrl == null) qcByUrl = "";
		if(qcAnalysisDisplay == null) qcAnalysisDisplay = "";
		if(qcAnalysisUrl == null) qcAnalysisUrl = "";
		if(overviewText == null) overviewText  = "";
		if(privacyLevel == null) privacyLevel = "";
		if(platform == null) platform = "";

		clear(); // clear old values from view

		titleSpan.setInnerText(layerName);
		
		// download link		
		Anchor downloadLink = new Anchor();
		downloadLink.setHTML(AbstractImagePrototype.create(icons.download16()).getHTML() + " Download Layer");
		downloadLink.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				licensedDownloader.showWindow();
			}
		});
		downloadPanel.add(downloadLink);

		// see terms
		Anchor termsLink = new Anchor();
		termsLink.setHTML(AbstractImagePrototype.create(icons.documentText16()).getHTML() + " See terms of use");
		termsLink.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				licensedDownloader.showWindow();
			}
		});
			
		termsOfUseSpan.clear();
		termsOfUseSpan.add(termsLink);
		
		// set description
		if(overviewText == null) overviewText = "";
		int summaryLength = overviewText.length() >= DisplayConstants.DESCRIPTION_SUMMARY_LENGTH ? DisplayConstants.DESCRIPTION_SUMMARY_LENGTH : overviewText.length();
		previewDisclosurePanel.init("Expand", overviewText.substring(0, summaryLength), overviewText);
		overviewPanel.add(previewDisclosurePanel);
		
		// add metadata to table
		int rowIndex = 0;
		addRowToTable(rowIndex++, "Platform:", platform, rightFlexTable);
		addRowToTable(rowIndex++, "Processing Facility:", processingFacility, rightFlexTable);
		addRowToTable(rowIndex++, "QC By:", "<a href=\"" + qcByUrl + "\">" + qcByDisplay + "</a>", rightFlexTable);
		addRowToTable(rowIndex++, "QC Analysis:", "<a href=\"" + qcAnalysisUrl + "\">" + qcAnalysisDisplay + "</a>", rightFlexTable);		
		if(qcDate != null)
			addRowToTable(rowIndex++, "QC Date:", DisplayConstants.DATE_FORMAT.format(qcDate), rightFlexTable);
		
		// breadcrumbs
		breadcrumbDatasetSpan.setInnerHTML(datasetLink);
		breadcrumbTitleSpan.setInnerText(layerName);
		
		
	}
	
	@Override
	public void showDownload() {	
		if(!disableDownloads)
			licensedDownloader.showWindow();
	}

	@Override
	public void setLicenseAgreement(LicenseAgreement agreement) {		
		licensedDownloader.setLicenseAgreement(agreement);		
	}

	@Override
	public void requireLicenseAcceptance(boolean requireLicense) {
		licensedDownloader.setRequireLicenseAcceptance(requireLicense);		
	}

	@Override
	public void disableLicensedDownloads(boolean disable) {
		this.disableDownloads = true;
	}

	@Override
	public void setLicensedDownloads(List<FileDownload> downloads) {
		licensedDownloader.setDownloadUrls(downloads);
	}
	
	
	@Override
	public void setLayerPreviewTable(TableResults preview,
			String[] columnDisplayOrder,
			Map<String, String> columnDescriptions,
			Map<String, String> columnUnits) {
		// TODO : add data to static table		
		staticTable.setDimensions(1002, 175);
		staticTable.setTitle("Data Preview");		
		
		// create static table columns
		List<StaticTableColumn> stColumns = new ArrayList<StaticTableColumn>();
		for(String key : columnDisplayOrder) {
			StaticTableColumn stCol = new StaticTableColumn();
			stCol.setId(key);
			stCol.setName(key);
			
			// add units to column if available
			if (columnUnits.containsKey(key)) {
				stCol.setUnits(columnUnits.get(key));
			} 
			
			// add description if available
			if (columnDescriptions.containsKey(key)) {
				stCol.setTooltip(columnDescriptions.get(key));
			}
			
			stColumns.add(stCol);
		}
		
		staticTable.setDataAndColumnsInOrder(preview.getRows(), stColumns);
		previewTablePanel.setWidget(staticTable.asWidget());		
	}	

	@Override
	public void clear() {
		titleSpan.setInnerText("");		
		rightFlexTable.clear();
		staticTable.clear();
		downloadPanel.clear();
	}

	
	/*
	 * Private Methods
	 */
	/**
	 * Add a row to the provided FlexTable.
	 * 
	 * @param key
	 * @param value
	 * @param table
	 */
	private static void addRowToTable(int row, String key, String value, FlexTable table) {
		table.setHTML(row, 0, key);
		table.getCellFormatter().addStyleName(row, 0, "boldRight");
		table.setHTML(row, 1, value);		
	}

	private void setupLicensedDownloaderCallbacks() {
		// give the LicensedDownloader something to call when the view accepts the license
		licensedDownloader.setLicenseAcceptedCallback(new AsyncCallback<Void>() {
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
