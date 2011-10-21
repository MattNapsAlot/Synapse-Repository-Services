package org.sagebionetworks.repo.model.query.jdo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;

import org.junit.Test;
import org.sagebionetworks.repo.model.Dataset;
import org.sagebionetworks.repo.model.Layer;
import org.sagebionetworks.repo.model.jdo.persistence.JDOAnnotationType;
import org.sagebionetworks.repo.model.jdo.persistence.JDODateAnnotation;
import org.sagebionetworks.repo.model.jdo.persistence.JDODoubleAnnotation;
import org.sagebionetworks.repo.model.jdo.persistence.JDOLongAnnotation;
import org.sagebionetworks.repo.model.jdo.persistence.JDONode;
import org.sagebionetworks.repo.model.jdo.persistence.JDONodeType;
import org.sagebionetworks.repo.model.jdo.persistence.JDOResourceAccess;
import org.sagebionetworks.repo.model.jdo.persistence.JDOStringAnnotation;
import org.sagebionetworks.repo.model.jdo.persistence.JDOUserGroup;
import org.sagebionetworks.repo.model.query.Compartor;
import org.sagebionetworks.repo.model.query.FieldType;

public class SqlConstantsTest {
	
	@Test
	public void testgetClassForFieldType(){
		// Make sure we can get each type
		// Date
		Class clazz = SqlConstants.getJdoClassForFieldType(FieldType.DATE_ATTRIBUTE);
		assertEquals(JDODateAnnotation.class, clazz);
		// Long
		clazz = SqlConstants.getJdoClassForFieldType(FieldType.LONG_ATTRIBUTE);
		assertEquals(JDOLongAnnotation.class, clazz);
		// String
		clazz = SqlConstants.getJdoClassForFieldType(FieldType.STRING_ATTRIBUTE);
		assertEquals(JDOStringAnnotation.class, clazz);
		// Double
		clazz = SqlConstants.getJdoClassForFieldType(FieldType.DOUBLE_ATTRIBUTE);
		assertEquals(JDODoubleAnnotation.class, clazz);
	}
	
	@Test
	public void testGetSqlForAllComparator(){
		// Make sure we support all types
		Compartor[] all = Compartor.values();
		for(Compartor comp: all){
			String sql = SqlConstants.getSqlForComparator(comp);
			assertNotNull(sql);
		}
	}
	
	@Test
	public void testEquals(){
		assertEquals("=", SqlConstants.getSqlForComparator(Compartor.EQUALS));
	}

	@Test
	public void testGreater(){
		assertEquals(">", SqlConstants.getSqlForComparator(Compartor.GREATER_THAN));
	}
	
	@Test
	public void testLesss(){
		assertEquals("<", SqlConstants.getSqlForComparator(Compartor.LESS_THAN));
	}
	
	@Test
	public void testGreaterThanOrEquals(){
		assertEquals(">=", SqlConstants.getSqlForComparator(Compartor.GREATER_THAN_OR_EQUALS));
	}
	
	@Test
	public void testLessThanOrEquals(){
		assertEquals("<=", SqlConstants.getSqlForComparator(Compartor.LESS_THAN_OR_EQUALS));
	}
	
	@Test
	public void testgetColumnNameForPrimaryFieldDatasets(){
		Field[] fields = Dataset.class.getDeclaredFields();
		for(int i=0; i<fields.length; i++){
			if(!fields[i].isAccessible()){
				fields[i].setAccessible(true);
			}
			String fieldName = fields[i].getName();
			// Make sure we can get each
			String column = SqlConstants.getColumnNameForPrimaryField(fieldName);
			assertNotNull(column);
			System.out.println("Field: "+fieldName+" maps to column: "+column);
		}
	}
	
	@Test
	public void testgetColumnNameForPrimaryFieldLayers(){
		Field[] fields = Layer.class.getDeclaredFields();
		for(int i=0; i<fields.length; i++){
			if(!fields[i].isAccessible()){
				fields[i].setAccessible(true);
			}
			String fieldName = fields[i].getName();
			// Make sure we can get each
			String column = SqlConstants.getColumnNameForPrimaryField(fieldName);
			assertNotNull(column);
			System.out.println("Field: "+fieldName+" maps to column: "+column);
		}
	}
	
	@Test
	public void testNodeClass(){
		assertEquals(SqlConstants.TABLE_NODE, SqlConstants.getTableForClass(JDONode.class));
	}
	@Test
	public void testNodeTypeClass(){
		assertEquals(SqlConstants.TABLE_NODE_TYPE, SqlConstants.getTableForClass(JDONodeType.class));
	}
	@Test
	public void testAnnotationTypeClass(){
		assertEquals(SqlConstants.TABLE_ANNOTATION_TYPE, SqlConstants.getTableForClass(JDOAnnotationType.class));
	}
	@Test
	public void testStringAnnotationClass(){
		assertEquals(SqlConstants.TABLE_STRING_ANNOTATIONS, SqlConstants.getTableForClass(JDOStringAnnotation.class));
	}
	@Test
	public void testLongAnnotationClass(){
		assertEquals(SqlConstants.TABLE_LONG_ANNOTATIONS, SqlConstants.getTableForClass(JDOLongAnnotation.class));
	}
	@Test
	public void testDoubleAnnotationClass(){
		assertEquals(SqlConstants.TABLE_DOUBLE_ANNOTATIONS, SqlConstants.getTableForClass(JDODoubleAnnotation.class));
	}
	@Test
	public void testDateAnnotationClass(){
		assertEquals(SqlConstants.TABLE_DATE_ANNOTATIONS, SqlConstants.getTableForClass(JDODateAnnotation.class));
	}

	@Test
	public void testUserGroupClass(){
		assertEquals(SqlConstants.TABLE_USER_GROUP, SqlConstants.getTableForClass(JDOUserGroup.class));
	}
	@Test
	public void testResourceAccessClass(){
		assertEquals(SqlConstants.TABLE_RESOURCE_ACCESS, SqlConstants.getTableForClass(JDOResourceAccess.class));
	}
}