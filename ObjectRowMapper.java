package com.common.annotation;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.common.util.Tool;

public class ObjectRowMapper implements RowMapper {
	private Class className;

	public ObjectRowMapper(Class className) {
		this.className = className;
	}

	/*
	 * 根据类型对具体对象属性赋值
	 */
	public static void setFieldValue(Object form, Field field, String value) {

		String elemType = field.getType().toString();

		if (elemType.indexOf("boolean") != -1
				|| elemType.indexOf("Boolean") != -1) {
			try {
				field.set(form, Boolean.valueOf(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (elemType.indexOf("byte") != -1
				|| elemType.indexOf("Byte") != -1) {
			try {
				field.set(form, Byte.valueOf(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (elemType.indexOf("char") != -1
				|| elemType.indexOf("Character") != -1) {
			try {
				field.set(form, Character.valueOf(value.charAt(0)));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (elemType.indexOf("double") != -1
				|| elemType.indexOf("Double") != -1) {
			try {
				field.set(form, Double.valueOf(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (elemType.indexOf("float") != -1
				|| elemType.indexOf("Float") != -1) {
			try {
				field.set(form, Float.valueOf(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (elemType.indexOf("int") != -1
				|| elemType.indexOf("Integer") != -1) {
			try {
				if (Tool.IsNullOrEmpty(value) || StringUtils.isBlank(value)) {
					field.set(form, 0);
				} else {
					field.set(form, Integer.valueOf(value));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (elemType.indexOf("long") != -1
				|| elemType.indexOf("Long") != -1) {
			try {
				field.set(form, Long.valueOf(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (elemType.indexOf("short") != -1
				|| elemType.indexOf("Short") != -1) {
			try {
				field.set(form, Short.valueOf(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			try {
				field.set(form, (Object) value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 该方法自动将数据库字段对应到Object中相应字段 要求：数据库与Object中字段名相同
	 */
	@Override
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		SqlGetter sqlgetter=new SqlGetter();
		Object nt = new Object();
		Field[] fields = className.getDeclaredFields();
		try {
			nt = className.newInstance();
			for (Field field : fields) {
				String column=sqlgetter.getFieldColumn(field);
				if(column==null){
					continue;
				}
				// 如果结果中没有改field项则跳过
				try {
					rs.findColumn(field.getName());
				} catch (Exception e) {
					continue;
				}
				// 修改相应filed的权限
				boolean accessFlag = field.isAccessible();
				field.setAccessible(true);
				String value = rs.getString(field.getName());
				value = value == null ? "" : value;
				setFieldValue(nt, field, value);

				// 恢复相应field的权限
				field.setAccessible(accessFlag);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nt;
	}

}
