/* kXML
 *
 * The contents of this file are subject to the Enhydra Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License
 * on the Enhydra web site ( http://www.enhydra.org/ ).
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License.
 *
 * The Initial Developer of kXML is Stefan Haustein. Copyright (C)
 * 2000, 2001 Stefan Haustein, D-46045 Oberhausen (Rhld.),
 * Germany. All Rights Reserved.
 *
 * Contributor(s): Paul Palaszewski, Wilhelm Fitzpatrick, 
 *                 Eric Foster-Johnson
 *
 * */

package org.kxml2.kdom;

import java.io.*;
import java.util.*;

import org.xmlpull.v1.*;
import org.xmlpull.v1.serializer.*;

/** In order to create an element, please use the createElement method
    instead of invoking the constructor directly. The right place to
    add user defined initialization code is the init method. */

public class Element extends Node {

    protected String namespace;
    protected String name;
    protected Vector attributes;
    protected Node parent;
    protected Vector namespaces;

    public Element() {
    }

    /** called when all properties are set, but before children
        are parsed. Please do not use setParent for initialization
        code any longer. */

    public void init() {
    }


	public void addAttribute (String namespace, String prefix, String name, String value) {
		if (attributes == null) attributes = new Vector ();
		attributes.addElement 
			(new String [] {namespace, prefix, name, value});
	}


    /** removes all children and attributes */

    public void clear() {
        attributes = null;
        children = new Vector ();
    }

    /** Forwards creation request to parent if any, otherwise
    calls super.createElement. Please note: For no
        namespace, please use Xml.NO_NAMESPACE, null is not a legal
        value. Currently, null is converted to Xml.NO_NAMESPACE, but
        future versions may throw an exception. */

    public Element createElement(
        String namespace,
        String name) {

        return (this.parent == null)
            ? super.createElement(namespace, name)
            : this.parent.createElement(namespace, name);
    }

    /** Returns the number of attributes of this element. */

    public int getAttributeCount() {
        return attributes == null ? 0 : attributes.size ();
    }

	public String getAttributeNamespace (int index) {
		return ((String []) attributes.elementAt (index)) [0];
	}

	public String getAttributePrefix (int index) {
		return ((String []) attributes.elementAt (index)) [1];
	}
	
	public String getAttributeName (int index) {
		return ((String []) attributes.elementAt (index)) [2];
	}
	

	public String getAttributeValue (int index) {
		return ((String []) attributes.elementAt (index)) [3];
	}
	

    /** Returns the document this element is a member of. The document
    is determined by ascending to the parent of the root element.
    If the element is not contained in a document, null is
    returned. */

    public Document getDocument() {

        if (parent instanceof Document)
            return (Document) parent;

        if (parent instanceof Element)
            return ((Element) parent).getDocument();

        return null;
    }

    /** returns the (local) name of the element */

    public String getName() {
        return name;
    }

    /** returns the namespace of the element */

    public String getNamespace() {
        return namespace;
    }

    /** Returns the parent node of this element */

    public Node getParent() {
        return parent;
    }

    /** Returns the parent element if available, null otherwise */

    public Element getParentElement() {
        return (parent instanceof Element)
            ? ((Element) parent)
            : null;
    }


    /** Builds the child elements from the given Parser. By overwriting parse, 
    an element can take complete control over parsing its subtree. */

    public void parse(XmlPullParser parser)
        throws IOException, XmlPullParserException {

        name = parser.getName();
        namespace = parser.getNamespace();
        for (int i = 0; i < parser.getAttributeCount (); i++) 
	        addAttribute (parser.getAttributeNamespace (i),
	        			  parser.getAttributePrefix (i),
	        			  parser.getAttributeName (i),
	        			  parser.getAttributeValue (i));


        //        if (prefixMap == null) throw new RuntimeException ("!!");

        init();


		if (parser.isEmptyElementTag()) 
			parser.nextToken ();
		else {
			parser.nextToken ();
	        super.parse(parser);

        	if (getChildCount() == 0)
            	addChild(IGNORABLE_WHITESPACE, "");
		}
		
        parser.require(
            parser.END_TAG,
            getNamespace(),
            getName());
            
        parser.nextToken ();
    }

    /** Removes the attribute at the given index */

    public void removeAttribute(int index) {
        attributes.removeElementAt(index);
    }


    /** sets the name of the element */

    public void setName(String name) {
        this.name = name;
    }

    /** sets the namespace of the element. Please note: For no
        namespace, please use Xml.NO_NAMESPACE, null is not a legal
        value. Currently, null is converted to Xml.NO_NAMESPACE, but
        future versions may throw an exception. */

    public void setNamespace(String namespace) {
        if (namespace == null) 
        	throw new NullPointerException ("Use \"\" for empty namespace");
        this.namespace = namespace;
    }

    /** Sets the Parent of this element. Automatically called from the
    add method.  Please use with care, you can simply
    create inconsitencies in the document tree structure using
    this method!  */

    protected void setParent(Node parent) {
        this.parent = parent;
    }


    /** Writes this element and all children to the given XmlWriter. */

    public void write(XmlSerializer writer)
        throws IOException {

        writer.startTag(
            getNamespace(),
            getName());

        int len = getAttributeCount();

        for (int i = 0; i < len; i++) {
            writer.attribute(
                getAttributeNamespace(i),
                getAttributeName(i),
                getAttributeValue(i));
        }

        writeChildren(writer);

        writer.endTag(getNamespace (), getName ());
    }
}