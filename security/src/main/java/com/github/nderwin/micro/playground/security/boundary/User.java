package com.github.nderwin.micro.playground.security.boundary;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @XmlElement(nillable = false, required = true)
    public String username;

    @XmlElement(nillable = false, required = true)
    public String password;
    
}
