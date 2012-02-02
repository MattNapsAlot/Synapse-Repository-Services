package org.sagebionetworks.repo.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sagebionetworks.repo.manager.backup.SearchDocumentDriverImpl;
import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.model.UserInfo;
import org.sagebionetworks.repo.model.search.Facet;
import org.sagebionetworks.repo.model.search.FacetConstraint;
import org.sagebionetworks.repo.model.search.FacetTypeNames;
import org.sagebionetworks.repo.model.search.Hit;
import org.sagebionetworks.repo.model.search.SearchResults;

/**
 * Helper functions for search functionality
 * 
 * @author deflaux
 * 
 */
public class SearchHelper {

	private static final Logger log = Logger.getLogger(SearchHelper.class
			.getName());

	/**
	 * Formulate the boolean query to enforce an access controll list for a particular user
	 * 
	 * @param userInfo
	 * @return the acl boolean query
	 * @throws DatastoreException
	 */
	public static String formulateAuthorizationFilter(UserInfo userInfo)
			throws DatastoreException {
		Collection<UserGroup> groups = userInfo.getGroups();
		if (0 == groups.size()) {
			// being extra paranoid here, this is unlikely
			throw new DatastoreException("no groups for user " + userInfo);
		}

		// Make our boolean query
		String authorizationFilter = "";
		for (UserGroup group : groups) {
			if (0 < authorizationFilter.length()) {
				authorizationFilter += " ";
			}
			authorizationFilter += SearchDocumentDriverImpl.ACL_INDEX_FIELD + ":'" + group.getName()
					+ "'";
		}
		if (1 == groups.size()) {
			authorizationFilter = "bq=" + authorizationFilter;
		} else {
			authorizationFilter = "bq=(or " + authorizationFilter + ")";
		}
		return authorizationFilter;
	}

	/**
	 * Merge and escape CloudSearch boolean queries as needed
	 * 
	 * @param query
	 * @return the cleaned up query string
	 * @throws UnsupportedEncodingException
	 */
	public static String cleanUpBooleanSearchQueries(String query)
			throws UnsupportedEncodingException {

		// Make sure the url is well-formed so that we can correctly clean it up
		String decodedQuery = URLDecoder.decode(query, "UTF-8");
		if (decodedQuery.contains("%")) {
			throw new IllegalArgumentException("Query is incorrectly encoded: "
					+ decodedQuery);
		}

		String booleanQuery = "";
		String escapedQuery = "";
		int numAndClauses = 0;
		String splits[] = decodedQuery.split("&");
		for (int i = 0; i < splits.length; i++) {
			if (0 == splits[i].indexOf("bq=")) {
				if (0 < booleanQuery.length()) {
					booleanQuery += " ";
				}
				String bqValue = splits[i].substring(3);
				if (0 == bqValue.indexOf("(and ")) {
					bqValue = bqValue.substring(5, bqValue.length() - 1);
					numAndClauses++;
				}
				booleanQuery += bqValue;
				numAndClauses++;
			} else {
				if (0 < escapedQuery.length()) {
					escapedQuery += "&";
				}
				escapedQuery += splits[i];
			}
		}

		if (0 != booleanQuery.length()) {
			if (1 < numAndClauses) {
				booleanQuery = "(and " + booleanQuery + ")";
			}

			if (0 < escapedQuery.length()) {
				escapedQuery += "&";
			}
			escapedQuery += "bq=" + URLEncoder.encode(booleanQuery, "UTF-8");
		}
		return escapedQuery;
	}

	/**
	 * Convert from CloudSearch JSON to a JSONEntity
	 * 
	 * TODO later when Schema2Pojo supports maps, make a JSONEntity for the
	 * CloudSearch object
	 * 
	 * @param cloudSearchResponse
	 * @return the SearchResults JSONEntity
	 * @throws JSONException
	 */
	public static SearchResults cloudSearchToSynapseSearchResults(
			String cloudSearchResponse) throws JSONException {
		JSONObject csResults = new JSONObject(cloudSearchResponse);
		SearchResults results = new SearchResults();
		results.setMatchExpression(csResults.getString("match-expr"));
		results.setFound(csResults.getJSONObject("hits").getLong("found"));
		results.setStart(csResults.getJSONObject("hits").getLong("start"));
		List<Hit> hits = new ArrayList<Hit>();
		results.setHits(hits);

		JSONArray csHits = csResults.getJSONObject("hits").getJSONArray("hit");
		for (int i = 0; i < csHits.length(); i++) {
			JSONObject csHit = csHits.getJSONObject(i).getJSONObject("data");
			Hit hit = new Hit();
			if (csHit.has("id")) {
				hit.setId(csHit.getString("id"));
			}
			if (csHit.has("name")) {
				hit.setName(csHit.getString("name"));
			}
			if (csHit.has("etag")) {
				hit.setEtag(csHit.getString("etag"));
			}
			if (csHit.has("description")) {
				hit.setDescription(csHit.getString("description"));
			}
			hits.add(hit);
		}

		if (!csResults.has("facets")) {
			return results;
		}

		List<Facet> facets = new ArrayList<Facet>();
		results.setFacets(facets);

		JSONObject csFacets = csResults.getJSONObject("facets");
		for (String facetName : JSONObject.getNames(csFacets)) {
			Facet facet = new Facet();
			facet.setName(facetName);

			FacetTypeNames facetType = SearchDocumentDriverImpl.FACET_TYPES
					.get(facetName);
			if (null == facetType) {
				// Skip over this facet since we do not know what type it is
				log
						.warn("facet "
								+ facetName
								+ " is not properly configured, add it to the facet type map");
				continue;
			}

			if (FacetTypeNames.DATE == facetType
					|| FacetTypeNames.CONTINUOUS == facetType) {
				facet.setMin(csFacets.getJSONObject(facetName).getLong("min"));
				facet.setMax(csFacets.getJSONObject(facetName).getLong("max"));
			}
			facets.add(facet);

			JSONArray csConstraints = csFacets.getJSONObject(facetName)
					.optJSONArray("constraints");
			if (null == csConstraints)
				continue;

			List<FacetConstraint> constraints = new ArrayList<FacetConstraint>();
			facet.setConstraints(constraints);
			for (int i = 0; i < csConstraints.length(); i++) {
				JSONObject csConstraint = csConstraints.getJSONObject(i);
				FacetConstraint constraint = new FacetConstraint();
				constraint.setValue(csConstraint.getString("value"));
				constraint.setCount(csConstraint.getLong("count"));
				constraints.add(constraint);
			}
		}

		return results;
	}
}