
package uk.ac.liv.scm.trac;


import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import redstone.xmlrpc.XmlRpcArray;

public interface ticket {
    
    XmlRpcArray query(); // qstr="status!=closed"
    XmlRpcArray query(String qstr);
    
    Integer delete(Integer id);
    
    Integer create( String summary, String description);
    Integer create( String summary, String description, Hashtable attribute);
    Integer create( String summary, String description, Hashtable attribute, Boolean notify);
    
    XmlRpcArray get(Integer id);
  
    XmlRpcArray update(Integer id, String comment);
    XmlRpcArray update(Integer id, String comment, Hashtable attributes);
    XmlRpcArray update(Integer id, String comment, Hashtable attributes, Boolean notify);
    
    Hashtable changeLog(Integer id);
    Hashtable changeLog(Integer id, Integer when);
    
    XmlRpcArray listAttachments(Integer ticket);
    
    byte[] getAttachment(Integer ticket, String filename);
    
    String putAttachment(Integer ticket, String filename, String description, byte[] data);
    String putAttachment(Integer ticket, String filename, String description, byte[] data, Boolean replace);
    
    Boolean deleteAttachment(Integer ticket, String filename);
    
    Vector<HashMap> getTicketFields();
}

