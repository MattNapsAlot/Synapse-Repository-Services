package org.sagebionetworks.web.client.view;

import org.sagebionetworks.web.client.IconsImageBundle;
import org.sagebionetworks.web.client.SageImageBundle;
import org.sagebionetworks.web.client.events.CancelEvent;
import org.sagebionetworks.web.client.events.CancelHandler;
import org.sagebionetworks.web.client.events.PersistSuccessEvent;
import org.sagebionetworks.web.client.events.PersistSuccessHandler;
import org.sagebionetworks.web.client.widget.editpanels.NodeEditor;
import org.sagebionetworks.web.client.widget.footer.Footer;
import org.sagebionetworks.web.client.widget.header.Header;
import org.sagebionetworks.web.client.widget.header.Header.MenuItems;
import org.sagebionetworks.web.client.widget.table.QueryServiceTable;
import org.sagebionetworks.web.client.widget.table.QueryServiceTableResourceProvider;
import org.sagebionetworks.web.shared.NodeType;
import org.sagebionetworks.web.shared.QueryConstants.ObjectType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ProjectsHomeViewImpl extends Composite implements ProjectsHomeView {

	public interface ProjectsHomeViewImplUiBinder extends UiBinder<Widget, ProjectsHomeViewImpl> {}

	@UiField
	SimplePanel header;
	@UiField
	SimplePanel footer;
	@UiField
	SimplePanel tablePanel;
	@UiField
	SimplePanel createProjectButtonPanel;
		
	private Presenter presenter;
	private QueryServiceTable queryServiceTable;
	private QueryServiceTableResourceProvider queryServiceTableResourceProvider;
	private IconsImageBundle icons;
	private NodeEditor nodeEditor;
	private Header headerWidget;

	private final int INITIAL_QUERY_TABLE_OFFSET = 0;
	private final int QUERY_TABLE_LENGTH = 20;
	
	@Inject
	public ProjectsHomeViewImpl(ProjectsHomeViewImplUiBinder binder,
			Header headerWidget, Footer footerWidget, IconsImageBundle icons,
			SageImageBundle imageBundle,
			QueryServiceTableResourceProvider queryServiceTableResourceProvider,
			final NodeEditor nodeEditor) {		
		initWidget(binder.createAndBindUi(this));

		this.queryServiceTableResourceProvider = queryServiceTableResourceProvider;
		this.icons = icons;
		this.nodeEditor = nodeEditor;
		this.headerWidget = headerWidget;
		
		header.add(headerWidget.asWidget());
		footer.add(footerWidget.asWidget());
		headerWidget.setMenuItemActive(MenuItems.PROJECTS);

	}


	@Override
	public void setPresenter(final Presenter presenter) {
		this.presenter = presenter;		
		headerWidget.refresh();
				
		this.queryServiceTable = new QueryServiceTable(queryServiceTableResourceProvider, ObjectType.project, true, 1000, 487, presenter.getPlaceChanger());		
		// Start on the first page and trigger a data fetch from the server
		queryServiceTable.pageTo(INITIAL_QUERY_TABLE_OFFSET, QUERY_TABLE_LENGTH);
		tablePanel.clear();
		tablePanel.add(queryServiceTable.asWidget());

				
		Button createProjectButton = new Button("Start a Project", AbstractImagePrototype.create(icons.addSquare16()));
		createProjectButton.addSelectionListener(new SelectionListener<ButtonEvent>() {			
			@Override
			public void componentSelected(ButtonEvent ce) {								
				final Window window = new Window();  
				window.setSize(600, 240);
				window.setPlain(true);
				window.setModal(true);
				window.setBlinkModal(true);
				window.setHeading("Start a Project");
				window.setLayout(new FitLayout());								
				nodeEditor.addCancelHandler(new CancelHandler() {					
					@Override
					public void onCancel(CancelEvent event) {
						window.hide();
					}
				});
				nodeEditor.addPersistSuccessHandler(new PersistSuccessHandler() {					
					@Override
					public void onPersistSuccess(PersistSuccessEvent event) {
						window.hide();
						queryServiceTable.refreshFromServer();
					}
				});
				nodeEditor.setPlaceChanger(presenter.getPlaceChanger());
				window.add(nodeEditor.asWidget(NodeType.PROJECT), new FitData(4));						
				window.show();			
			}
		});
		createProjectButtonPanel.clear();
		createProjectButtonPanel.add(createProjectButton);		
	}

	@Override
	public void showErrorMessage(String message) {
		MessageBox.info("Message", message, null);
	}

}