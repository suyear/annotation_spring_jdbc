package com.common.annotation;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;

import com.common.util.Tool;
import com.mysql.jdbc.Statement;

public class SqlGetter {
	protected final Log log = LogFactory.getLog(getClass());
	
	public static void main(String[] args) throws Exception {  
        User user = new User();  
        user.setName("nihao");  
        user.setUserAge(12);
        user.setEmail("1231231");
        user.setQq("ces");
        
        
        //user.getClass().getAnnotations()
        
        SqlGetter sqlGetter=new SqlGetter();
        Field field=user.getClass().getDeclaredField("id");
        System.out.println(sqlGetter.getColumn(user,"id"));
        sqlGetter.getSearchSql(user);
    }  
  
	
	/**
	 * 根据对象，以及单个属性获取对应数据库的字段
	 * @param obj
	 * @param arg
	 * @return
	 */
	public String getColumn(Object obj,String arg){
		try {
			SqlGetter sqlGetter=new SqlGetter();
			Field field=obj.getClass().getDeclaredField(arg);
			return sqlGetter.getFieldColumn(field);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 生成PreparedStatement对象
	 * @param sql
	 * @param obj
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public PreparedStatement getPs(String sql,Object obj,Connection con) throws Exception{
		try {
			PreparedStatement ps=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			return ps;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 封装Rowmapper
	 * @param obj
	 * @return
	 */
	public RowMapper getRowMapper(Class<?> cls){
		ObjectRowMapper obj=new ObjectRowMapper(cls);
		return obj;
	}
	
	
	
	/**
	 * 生成插入SQL
	 * @param obj
	 * @return
	 * @throws Exception
	 */
    public String getInsertSql(Object obj) throws Exception {  
        //获取table名称  
        String tableName=obj.getClass().getAnnotation(TableName.class).value();  
          
        // 存放column---value  
        TreeMap<String, Object> kvs = new TreeMap<String, Object>();  
        TreeMap<String, String> vType = new TreeMap<String, String>();
        Field[] fs = obj.getClass().getDeclaredFields();
        
        for (Field f : fs) {  
            String cn = this.getFieldColumn(f);
            String type=f.getType().toString();
            if(type.equals("class java.lang.String")){
            	vType.put(f.getName(), "String");
            }
            if(type.equals("class java.sql.Timestamp")){
            	vType.put(f.getName(), "Timestamp");
            }
            if (cn != null) {  
                kvs.put(f.getName(), this.getFieldValue(obj,f));  
            }  
        }  
        StringBuilder prefix = new StringBuilder();  
        StringBuilder suffix = new StringBuilder();  
        for(Iterator<String> it=kvs.keySet().iterator();it.hasNext();){  
            String key=it.next();  
            String type=vType.get(key);
            String value=kvs.get(key)==null ? "undefinedStr" :kvs.get(key).toString();
            if("undefinedStr".equals(value)){
            	continue;
            }
            value=Tool.filterStr(value);
            if("id".equals(key) && "0".equals(value)){
            	continue;
            }
            
            prefix.append(key);  
            
            if("String".equals(type)){
            	suffix.append("'"+value+"'");
            }else if("Timestamp".equals(type)){
            	suffix.append("'"+value.toString().substring(0,19)+"'");
            }else{
            	suffix.append(value);	
            }
            
            if (it.hasNext()) {  
                prefix.append(",");  
                suffix.append(",");  
            }  
        }         
        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName,prefix,suffix);  
    }  
    
    
    /**
     * 生成查询SQL
     * @param obj
     * @return
     * @throws Exception
     */
    public String getSearchSql(Object obj) throws Exception {  
        String tableName=obj.getClass().getAnnotation(TableName.class).value();  
        Field field=obj.getClass().getDeclaredField("id");
        int value=Integer.valueOf(this.getFieldValue(obj,field).toString());
        return String.format("SELECT * FROM "+tableName+" WHERE id="+value);  
    }  
    
    
    /**
     * 生成删除SQL
     * @param obj
     * @return
     * @throws Exception
     */
    public String getDeleteSql(Object obj) throws Exception {  
        String tableName=obj.getClass().getAnnotation(TableName.class).value();  
        Field field=obj.getClass().getDeclaredField("id");
        int value=Integer.valueOf(this.getFieldValue(obj,field).toString());
        return String.format("UPDATE "+tableName+" SET is_delete=1 WHERE id="+value);  
    }
    
    
    /**
     * 生成更新SQL
     * @param obj
     * @return
     * @throws Exception
     */
    public String getUpdateSql(Object obj) throws Exception {  
        //获取table名称  
        String tableName=obj.getClass().getAnnotation(TableName.class).value();  
          
        // 存放column---value  
        TreeMap<String, Object> kvs = new TreeMap<String, Object>();  
        TreeMap<String, String> vType = new TreeMap<String, String>();
        Field[] fs = obj.getClass().getDeclaredFields();
        
        for (Field f : fs) {  
            String cn = this.getFieldColumn(f);
            String type=f.getType().toString();
            if(type.equals("class java.lang.String")){
            	vType.put(f.getName(), "String");
            }
            if(type.equals("class java.sql.Timestamp")){
            	vType.put(f.getName(), "Timestamp");
            }
            if (cn != null) {  
                kvs.put(f.getName(), this.getFieldValue(obj,f));  
            }  
        }  
        StringBuilder prefix = new StringBuilder();  
        for(Iterator<String> it=kvs.keySet().iterator();it.hasNext();){  
        	 String key=it.next();  
             String type=vType.get(key);
             String value=kvs.get(key)==null ? "undefinedStr" :kvs.get(key).toString();
             if("undefinedStr".equals(value)){
             	continue;
             }
             value=Tool.filterStr(value);
             if("id".equals(key) && "0".equals(value)){
             	continue;
             }
            
            if("String".equals(type)){
            	prefix.append(key+"='"+value+"'");
            }else if("Timestamp".equals(type)){
            	prefix.append(key+"='"+value.substring(0,19)+"'");
            }else{
            	prefix.append(key+"="+value);
            }
            
            if (it.hasNext()) {  
                prefix.append(",");  
            }  
        }      
        
        Field field=obj.getClass().getDeclaredField("id");
        int id=Integer.valueOf(this.getFieldValue(obj,field).toString());
        return String.format("UPDATE %s SET %s WHERE id="+id, tableName,prefix);  
    }  
  
    
    public String getFieldColumn(Field field) {  
        Column column = field.getAnnotation(Column.class);  
        if (column != null) {  
            if ("".equals(column.value())){  
                return field.getName();  
            } else {  
                return column.value();  
            }  
        }  
        return null;  
    }  
  
    public Object getFieldValue(Object obj,Field field) throws Exception {  
        String name = field.getName();  
        String c = name.substring(0, 1);  
        name = name.replaceFirst(c, c.toUpperCase());  
        Method m = obj.getClass().getMethod("get" + name, new Class<?>[] {});  
        return m.invoke(obj, new Object[] {});  
    }  
}
