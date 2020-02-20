package com.cfcc.itfe.webservice.jaxws;

public class NoticeDelegateProxy implements com.cfcc.itfe.webservice.jaxws.NoticeDelegate {
  private String _endpoint = null;
  private com.cfcc.itfe.webservice.jaxws.NoticeDelegate noticeDelegate = null;
  
  public NoticeDelegateProxy() {
    _initNoticeDelegateProxy();
  }
  
  public NoticeDelegateProxy(String endpoint) {
    _endpoint = endpoint;
    _initNoticeDelegateProxy();
  }
  
  private void _initNoticeDelegateProxy() {
    try {
      noticeDelegate = (new com.cfcc.itfe.webservice.jaxws.NoticeServiceLocator()).getNoticePort();
      if (noticeDelegate != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)noticeDelegate)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)noticeDelegate)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (noticeDelegate != null)
      ((javax.xml.rpc.Stub)noticeDelegate)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.cfcc.itfe.webservice.jaxws.NoticeDelegate getNoticeDelegate() {
    if (noticeDelegate == null)
      _initNoticeDelegateProxy();
    return noticeDelegate;
  }
  
  public void readReportNotice(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, java.lang.String[] arg4) throws java.rmi.RemoteException, com.cfcc.itfe.webservice.jaxws.Exception{
    if (noticeDelegate == null)
      _initNoticeDelegateProxy();
    noticeDelegate.readReportNotice(arg0, arg1, arg2, arg3, arg4);
  }
  
  
}