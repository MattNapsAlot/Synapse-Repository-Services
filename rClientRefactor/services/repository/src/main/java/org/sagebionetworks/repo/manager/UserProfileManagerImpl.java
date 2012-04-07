/**
 * 
 */
package org.sagebionetworks.repo.manager;

import org.sagebionetworks.repo.model.ConflictingUpdateException;
import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.InvalidModelException;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.sagebionetworks.repo.model.UserInfo;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.UserProfileDAO;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.schema.ObjectSchema;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author brucehoff
 *
 */
public class UserProfileManagerImpl implements UserProfileManager {
	@Autowired
	private UserProfileDAO userProfileDAO;

	/***
	 *
	 * This method retrieves the JDO from the object ID and transfers it to the DTO, filtering
	 * out private fields if the 'userInfo' is not for the object owner or an admin 
	 *
	 */
	@Override
	public UserProfile getUserProfile(UserInfo userInfo, String ownerId)
			throws NotFoundException, DatastoreException, UnauthorizedException {
		ObjectSchema schema = SchemaCache.getSchema(UserProfile.class);
		UserProfile userProfile = userProfileDAO.get(ownerId, schema);
		boolean canSeePrivate = UserProfileManagerUtils.isOwnerOrAdmin(userInfo, userProfile.getOwnerId());
		if (!canSeePrivate) {
			UserProfileManagerUtils.clearPrivateFields(userProfile);
		}
		return userProfile;
	}


	/**
	 * 
	 * This method is only available to the object owner or an admin
	 * 
	 */
	@Override
	public UserProfile updateUserProfile(UserInfo userInfo, UserProfile updated)
			throws NotFoundException, DatastoreException,
			UnauthorizedException, ConflictingUpdateException,
			InvalidModelException {
		ObjectSchema schema = SchemaCache.getSchema(UserProfile.class);
		UserProfile userProfile = userProfileDAO.get(updated.getOwnerId(), schema);
		boolean canUpdate = UserProfileManagerUtils.isOwnerOrAdmin(userInfo, userProfile.getOwnerId());
		if (!canUpdate) throw new UnauthorizedException("Only owner or administrator may update UserProfile.");
		return userProfileDAO.update(updated, schema);
	}


}
