/**
 * NoticeServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cfcc.itfe.webservice.jaxws;

import com.cfcc.itfe.config.ITFECommonConstant;

public class NoticeServiceLocator extends org.apache.axis.client.Service implements com.cfcc.itfe.webservice.jaxws.NoticeService {
    public NoticeServiceLocator() {
    }


    public NoticeServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public NoticeServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for NoticePort
    private java.lang.String NoticePort_address = ITFECommonConstant.FINSERVICE_URL;

    public java.lang.String getNoticePortAddress() {
        return NoticePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String NoticePortWSDDServiceName = "NoticePort";

    public java.lang.String getNoticePortWSDDServiceName() {
        return NoticePortWSDDServiceName;
    }

    public void setNoticePortWSDDServiceName(java.lang.String name) {
        NoticePortWSDDServiceName = name;
    }

    public com.cfcc.itfe.webservice.jaxws.NoticeDelegate getNoticePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(NoticePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getNoticePort(endpoint);
    }

    public com.cfcc.itfe.webservice.jaxws.NoticeDelegate getNoticePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cfcc.itfe.webservice.jaxws.NoticePortBindingStub _stub = new com.cfcc.itfe.webservice.jaxws.NoticePortBindingStub(portAddress, this);
            _stub.setPortName(getNoticePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setNoticePortEndpointAddress(java.lang.String address) {
        NoticePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.cfcc.itfe.webservice.jaxws.NoticeDelegate.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cfcc.itfe.webservice.jaxws.NoticePortBindingStub _stub = new com.cfcc.itfe.webservice.jaxws.NoticePortBindingStub(new java.net.URL(NoticePort_address), this);
                _stub.setPortName(getNoticePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("NoticePort".equals(inputPortName)) {
            return getNoticePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://jaxws.hmw.com/", "NoticeService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://jaxws.hmw.com/", "NoticePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("NoticePort".equals(portName)) {
            setNoticePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
