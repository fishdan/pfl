/*
 * Java domain class for entity "Phone" 
 * Created on 2022-01-17 ( Date ISO 2022-01-17 - Time 14:05:34 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
package com.fishdan.objects;

import java.io.Serializable;


/**
 * Domain class for entity "Phone"
 *
 * @author Telosys Tools Generator
 *
 */
public class Phone extends FishObject implements Personal {


    //----------------------------------------------------------------------
    // ENTITY PRIMARY KEY 
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // ENTITY DATA FIELDS 
    //----------------------------------------------------------------------    
    private Integer    id           ;
    private Integer    personid     ;
    private Integer    number       ;
    private String     type         ;

    //----------------------------------------------------------------------
    // ENTITY LINKS ( RELATIONSHIP )
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // CONSTRUCTOR(S)
    //----------------------------------------------------------------------
    public Phone() {
		super();
    }
    
    //----------------------------------------------------------------------
    // GETTER & SETTER FOR "KEY FIELD(S)"
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // GETTERS & SETTERS FOR "DATA FIELDS"
    //----------------------------------------------------------------------
    public void setId( Integer id ) {
        this.id = id ;
    }
    public Integer getId() {
        return this.id;
    }

    public void setPersonid( Integer personid ) {
        this.personid = personid ;
    }
    public Integer getPersonid() {
        return this.personid;
    }

    public void setNumber( Integer number ) {
        this.number = number ;
    }
    public Integer getNumber() {
        return this.number;
    }

    public void setType( String type ) {
        this.type = type ;
    }
    public String getType() {
        return this.type;
    }


    //----------------------------------------------------------------------
    // GETTERS & SETTERS FOR LINKS
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // toString METHOD
    //----------------------------------------------------------------------
    public String toString() { 
        StringBuilder sb = new StringBuilder(); 
        sb.append(id);
        sb.append("|");
        sb.append(personid);
        sb.append("|");
        sb.append(number);
        sb.append("|");
        sb.append(type);
        return sb.toString(); 
    } 

}
