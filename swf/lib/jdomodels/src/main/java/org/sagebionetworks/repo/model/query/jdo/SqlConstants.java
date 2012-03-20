package org.sagebionetworks.repo.model.query.jdo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.sagebionetworks.repo.model.Dataset;
import org.sagebionetworks.repo.model.Layer;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.NodeConstants;
import org.sagebionetworks.repo.model.jdo.BasicIdentifierFactory;
import org.sagebionetworks.repo.model.query.Compartor;
import org.sagebionetworks.repo.model.query.FieldType;
import org.sagebionetworks.repo.model.query.jdo.JDONodeQueryDaoImpl.AttributeDoesNotExist;
 
@SuppressWarnings("rawtypes")
public class SqlConstants {
	
	public static final String COL_ID					= "ID";
	// Node table constants
	public static final String TABLE_NODE 				= "JDONODE";
	public static final String COL_NODE_ID				= "ID";
	public static final String COL_NODE_PARENT_ID		= "PARENT_ID";
	public static final String COL_NODE_BENEFACTOR_ID	= "BENEFACTOR_ID";
	public static final String COL_NODE_NAME			= "NAME";
	public static final String COL_NODE_ANNOATIONS		= "ANNOTATIONS_ID_OID";
	public static final String COL_NODE_DESCRIPTION 	= "DESCRIPTION";
	public static final String COL_NODE_ETAG 			= "ETAG";
	public static final String COL_NODE_CREATED_BY 		= "CREATED_BY";
	public static final String COL_NODE_CREATED_ON 		= "CREATED_ON";
	public static final String COL_NODE_TYPE			= "NODE_TYPE";
	public static final String COL_NODE_ACL				= "NODE_ACL";
	public static final String COL_CURRENT_REV			= "CURRENT_REV_NUM";
	public static final String DDL_FILE_NODE			="schema/Node-ddl.sql";
	
	// The Revision table
	public static final String TABLE_REVISION 			= "JDOREVISION";
	public static final String COL_REVISION_OWNER_NODE	= "OWNER_NODE_ID";
	public static final String COL_REVISION_NUMBER		= "NUMBER";
	public static final String COL_REVISION_LABEL		= "LABEL";
	public static final String COL_REVISION_COMMENT		= "COMMENT";
	public static final String COL_REVISION_ANNOS_BLOB	= "ANNOTATIONS";
	public static final String COL_REVISION_REFS_BLOB	= "REFERENCES";
	public static final String COL_REVISION_MODIFIED_BY	= "MODIFIED_BY";
	public static final String COL_REVISION_MODIFIED_ON	= "MODIFIED_ON";
	public static final String DDL_FILE_REVISION		="schema/Revision-ddl.sql";

	// The Reference table
	public static final String TABLE_REFERENCE						= "JDOREFERENCE";
	public static final String COL_REFERENCE_OWNER_NODE				= "REF_OWNER_NODE_ID";
	public static final String COL_REFERENCE_TARGET_NODE			= "REF_TARGET_NODE_ID";
	public static final String COL_REFERENCE_TARGET_REVISION_NUMBER	= "REF_TARGET_REV_NUM";
	public static final String COL_REFERENCE_GROUP_NAME				= "REF_GROUP_NAME";
	public static final String DDL_FILE_REFERENCE					="schema/Reference-ddl.sql";
	
	// Annotations tables
	public static final String TABLE_STRING_ANNOTATIONS	= "JDOSTRINGANNOTATION";
	public static final String TABLE_DOUBLE_ANNOTATIONS	= "JDODOUBLEANNOTATION";
	public static final String TABLE_LONG_ANNOTATIONS	= "JDOLONGANNOTATION";
	public static final String TABLE_DATE_ANNOTATIONS	= "JDODATEANNOTATION";
	public static final String TABLE_STACK_STATUS		= "JDOSTACKSTATUS";
	
	
	// The User Profile table
	public static final String TABLE_USER_PROFILE				= "JDOUSERPROFILE";
	public static final String COL_USER_PROFILE_ID				= "ID";
	public static final String COL_USER_PROFILE_NAME			= "NAME";
	public static final String COL_USER_PROFILE_ETAG			= "ETAG";
	public static final String COL_USER_PROFILE_PROPS_BLOB		= "PROPERTIES";
	public static final String COL_USER_PROFILE_ANNOS_BLOB		= "ANNOTATIONS";
	public static final String DDL_FILE_USER_PROFILE			="schema/UserProfile-ddl.sql";
	// 
	public static final String COL_STACK_STATUS_STATUS				= "STATUS";
	public static final String COL_STACK_STATUS_CURRENT_MESSAGE		= "CURRENT_MESSAGE";
	public static final String COL_STACK_STATUS_PENDING_MESSAGE		= "PENDING_MESSAGE";
	
	// The width of the string annotations value column
	public static final int STRING_ANNOTATIONS_VALUE_LENGTH = 500;
	
