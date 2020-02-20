/**
 * NoticeService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cfcc.itfe.webservice.jaxws;

public interface NoticeService extends javax.xml.rpc.Service {
    public java.lang.String getNoticePortAddress();

    public com.cfcc.itfe.webservice.jaxws.NoticeDelegate getNoticePort() throws javax.xml.rpc.ServiceException;

    public com.cfcc.itfe.webservice.jaxws.NoticeDelegate getNoticePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
