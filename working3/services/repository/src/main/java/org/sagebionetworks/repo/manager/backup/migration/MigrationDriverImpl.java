package org.sagebionetworks.repo.manager.backup.migration;

import java.util.LinkedList;
import java.util.List;

import org.sagebionetworks.repo.model.NodeRevisionBackup;
import org.sagebionetworks.repo.model.EntityType;

/**
 * This is a simple step-wise migration driver implementation. 
 * 
 * @author jmhill
 *
 */
public class MigrationDriverImpl implements MigrationDriver{
	
	/**
	 * Each revision step is responsible for migrating the object a single revision.
	 * For example, a single step is used to go from version "0.0" to "0.1"
	 */
	private List<RevisionMigrationStep> revisionSteps = null;
	
	/**
	 * For now each step does not need any spring beans so we create the steps in the constructor.
	 */
	public MigrationDriverImpl(){
		revisionSteps = new LinkedList<RevisionMigrationStep>();
		// The first step goes from v0 to v1
		revisionSteps.add(new RevisionStepV0toV1());
	}

	/**
	 * Walk over each step until we get to the current version.
	 */
	@Override
	public NodeRevisionBackup migrateToCurrentVersion(NodeRevisionBackup toMigrate,	EntityType type) {
		if(toMigrate == null) throw new IllegalArgumentException("NodeRevsion toMigrate cannot be null");
		if(type == null) throw new IllegalArgumentException("ObjectType cannot be null");
		String startingVersion = toMigrate.getXmlVersion();
		// Take each step to get to the current version
		for(RevisionMigrationStep step: revisionSteps){
			toMigrate = step.migrateOneStep(toMigrate, type);
		}
		// Validate we are on the current version
		if(!NodeRevisionBackup.CURRENT_XML_VERSION.equals(toMigrate.getXmlVersion())) throw new IllegalStateException("Failed to migrate a NodeRevisionBackup from version: "+startingVersion+" to the current version: "+NodeRevisionBackup.CURRENT_XML_VERSION);
		return toMigrate;
	}

}
