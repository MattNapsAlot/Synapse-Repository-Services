package org.sagebionetworks.web.client.presenter;

import java.util.LinkedList;
import java.util.List;

import org.sagebionetworks.web.client.SearchServiceAsync;
import org.sagebionetworks.web.client.cookie.CookieKeys;
import org.sagebionetworks.web.client.cookie.CookieProvider;
import org.sagebionetworks.web.client.cookie.CookieUtils;
import org.sagebionetworks.web.client.place.DynamicTest;
import org.sagebionetworks.web.client.view.DynamicTableView;
import org.sagebionetworks.web.client.view.RowData;
import org.sagebionetworks.web.shared.HeaderData;
import org.sagebionetworks.web.shared.SearchParameters;
import org.sagebionetworks.web.shared.TableResults;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;


public class DynamicTablePresenter extends AbstractActivity implements DynamicTableView.Presenter{
	
	private DynamicTableView view;
	private SearchServiceAsync service;

	private String sortKey = null;
	private boolean ascending = false;
	
	// This keeps track of which page we are on.
	private int paginationOffest = 0;
	private int paginationLength = 10;
	private CookieProvider cookieProvider;
	private List<HeaderData> currentColumns = null;
	

	@Inject
	public DynamicTablePresenter(DynamicTableView view, SearchServiceAsync service, CookieProvider cookieProvider){
		this.view = view;
		this.service = service;
		this.view.setPresenter(this);
		this.cookieProvider = cookieProvider;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		// First refresh from the server
		refreshFromServer();

		// Add the view to the main container
		panel.setWidget(view.asWidget());	
	}
	
	/**
	 * Get the columns to display.
	 * @return
	 */
	public List<String> getDisplayColumns(){
		// This is bad as it ties directly to datastes.
		String cookie = cookieProvider.getCookie(CookieKeys.SELECTED_DATASETS_COLUMNS);
		// if the cookie is not null then create list
		if(cookie != null){
			return CookieUtils.createListFromString(cookie);
		}else{
			// Return an empty list, which will be interpreted as the default
			return new LinkedList<String>();
		}
	}
	
	/**
	 * Helper for getting the search parameters that will be used.
	 * This is allows tests to make the parameters used by this class.
	 * @return
	 */
	public SearchParameters getCurrentSearchParameters(){
		return new SearchParameters(getDisplayColumns(), paginationOffest, paginationLength, sortKey, ascending);
	}

	/**
	 * Asynchronous call that will execute the current query and set the results.
	 */
	public void refreshFromServer() {
		service.executeSearch(getCurrentSearchParameters(), new AsyncCallback<TableResults>() {
			
			@Override
			public void onSuccess(TableResults result) {
				setTableResults(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				view.showMessage(caught.getMessage());
			}
		});
	}

	public void setTableResults(TableResults result) {
		// First, set the columns
		setCurrentColumns(result.getColumnInfoList());
		// Now set the rows
		RowData data = new RowData(result.getRows(), paginationOffest, paginationLength, result.getTotalNumberResults(), sortKey, ascending);
		view.setRows(data);
	}

	/**
	 * Set the current columns
	 * @param columnInfoList
	 */
	public void setCurrentColumns(List<HeaderData> columnInfoList) {
		// If this list has changed then we need to let the view know
		// about the new columns
		if(!matchesCurrentColumns(columnInfoList)){
			// The columns have change so we need to update the view
			this.currentColumns = columnInfoList;
			view.setColumns(columnInfoList);
		}
	}
	
	/**
	 * Does the current column list match the new list
	 * @param other
	 * @return
	 */
	public boolean matchesCurrentColumns(List<HeaderData> other){
		if (currentColumns == null) {
			if (other != null)
				return false;
		}
		if(currentColumns.size() != other.size()) return false;
		// If a new type of HeaderData is added, and they do
		// not implement equals() (a very likely scenario) we
		// do not want to rebuild the whole table each time.
		// Rather we only want to rebuild the table when
		// a column id has changed.
		for(int i=0; i<currentColumns.size(); i++){
			String thisKey = currentColumns.get(i).getId();
			String otherKey = other.get(i).getId();
			if(!thisKey.equals(otherKey)) return false;
		}
		return true;
	}
	
	@Override
	public void pageTo(int start, int length) {
		this.paginationOffest = start;
		this.paginationLength = length;
		refreshFromServer();
	}

	@Override
	public void toggleSort(String columnKey) {
		// We need to resynch
		sortKey = columnKey;
		ascending = !ascending;
		refreshFromServer();
	}

	public void setPlace(DynamicTest place) {
		// TODO Auto-generated method stub
		
	}

	public String getSortKey() {
		return sortKey;
	}

	public boolean isAscending() {
		return ascending;
	}

	public int getPaginationOffest() {
		return paginationOffest;
	}

	public int getPaginationLength() {
		return paginationLength;
	}
	
}
