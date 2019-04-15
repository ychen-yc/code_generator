package com.qxs.database.dialect.sqlserver;

import org.springframework.stereotype.Service;

import com.qxs.database.dialect.AbstractDialect;

/**
 * SQLServer方言
 * 
 * @author <a href="mailto:wuzhiqiang@novacloud.com">wuzq</a>
 * @date 2012-7-18下午01:27:44
 * @version Revision: 1.0
 */
@Service("microsoftsqlserverDialect")
public class SqlServerDialect extends AbstractDialect {
	/**
	 * 获取所有的表名sql文件名
	 * **/
	private static final String SQL_ALL_TABLE_NAMES_FILE_NAME = "all_table_names.sql";
	/**
	 * 获取表信息sql文件名
	 * **/
	private static final String SQL_TABLE_FILE_NAME = "table.sql";
	/**
	 * 获取列信息sql文件名
	 * **/
	private static final String SQL_COLUMNS_FILE_NAME = "columns.sql";
	/**
	 * 获取所有的表名sql
	 * **/
	private static final String SQL_ALL_TABLE_NAMES;
	/**
	 * 获取表信息sql
	 * **/
	private static final String SQL_TABLE;
	/**
	 * 获取列信息sql
	 * **/
	private static final String SQL_COLUMNS;
	
	static {
		SQL_ALL_TABLE_NAMES = read(SqlServerDialect.class,SQL_ALL_TABLE_NAMES_FILE_NAME);
		SQL_TABLE = read(SqlServerDialect.class,SQL_TABLE_FILE_NAME);
		SQL_COLUMNS = read(SqlServerDialect.class,SQL_COLUMNS_FILE_NAME);
	}
	public SqlServerDialect() {
		super();
	}

	@Override
	protected String getAllTableNamesSql() {
		return SQL_ALL_TABLE_NAMES;
	}

	@Override
	protected String getTableSql() {
		return SQL_TABLE;
	}

	@Override
	protected String getColumnsSql() {
		return SQL_COLUMNS;
	}
	@Override
	public String extractDatabaseName(String url) {
		
		return null;
	}
	
}
