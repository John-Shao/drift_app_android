package com.drift.foreamlib.boss.model;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ResCommon {
	private int Status;
	private String ErrMessage;
	
	public ResCommon(){
		
	}
	public ResCommon(Node XMLDevInfo){
		NodeList properties = XMLDevInfo.getChildNodes();  
        for (int j = 0; j < properties.getLength(); j++) {  
            Node property = properties.item(j);  
            String nodeName = property.getNodeName();  
            if (nodeName.equals("Status")) {  
            	Status = Integer.valueOf(property.getFirstChild().getNodeValue());
            }else if(nodeName.equals("ErrMessage")) {  
            	ErrMessage = property.getFirstChild().getNodeValue();
            }
        }  
	}
	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getErrMessage() {
		return ErrMessage;
	}

	public void setErrMessage(String errMessage) {
		ErrMessage = errMessage;
	}
}