	// There are the column names that all annotation tables have.
	public static final String ANNOTATION_ATTRIBUTE_COLUMN 		= "ATTRIBUTE";
	public static final String ANNOTATION_VALUE_COLUMN			= "VALUE";
	public static final String ANNOTATION_OWNER_ID_COLUMN		= "OWNER_ID";
	public static final String DDL_FILE_STRING_ANNOTATION		= "schema/StringAnnotation-ddl.sql";
	public static final String DDL_FILE_LONG_ANNOTATION			= "schema/LongAnnotation-ddl.sql";
	public static final String DDL_FILE_DATE_ANNOTATION			= "schema/DateAnnotation-ddl.sql";
	public static final String DDL_FILE_DOUBLE_ANNOTATION		= "schema/DoubleAnnotation-ddl.sql";
	
	// The name of the node type table.
	public static final String TABLE_NODE_TYPE				= "NODE_TYPE";
	public static final String COL_NODE_TYPE_NAME 			= "NAME";
	public static final String COL_NODE_TYPE_ID 			= "ID";
	public static final String DDL_FILE_NODE_TYPE			= "schema/NodeType-ddl.sql";
	
	
	public static final String TABLE_ANNOTATION_TYPE		= "ANNOTATION_TYPE";

	public static final String TABLE_USER					= "JDOUSER";
	public static final String TABLE_USER_GROUP				= "JDOUSERGROUP";
	public static final String TABLE_USER_GROUP_USERS		= "JDOUSERGROUPUSERS";
	public static final String COL_USER_GROUP_ID			= "ID";
	public static final String COL_USER_GROUP_NAME 			= "NAME";
	public static final String COL_USER_GROUP_IS_INDIVIDUAL = "ISINDIVIDUAL";
	
	public static final String TABLE_ACCESS_CONTROL_LIST = "ACL";
	public static final String COL_ACL_ID				= "ID";
	public static final String COL_ACL_OWNER_ID			= "NODE_OWNER";
	public static final String ACL_OWNER_ID_COLUMN		= "OWNER_ID_COLUMN";
	public static final String DDL_FILE_ACL				= "schema/ACL-ddl.sql";
	
	// The resource access table
	public static final String TABLE_RESOURCE_ACCESS			= "JDORESOURCEACCESS";
	public static final String COL_RESOURCE_ACCESS_OWNER		= "OWNER_ID";
	public static final String COL_RESOURCE_ACCESS_GROUP_ID		= "GROUP_ID";
	public static final String COL_RESOURCE_ACCESS_TYPE			= "RESOURCE_TYPE";
	public static final String COL_RESOURCE_ACCESS_RESOURCE_ID	= "RESOURCE_ID";
	public static final String COL_RESOURCE_ACCESS_ID			= "ID";
	public static final String DDL_FILE_RES_ACCESS				= "schema/ResourceAccess-ddl.sql";
	
	// The backup/restore status table
	public static final String TABLE_BACKUP_STATUS 				= "DAEMON_STATUS";
	public static final String COL_BACKUP_ID					= "ID";
	public static final String COL_BACKUP_STATUS				= "STATUS";
	public static final String COL_BACKUP_TYPE					= "TYPE";
	public static final String COL_BACKUP_STARTED_BY 			= "STARTED_BY";
	public static final String COL_BACKUP_STARTED_ON 			= "STARTED_ON";
	public static final String COL_BACKUP_PROGRESS_MESSAGE		= "PROGRESS_MESSAGE";
	public static final String COL_BAKUP_PROGRESS_CURRENT		= "PROGRESS_CURRENT";
	public static final String COL_BACKUP_PROGRESS_TOTAL		= "PROGRESS_TOTAL";
	public static final String COL_BACKUP_ERORR_MESSAGE			= "ERROR_MESSAGE";
	public static final String COL_BACKUP_ERROR_DETAILS			= "ERROR_DETAILS";
	public static final String COL_BACKUP_URL					= "BACKUP_URL";
	public static final String COL_BACKUP_RUNTIME				= "RUN_TIME_MS";
	// the max size of the error message.
	public static final int ERROR_MESSAGE_MAX_LENGTH			= 3000;
	
	public static final String TABLE_BACKUP_TERMINATE 			= "DAEMON_TERMINATE";
	public static final String COL_BACKUP_TERM_OWNER			= "BACKUP_OWNER";
	public static final String COL_BACKUP_FORCE_TERMINATION		= "FORCE_TERMINATION";
		
	// The resource access join table
	// datanucleus doesn't seem to be respecting the join table name when creating the schema
	// so I've modified the string to match the generated name
	public static final String TABLE_RESOURCE_ACCESS_TYPE		= "JDORESOURCEACCESS_ACCESSTYPE"; 
	public static final String COL_RESOURCE_ACCESS_TYPE_ID		= "ID_OID";
	public static final String COL_RESOURCE_ACCESS_TYPE_ELEMENT	= "STRING_ELE";
	public static final String DDL_FILE_RES_ACCESS_TYPE			= "schema/ResourceAccessType-ddl.sql";
	
