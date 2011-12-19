package org.sagebionetworks.web.client.presenter;

import java.util.List;

import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.PlaceChanger;
import org.sagebionetworks.web.client.cookie.CookieKeys;
import org.sagebionetworks.web.client.cookie.CookieProvider;
import org.sagebionetworks.web.client.cookie.CookieUtils;
import org.sagebionetworks.web.client.place.DatasetsHome;
import org.sagebionetworks.web.client.view.DatasetsHomeView;
import org.sagebionetworks.web.shared.QueryConstants.ObjectType;
import org.sagebionetworks.web.shared.WhereCondition;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class DatasetsHomePresenter extends AbstractActivity implements DatasetsHomeView.Presenter {
	
	public static final String KEY_DATASETS_SELECTED_COLUMNS_COOKIE = CookieKeys.SELECTED_DATASETS_COLUMNS;
	public static final String KEY_DATASETS_APPLIED_FILTERS_COOKIE = CookieKeys.APPLIED_DATASETS_FILTERS;

	private DatasetsHome place;
	private DatasetsHomeView view;
	private ColumnsPopupPresenter columnsPopupPresenter;
	private CookieProvider cookieProvider; 
	private PlaceChanger placeChanger;
	GlobalApplicationState globalApplicationState;
	
	@Inject
	public DatasetsHomePresenter(DatasetsHomeView view, ColumnsPopupPresenter columnsPopupPresenter, CookieProvider cookieProvider, GlobalApplicationState globalApplicationState){
		this.view = view;
		this.columnsPopupPresenter = columnsPopupPresenter;
		this.globalApplicationState = globalApplicationState;
		this.placeChanger = globalApplicationState.getPlaceChanger();

		// Set the presenter on the view
		this.cookieProvider = cookieProvider;
		view.setPresenter(this);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		// Setup the columns
		setVisibleColumns();
		// Setup the filters
		setAppliedFilters();
		// Install the view
		panel.setWidget(view);
	}

	public void setPlace(DatasetsHome place) {
		this.place = place;
		view.setPresenter(this);
	}

	@Override
	public void onEditColumns() {
		// Start with a null list
		List<String> currentSelection = null;
		String cookieValue = this.cookieProvider.getCookie(KEY_DATASETS_SELECTED_COLUMNS_COOKIE);
		if(cookieValue != null){
			currentSelection = CookieUtils.createListFromString(cookieValue);
		}

		// Show the view.
		columnsPopupPresenter.showPopup(ObjectType.dataset.name(), currentSelection, new ColumnSelectionChangeListener() {
			
			@Override
			public void columnSelectionChanged(List<String> newSelection) {
				// Save the value back in a cookie
				String newValue = CookieUtils.createStringFromList(newSelection);
				cookieProvider.setCookie(KEY_DATASETS_SELECTED_COLUMNS_COOKIE, newValue);
				setVisibleColumns();
			}
		});
	}
	
	private void setVisibleColumns(){
		List<String> currentSelection = null;
		String cookieValue = this.cookieProvider.getCookie(KEY_DATASETS_SELECTED_COLUMNS_COOKIE);
		if(cookieValue != null){
			currentSelection = CookieUtils.createListFromString(cookieValue);
			view.setVisibleColumns(currentSelection);
		}
	}
	
	private void setAppliedFilters() {
		List<WhereCondition> currentFilters = null;
		String cookieValue = this.cookieProvider.getCookie(KEY_DATASETS_APPLIED_FILTERS_COOKIE);
		if(cookieValue != null) {
			currentFilters = CookieUtils.createWhereListFromString(cookieValue);
			view.setAppliedFilters(currentFilters);
		}
	}

	@Override
	public PlaceChanger getPlaceChanger() {
		return placeChanger;
	}

	@Override
    public String mayStop() {
        view.clear();
        return null;
    }

	@Override
	public void onChangeFilter(List<WhereCondition> newConditions) {
		String newValue = CookieUtils.createStringFromWhereList(newConditions);
		cookieProvider.setCookie(KEY_DATASETS_APPLIED_FILTERS_COOKIE, newValue);
	}

}