package com.cfcc.itfe.config;

public interface ITFEGlobalBeanId {

	public static final String HTTP_CONFIG = "HttpConfig.ITFE.ID";

	public static final String DATABASE_FACADE_DB_PREFIX = "Database.Facade.Db.";

	public static final String COMMON_FACADE_DB_PREFIX = "Common.Facade.Db.";
	
	public static final String GZIPUTILBEAN_TAS_ID = "GZIPUTILBEAN.TAS.ID";
	/**
	 * 新建事务的模板类，通过该模板类，可以在需要的时候新建一个事务<br>
	 * 在方法结束时将事务中的操作提交<br>
	 * 本模板类会新建一个事务，将所包含的操作放置在新建的事务中进行管理
	 */
	public static final String NEW_TRANSACTION_TEMPLATE = "NewTransactionTemplate";
	/**
	 * 使用已有事务的模板类，通过该模板类，可以在已有事务的时候使用已有事务，在没有事务的时候会新建一个事务<br>
	 * 在方法结束时将事务中的操作提交<br>
	 * 本模板类
     */
	public static final String REQUEIRED_TRANSACTION_TEMPLATE = "RequiredTransactionTemplate";
	public static final String FILE_UTIL = "FileUtil.ITFE.ID";
	public static final String ITFE_CONFIG = "ITFE.CONFIG";
	public static final String EDITION_UTIL ="EditionUtil.ITFE.ID";
	public static final String AREA_UTIL ="AreaMode";
	public static final String APPLICATION_PATH ="ApplicationPath";
	
}