	// This constraint ensure that children names are unique within their parent.
	public static final String CONSTRAINT_UNIQUE_CHILD_NAME = "NODE_UNIQUE_CHILD_NAME";
	
	
	// The alias used for the dataset table.
	public static final String NODE_ALIAS					= "nod";
	public static final String REVISION_ALIAS				= "rev";
	public static final String SORT_ALIAS					= "srt";
	public static final String EXPRESSION_ALIAS_PREFIX		= "exp";
	
	// This seems to be the name of the id column for all tables.
	public static final String COLUMN_ID		= "id";
	
	public static final String TYPE_COLUMN_NAME = "nodeType";
	
	public static final String AUTH_FILTER_ALIAS = "auth";
	
	
	public static final String[] PRIMARY_FIELDS;
	
	static {
		Field[] fields = Node.class.getDeclaredFields();
		PRIMARY_FIELDS = new String[fields.length];
		for(int i=0; i<fields.length; i++){
			PRIMARY_FIELDS[i] = fields[i].getName();
		}
	}
	

	
	// This is the alias of the sub-query used for sorting on annotations.
	public static final String ANNOTATION_SORT_SUB_ALIAS 	= "assa";
	
	public static final String OPERATOR_SQL_EQUALS					= "=";
	public static final String OPERATOR_SQL_DOES_NOT_EQUAL			= "!=";
	public static final String OPERATOR_SQL_GREATER_THAN			= ">";
	public static final String OPERATOR_SQL_LESS_THAN				= "<";
	public static final String OPERATOR_SQL_GREATER_THAN_OR_EQUALS	= ">=";
	public static final String OPERATOR_SQL_LESS_THAN_OR_EQUALS		= "<=";
		
	public static final String INPUT_DATA_LAYER_DATASET_ID = "INPUT_LAYERS_ID_OWN";
	
	private static final Map<String, String> primaryFieldColumns;

	static{
		// Map column names to the field names
		// RELEASE_DATE,STATUS,PLATFORM,PROCESSING_FACILITY,QC_BY,QC_DATE,TISSUE_TYPE,TYPE,CREATION_DATE,DESCRIPTION,PREVIEW,PUBLICATION_DATE,RELEASE_NOTES
		primaryFieldColumns = new HashMap<String, String>();
		SqlConstants.addAllFields(Node.class, primaryFieldColumns);
		// This is a special case for nodes.
		primaryFieldColumns.put(NodeConstants.COL_PARENT_ID, "PARENT_ID_OID");
		
		// These will be deleted once we move to NodeDao
		SqlConstants.addAllFields(Dataset.class, primaryFieldColumns);
		SqlConstants.addAllFields(Layer.class, primaryFieldColumns);
		primaryFieldColumns.put(NodeConstants.COL_PARENT_ID, "PARENT_ID");
		primaryFieldColumns.put("INPUT_LAYERS_ID_OWN", "INPUT_LAYERS_ID_OWN");
				
		
	}

	
	/**
	 * Add all of the fields for a given object.
	 * @param clazz
	 * @param map
	 */
	private static void addAllFields(Class clazz, Map<String, String> map){
		// This class generates the names the same way as datanucleus.
		BasicIdentifierFactory factory = new BasicIdentifierFactory();
		Field[] fields = clazz.getDeclaredFields();
		for(int i=0; i<fields.length; i++){
			if(!fields[i].isAccessible()){
				fields[i].setAccessible(true);
			}
			String fieldName = fields[i].getName();
			map.put(fieldName, factory.generateIdentifierNameForJavaName(fieldName));
		}
	}
	
	/**
	 * Get the database column name for a given primary field name.
	 * @param field
	 * @return
	 */
	public static String getColumnNameForPrimaryField(String field){
		if(field == null) return null;
		String column = primaryFieldColumns.get(field);
		if(column == null) throw new IllegalArgumentException("Unknown field: "+field);
		return column;
	}
		
	/**
	 * Translate an Comparator to SQL
	 * @param comp
	 * @return
	 */
	public static String getSqlForComparator(Compartor comp){
		if(Compartor.EQUALS == comp){
			return OPERATOR_SQL_EQUALS;
		}else if(Compartor.NOT_EQUALS == comp){
			return OPERATOR_SQL_DOES_NOT_EQUAL;
		}else if(Compartor.GREATER_THAN == comp){
			return OPERATOR_SQL_GREATER_THAN;
		}else if(Compartor.LESS_THAN == comp){
			return OPERATOR_SQL_LESS_THAN;
		}else if(Compartor.GREATER_THAN_OR_EQUALS == comp){
			return OPERATOR_SQL_GREATER_THAN_OR_EQUALS;
		}else if(Compartor.LESS_THAN_OR_EQUALS == comp){
			return OPERATOR_SQL_LESS_THAN_OR_EQUALS;
		}else{
			throw new IllegalArgumentException("Unsupported Compartor: "+comp);
		}
	}
	

}