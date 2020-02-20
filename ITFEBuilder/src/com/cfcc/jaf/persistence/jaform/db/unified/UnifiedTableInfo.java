package com.cfcc.jaf.persistence.jaform.db.unified;

import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.info.ColumnInfo;
import com.cfcc.jaf.persistence.jaform.info.ITableInfo;
import com.cfcc.jaf.persistence.jdbc.core.IDataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnifiedTableInfo
  implements ITableInfo
{
  private static Log Log = LogFactory.getLog(UnifiedTableInfo.class);
  String catalog;
  String tabSchema;
  String tablename;
  String tableremarks;
  List columns = new ArrayList();
  private IDataSource dataSource;
  DatabaseMetaData metaData;
  public static Map map = new HashMap();

  public void readInfoFromDB(List generated)
    throws JAFDatabaseException
  {
    Connection conn = null;
    try {
      conn = this.dataSource.getConnection();
      this.metaData = conn.getMetaData();
      ResultSet rs = this.metaData.getColumns(this.catalog, this.tabSchema, this.tablename, null);

      processColumns(rs);
      rs = this.metaData.getPrimaryKeys(this.catalog, this.tabSchema, this.tablename);
      processPrimarykeys(rs);
      rs = this.metaData.getVersionColumns(this.catalog, this.tabSchema, this.tablename);
      processVersionColumns(rs);
      processGeneratedColumns(generated);
    }
    catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public List getColumns()
  {
    return this.columns;
  }

  public String getTablename()
  {
    return this.tablename;
  }

  public String getTableremarks()
  {
    return this.tableremarks;
  }

  public String getDtoFieldType(String DBFieldTye)
  {
    String str = (String)map.get(DBFieldTye);
    if (str == null) {
      Log.error("Î´ÕÒµ½ÀàÐÍ£º" + DBFieldTye);
    }
    return str;
  }

  public void setTablename(String tableName)
  {
    this.tablename = tableName;
  }

  public void setTableremarks(String tableRemarks)
  {
    this.tableremarks = tableRemarks;
  }

  public String toString()
  {
    return "TableName:" + this.tablename + "    Remarks:" + this.tableremarks;
  }

  public void setDataSource(IDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void setMetaData(DatabaseMetaData metaData) {
    this.metaData = metaData;
  }

  public void setCatalog(String catalog) {
    this.catalog = catalog;
  }

  public void setTabSchema(String tabSchema) {
    this.tabSchema = tabSchema;
  }

  protected void processColumns(ResultSet rs)
  {
    try
    {
      while (rs.next()) {
        ColumnInfo ci = new ColumnInfo();
        ci.setName(rs.getString("COLUMN_NAME"));
        ci.setType(rs.getString("TYPE_NAME"));
        ci.setWidth(rs.getInt("COLUMN_SIZE"));
        System.out.println(rs.getString("COLUMN_NAME")+"||"+rs.getString("TYPE_NAME")+"||"+rs.getInt("COLUMN_SIZE")+"||"+rs.getInt("DECIMAL_DIGITS"));
        if ("NUMBER".equals(ci.getType()))  {
        	if (rs.getInt("DECIMAL_DIGITS")>0){
        	    ci.setType("NUMBER20");
        	}else if(ci.getWidth()<=21){
        		ci.setType("NUMBER21");
        	}else if(ci.getWidth()>21){
        		ci.setType("NUMBER30");
        	}
		}
        ci.setBNULL("YES".equals(rs.getString("IS_NULLABLE")));
        ci.setRemarks(rs.getString("REMARKS"));
        ci.setBPK(false);
        ci.setGenerated(false);
        ci.setIdentity(false);
        this.columns.add(ci);
      }

    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  void processPrimarykeys(ResultSet rs)
  {
    try
    {
      Set set = new HashSet();
      while (rs.next()) {
        set.add(rs.getString("COLUMN_NAME"));
      }
      for (int i = 0; i < this.columns.size(); i++) {
        ColumnInfo ci = (ColumnInfo)this.columns.get(i);
        if (set.contains(ci.getName()))
          ci.setBPK(true);
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  void processVersionColumns(ResultSet rs)
  {
    try
    {
      Set set = new HashSet();
      while (rs.next()) {
        int version = rs.getShort("PSEUDO_COLUMN");
        if (version == 2) {
          set.add(rs.getString("COLUMN_NAME"));
        }
      }
      for (int i = 0; i < this.columns.size(); i++) {
        ColumnInfo ci = (ColumnInfo)this.columns.get(i);
        if (set.contains(ci.getName()))
          ci.setGenerated(true);
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  void processGeneratedColumns(List generated) {
    if (generated == null)
      return;
    for (int i = 0; i < this.columns.size(); i++) {
      ColumnInfo ci = (ColumnInfo)this.columns.get(i);
      if (generated.contains(this.tablename + "." + ci.getName()))
        ci.setGenerated(true);
    }
  }

  static
  {
    map.put("BIGINT", "Long");
    map.put("CHARACTER", "String");
    map.put("CHAR", "String");
    map.put("DATE", "Date");
    map.put("DECIMAL", "BigDecimal");
    map.put("DOUBLE", "BigDecimal");
    map.put("INTEGER", "Integer");
    map.put("LONG VARCHAR", "String");
    map.put("REAL", "BigDecimal");
    map.put("SMALLINT", "Short");
    map.put("TIME", "Time");
    map.put("TIMESTAMP", "Timestamp");
    map.put("VARCHAR", "String");
    map.put("BLOB", "byte[]");
    map.put("CLOB", "Clob");
    map.put("VARCHAR2", "String");
    map.put("NUMBER21", "Long");
    map.put("NUMBER30", "Integer");
    map.put("NUMBER20", "BigDecimal");
    map.put("TIMESTAMP(6)", "Timestamp");

    map.put("INT", "Integer");
    map.put("INT UNSIGNED", "Integer");
  }
}