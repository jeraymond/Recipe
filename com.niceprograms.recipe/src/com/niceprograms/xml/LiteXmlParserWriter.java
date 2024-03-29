package com.niceprograms.xml;

/*
 This module is free software; you can redistribute it and/or
 modify it under the terms of the GNU Library General Public
 License as publised by the Free Software Foundation; either
 version 2 of the License, or (at your option) any later version.

 This module is distributed WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A
 PARTICULAR PURPOSE.  See the GNU Library General Public License
 for more details.

 You should have received a copy of the GNU Library General Public
 License along with this library; if not, write to the Free
 Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 MA 02111-1307, USA

 Should you need to contact the author, you can do so by email to
 <wsh@sprintmail.com>.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Stack;
import java.util.Vector;

/**
 * <p>
 * Title: XML parser/generator
 * </p>
 * <b>
 * <p>
 * Copyright: Copyright (c) 2003<br>
 * License: GPL<br>
 * Summary:This is a poor man's version of a DOM like XML reader/writer written
 * specifically so that it is very small and ideal for an applet.
 * </p>
 * </b>
 * <p>
 * The format supported is as followed:
 * </p>
 * <p>
 * &lt;?xml version=&quot;1.0&quot; standalone=&quot;yes&quot;?&gt;<br>
 * &lt;!-- Comment --&gt;<br>
 * &lt;rootname&gt;<br>
 * &nbsp;&nbsp;&lt;tag&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;tag attribute1=&quot;value&quot;
 * attribute2=&quot;value&quot;&gt;elementvalue&lt;/tag&gt;<br>
 * &nbsp;&nbsp;&lt;/tag&gt;<br>
 * &lt;/rootname&gt;
 * </p>
 * <p>
 * The following are not supported:
 * </p>
 * <ul>
 * <li> Processing Instructions
 * <li> Namespace
 * </ul>
 * <hr>
 * <p>
 * How to create XML tree in memory to write to disk:
 * </p>
 * <ol>
 * <li>Create the root node with a name (LiteXmlParserWriter root = new
 * LiteXmlParserWriter(&quot;roottag&quot;);)
 * <li>Create subelements
 * <ol>
 * <li>If subelement will have subelements (branch), use the form without a
 * value (LiteXmlParserWriter subelement =
 * roottag.addElement(&quot;subelement&quot;);)
 * <li>If the subelement will not have subelements (leaf), add the element with
 * a value (LiteXmlParserWriter subelement =
 * roottag.addElement(&quot;element&quot;,value);)
 * <li>For each subelement (branch and leaf) add attributes
 * (subelement.Attribute.add(&quot;name&quot;,value);) or
 * (root.Attribute.add(&quot;name&quot;,value);)
 * </ol>
 * <li>Continue this until you have completed the tree
 * <li>Write it to a PrintWriter stream to generate XML
 * </ol>
 * <p>
 * Example:
 * </p>
 * <ul>
 * <li> LiteXmlParserWriter root = new LiteXmlParserWriter("root");
 * <li> LiteXmlParserWriter sub1 = root.addElement("element1",value1);
 * <li> sub1.Attribute.add("attribute1",att1);
 * <li> sub1.Attribute.add("attribute2",att2);
 * <li> LiteXmlParserWriter sub2 = root.addElement("element2");
 * <li> sub2.Attribute.add("attribute20",att20);
 * <li> LiteXmlParserWriter sub21 = sub1.addElement("element21");
 * <li> sub21.Attribute.add("attribute21",value21);
 * <li> root.serialize(pw);
 * </ul>
 * <p>
 * Results in the following:
 * </p>
 * <p>
 * &lt;?xml version=&quot;1.0&quot; standalone=&quot;yes&quot;?&gt;<br>
 * &lt;root&gt;<br>
 * &nbsp;&nbsp;&lt;element1 attribute1=&quot;att1&quot;
 * attribute2=&quot;att2&quot;&gt;value1&lt;/element1&gt;<br>
 * &nbsp;&nbsp;&lt;element2 attribute20=&quot;att20&quot;&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;element21
 * attribute21=&quot;att21&quot;&gt;value21&lt;/element21&gt;<br>
 * &nbsp;&nbsp;&lt;/element2&gt;<br>
 * &lt;/root&gt;
 * </p>
 * <p>
 * Values for Elements and Attributes can be any type of object but will be
 * converted to String by calling toString() before storing the value.
 * </p>
 * <hr>
 * <p>
 * How to load XML tree from disk:
 * </p>
 * <ol>
 * <li> Open a BufferedReader (in = new BufferedReader(file);)
 * <li> Create a new LiteXmlParserWriter(BufferedReader) object passing it the
 * BufferedReader (xmlroot = new LiteXmlParserWriter((BufferedReader)in);)
 * </ol>
 * <hr>
 * <p>
 * How to walk through the XML tree:
 * </p>
 * <ul>
 * <li> LiteXmlParserWriter.getTag(): Returns the tag name of the
 * LiteXmlParserWriter object
 * <li> LiteXmlParserWriter.getValue(): Returns the value of the
 * LiteXmlParserWriter object
 * <li> LiteXmlParserWriter.getElement(int): Returns the LiteXmlParserWriter
 * sub-object at the specified index
 * <li> LiteXmlParserWriter.findElement(String tag): Returns the first
 * LiteXmlParserWriter object with the name tag
 * <li> LiteXmlParserWriter.findElement(String tag, Attribute name): Returns the
 * first LiteXmlParserWriter object with the name tag and attribute name
 * <li> LiteXmlParserWriter.Attribute.find(String name): Returns the attribute
 * value associated with name
 * </ul>
 * 
 * @author Scott Hiles
 * @version 1.1.0
 */

public class LiteXmlParserWriter implements Serializable {
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	private final boolean debug = false;

	private final String XMLheader = "<?xml version=\"1.0\" standalone=\"yes\"?>";

	private final String XMLcomment = ""; // "<!-- Written by WiSH XML writer

	// -->";

	private String tag;

	private transient Object element = null; // either an Object or a

	// Vector()

	/** The attribute */
	public transient Attribute Attribute = new Attribute();

	/**
	 * Create new LiteXmlParserWriter object with no subelements, no attributes,
	 * and no value
	 * 
	 * @param tag initial name for the XML element or null for empty element
	 */
	public LiteXmlParserWriter(String tag) {
		this.tag = tag;
		if (debug) {
			System.out.println("LiteXmlParserWriter(" + tag + ")");
		}
	}

	/** Default constructor */
	public LiteXmlParserWriter() {
		super();
	}

	/**
	 * Retrieve the name/tag
	 * 
	 * @return <b>null</b> if no name set<br>
	 * <b>String object</b>: for the text of the XML element
	 */
	public String getTag() {
		return this.tag;
	}

	/**
	 * Retrieve the data value for this element in String form
	 * 
	 * @return <b>null</b> if no value set<br>
	 * <b>String object</b>: for the text of the XML element
	 */
	public String getValue() {
		if (element == null) {
			return null;
		}
		if (this.element.getClass() == Vector.class) {
			return null;
		}
		return (String) this.element;
	}

	/**
	 * Retrieve the number of elements attached to this element
	 * 
	 * @return int value
	 */
	public int size() {
		if (element == null) {
			return 0;
		}
		if (element.getClass() == Vector.class) {
			return ((Vector) element).size();
		}
		return 0;
	}

	/**
	 * Find and retrieve the first element that has the specified tag name The
	 * search starts with this object and follows all branch and leaf nodes in
	 * the XML tree.
	 * 
	 * @param theTag the element to find.
	 * @return <b>null</b>: no object with the specified tag found<br>
	 * <b>LiteXmlParserWriter object</b>: First object found with the matching
	 * tag
	 */
	public LiteXmlParserWriter findElement(String theTag) {
		if (this.tag.compareTo(theTag) == 0) {
			return this;
		}
		if (element == null) {
			return null;
		}
		if (element.getClass() != Vector.class) {
			return null;
		}

		LiteXmlParserWriter xml = null;
		Vector v = (Vector) this.element;
		int i;
		for (i = 0; i < v.size(); i++) {
			xml = (LiteXmlParserWriter) v.get(i);
			if (xml == null) {
				continue;
			}
			if (xml.findElement(theTag) != null) {
				return xml;
			}
		}
		return null;
	}

	/**
	 * Remove subelement (element) from list of objects for a parent element
	 * 
	 * @param someElement element to be removed from object list of parent
	 * @return boolean value set to true if the element was found and removed
	 * from the parent
	 * @throws LiteXmlParserWriterException if an error occurs removing the
	 * element.
	 */
	public boolean removeElement(LiteXmlParserWriter someElement)
			throws LiteXmlParserWriterException {
		if (someElement == null) {
			throw new LiteXmlParserWriterException(
					"Cannot prune if target element is null");
		}
		if (this.element == null) {
			throw new LiteXmlParserWriterException("No subelements");
		}
		if (this.element.getClass() != Vector.class) {
			throw new LiteXmlParserWriterException("No subelements");
		}

		Vector v = (Vector) this.element;
		if (v.size() <= 0) {
			return false;
		}
		if (!v.remove(someElement)) {
			return false;
		}
		if (v.size() == 0) {// go ahead and cl
			this.element = null;
		}
		return true;
	}

	/**
	 * Tests if the specified element is a subelement of this element
	 * 
	 * @param someElement element to test
	 * @return boolean value set to true if element is found to be subelement
	 * @throws LiteXmlParserWriterException if an error occurs checking if the
	 * element is contained.
	 */
	public boolean contains(LiteXmlParserWriter someElement)
			throws LiteXmlParserWriterException {
		if (someElement == null) {
			throw new LiteXmlParserWriterException(
					"Cannot prune if target element is null");
		}
		if (this.element == null) {
			throw new LiteXmlParserWriterException("No subelements");
		}
		if (this.element.getClass() != Vector.class) {
			throw new LiteXmlParserWriterException("No subelements");
		}

		Vector v = (Vector) this.element;
		if (v.size() <= 0) {
			return false;
		}
		return v.contains(someElement);
	}

	/**
	 * Removes all subelements from the specified parent
	 * 
	 * @return boolean value set to true if all subelements removed
	 */
	public boolean removeAllElements() {
		if (this.element == null) {
			return false;
		}
		if (this.element.getClass() != Vector.class) {
			return false;
		}
		element = null;
		return true;
	}

	/**
	 * Tests if this parent has no subelements
	 * 
	 * @return boolean value set to true if size is zero
	 */
	public boolean isEmpty() {
		return (this.element == null);
	}

	/**
	 * Find and retrieve the first element that has the specified attribute
	 * name. The search starts with this object and follows all branch and leaf
	 * nodes in the XML tree.
	 * 
	 * @param name the name of the attribute to find.
	 * @return <b>null</b>: No element found with the specified attribute name<br>
	 * <b>LiteXmlParserWriter object</b>: First object found with the matching
	 * attribute name
	 */
	public LiteXmlParserWriter findAttribute(String name) {
		if (Attribute.find(name) != null) {
			return this;
		}
		if (element == null) {
			return null;
		}
		if (element.getClass() != Vector.class) {
			return null;
		}

		LiteXmlParserWriter xml = null;
		Vector v = (Vector) element;
		int i;
		for (i = 0; i < v.size(); i++) {
			xml = (LiteXmlParserWriter) v.get(i);
			if (xml.findAttribute(name) != null) {
				return xml;
			}
		}
		return null;
	}

	/**
	 * Find and retrieve the first element that has the specified tag name and
	 * the specified attribute name. The search starts with this object and
	 * follows all branch and leaf nodes in the XML tree.
	 * 
	 * @param theTag the tag to find.
	 * @param attname the attribute name to find.
	 * @return <b>null</b>: No element found with the specified tag name and
	 * attribute name<br>
	 * <b>LiteXmlParserWriter object</b>: First object found matching both the
	 * tag name and attribute name
	 */
	public LiteXmlParserWriter findElement(String theTag, String attname) {
		if (this.tag.compareTo(theTag) == 0
				&& this.Attribute.find(attname) != null) {
			return this;
		}
		if (element == null) {
			return null;
		}
		if (element.getClass() != Vector.class) {
			return null;
		}

		LiteXmlParserWriter xml = null;
		Vector v = (Vector) element;
		int i;
		for (i = 0; i < v.size(); i++) {
			xml = (LiteXmlParserWriter) v.get(i);
			if (xml.findElement(theTag) != null
					&& xml.Attribute.find(attname) != null) {
				return xml;
			}
		}
		return null;
	}

	/**
	 * Retrieve the element object at the specified <b>index</b>. The branche
	 * and leaf nodes in the XML tree are stored in a Vector and each index
	 * represents a node in the tree. Indicies start at 0 and count up to the
	 * length of the Vector.
	 * 
	 * @param index integer indicating the branch or leaf of the this XML object
	 * @return <b>null</b>: if index is out of bounds<br>
	 * <b>LiteXmlParserWriter object</b>: element at the specified index.
	 */
	public LiteXmlParserWriter getElement(int index) {
		if (element == null) {
			return null;
		}
		if (element.getClass() != Vector.class) {
			return null;
		}

		Vector v = (Vector) element;
		if (index > v.size()) {
			return null;
		}
		if (index < 0) {
			return null;
		}
		return (LiteXmlParserWriter) v.get(index);
	}

	/**
	 * Set the tag for this element (private)
	 * 
	 * @param tag String to use for tag name for element
	 */
	private void setTag(String tag) {
		this.tag = tag;
		if (debug) {
			System.out.println("LiteXmlParserWriter.setTag(" + tag + ")");
		}
	}

	/**
	 * Set the value for this element (private)
	 * 
	 * @param value String to use for value for the element
	 */
	private void setValue(String value) throws LiteXmlParserWriterException {
		if (element != null) {
			if (element.getClass() == Vector.class) {
				throw new LiteXmlParserWriterException(
						"Cannot assign a value to an element with subelements");
			}
		}
		this.element = value;
		if (debug) {
			System.out.println("LiteXmlParserWriter.setValue(" + value + ")");
		}
	}

	/**
	 * Add a subelement which will be used as a branch in the XML tree to the
	 * this element of the form:<br>
	 * <br>
	 * &lt;tag attr1=&quot;attr1value attr2=&quot;attr2value&quot; ... &gt;<br>
	 * ...<br>
	 * &lt;/tag&gt;
	 * 
	 * @param theTag String used to identify the element
	 * @return <b>LiteXmlParserWriter object</b>: the newly created element
	 * @throws LiteXmlParserWriterException if an error occurs adding the
	 * element.
	 */
	@SuppressWarnings("unchecked")
	public LiteXmlParserWriter addElement(String theTag)
			throws LiteXmlParserWriterException {
		Vector<Object> v = null;

		if (element != null) {
			if (element.getClass() != Vector.class) {
				throw new LiteXmlParserWriterException(
						"Cannot create a subelement to an element with a value");
			}
		}
		if (element == null) {
			v = new Vector<Object>(); // create the new subelement
			element = v;
		} else {
			v = (Vector) element;
		}

		LiteXmlParserWriter xml = new LiteXmlParserWriter(theTag);
		v.add(xml);
		if (debug) {
			System.out
					.println("LiteXmlParserWriter.addElement(" + theTag + ")");
		}
		if (debug) {
			System.out.println("elements.size() = " + v.size());
		}
		return xml;
	}

	/**
	 * Add a leaf element to the XML tree of the form:<br>
	 * <br>
	 * &lt;tag attr1=&quot;attr1value attr2=&quot;attr2value&quot; ...
	 * &gt;value&lt;/tag&gt;
	 * 
	 * @param theTag String used to identify the element
	 * @param value Object to use for the element value (Note that the object
	 * will be converted to a String before storing in the XML tree.
	 * @return <b>LiteXmlParserWriter object</b>: the newly created element
	 * @throws LiteXmlParserWriterException if an error occurs adding the
	 * element.
	 */
	@SuppressWarnings("unchecked")
	public LiteXmlParserWriter addElement(String theTag, Object value)
			throws LiteXmlParserWriterException {
		Vector<LiteXmlParserWriter> v = null;

		if (element != null) {
			if (element.getClass() != Vector.class) {
				throw new LiteXmlParserWriterException(
						"Cannot create a subelement to an element with a value");
			}
		}
		if (element == null) {
			v = new Vector<LiteXmlParserWriter>();
			element = v;
		} else {
			v = (Vector) element;
		}

		LiteXmlParserWriter e = new LiteXmlParserWriter(theTag);
		e.setValue(value.toString());
		v.add(e);
		if (debug) {
			System.out.println("LiteXmlParserWriter.addElement(" + theTag + ","
					+ value + ")");
		}
		if (debug) {
			System.out.println("elements.size() = " + v.size());
		}
		return e;
	}

	// -----------------------------------------------------------------------------
	/**
	 * Attributes associated with element tags. Multiple attributes can be added
	 * to any element tag and will be stored between the &lt; and &gt;.
	 * Attributes are written to the XML tree in the form:<br>
	 * <br>
	 * &lt;tag attr1=&quot;attr1value attr2=&quot;attr2value&quot; ...
	 * &gt;value&lt;/tag&gt;
	 */
	public class Attribute {
		private Vector<Object> attributes = new Vector<Object>();

		/**
		 * Add a named attribute to this element
		 * 
		 * @param name String identifying the name of the attribute
		 * @param value Object identifying the data to be associated with the
		 * attribute
		 */
		public void add(String name, Object value) {
			AttributeInternal a = new AttributeInternal(name, value.toString());
			attributes.add(a);
			if (debug) {
				System.out.println("LiteXmlParserWriter.Attribute.add(" + name
						+ "," + value + ")");
			}
		}

		private String dumpattributes() {
			int i;
			String result = new String("");
			for (i = 0; i < attributes.size(); i++) {
				AttributeInternal o = (AttributeInternal) attributes.get(i);
				result = new String(result + " " + o.name + "=\""
						+ cleanup(o.value) + "\"");
			}
			return result;
		}

		/**
		 * Retrieve the value associated with the attribute identified
		 * 
		 * @param name The name of the attribute to find
		 * @return <b>String object</b>: String form of the value associated
		 * with the attribute
		 */
		public String find(String name) {
			int i;
			if (attributes.size() == 0) {
				return null;
			}
			for (i = 0; i < attributes.size(); i++) {
				AttributeInternal a = (AttributeInternal) attributes.get(i);
				if (a.name == null) {
					continue;
				} else if (a.name.compareTo(name) == 0) {
					return a.value;
				}
			}
			return null;
		}

		private class AttributeInternal {
			/** The attribute name. */
			public String name = null;

			/** The attribute value. */
			public String value = null;

			/**
			 * @param name
			 * @param value
			 */
			AttributeInternal(String name, String value) {
				this.name = name;
				this.value = value;
			}
		}
	}

	// -----------------------------------------------------------------------------
	// XML writer (serializer)
	// -----------------------------------------------------------------------------
	// Simply recurse through the tree printing out the tags, attributes, and
	// subelements as you go. Every node in the tree is an instance of
	// LiteXmlParserWriter
	// so recursion is fairly trivial.
	//
	// This is all original code, so don't stone me if I have a mistake.
	// -----------------------------------------------------------------------------
	/**
	 * Write the XML tree to an output stream in text form.
	 * 
	 * @param out A PrintWriter object to be used as the output stream for the
	 * XML tree.
	 * @throws IOException if an error occurs serializing to the output.
	 */
	@SuppressWarnings("unused")
	public void serialize(PrintWriter out) throws IOException {
		out.println(XMLheader);
		out.println(XMLcomment);
		dumpElements("", out);
	}

	private void dumpElements(String indent, PrintWriter out) {
		int i;
		out.print(indent + "<" + tag + Attribute.dumpattributes());
		if (element == null) {
			out.println("/>");
			return;
		}
		if (element.getClass() == Vector.class) {
			Vector v = (Vector) element;
			out.println(">");
			for (i = 0; i < v.size(); i++) {
				((LiteXmlParserWriter) v.get(i)).dumpElements(new String(indent
						+ "  "), out);
			}
			out.println(indent + "</" + tag + ">");
		} else { // we have children so we can't have any elements
			out.println(">" + cleanup(getValue()) + "</" + tag + ">");
		}
	}

	// Here is a cute little method that you have to use before you write any
	// user specified names out. If the user embeds the characters &, <, >, ",
	// or '
	// in the name of an object and it is written as is, the XML reader will
	// choke.
	//
	// The order that the characters are replaced is very important in so much
	// as
	// the & character must be replaced first since the other replacements add
	// & characters.
	//
	// This method is private as it is only needed as you serialize the objects.
	// The application (user) shouldn't have to worry about the characters that
	// are used in the application or entered by the user.
	private Object cleanup(Object o) {
		if (o.getClass() == String.class) {
			String s = (String) o;
			s = s.replaceAll("&", "&amp;");
			s = s.replaceAll("<", "&lt;");
			s = s.replaceAll(">", "&gt;");
			s = s.replaceAll("\"", "&quot;");
			s = s.replaceAll("\'", "&apos;");
			s = s.replaceAll("/", "&#47;");
			// s = s.replaceAll("?","&#63;");
			s = s.replaceAll("!", "&#33;");
			return s;
		}
		return o;
	}

	// -----------------------------------------------------------------------------
	// XML parser/reader
	// -----------------------------------------------------------------------------
	// The XML reader/parser state machine technique was extracted from
	// Steven Brandt's article in JavaWorld
	// (http://www.javaworld.com/javaworld/javatips/jw-javatip128.html)
	//
	// Steven's state machine makes calls like a SAX parser; however, this
	// adaption acts more like a DOM parser to load the entire document into
	// memory exactly in the form that the writer expects so that the
	// application
	// can query the object tree to find information. Variable names and
	// programming technique differs from Steven's so that I could figure out
	// what his state machine was trying to do.
	//
	// All values for Elements and Attributes are stored as String objects and
	// Should be interpreted by the application.
	// -----------------------------------------------------------------------------
	/**
	 * Read the XML tree in from the specified stream. The stream can be
	 * anything that can be opened as a BufferedReader which includes URLs and
	 * files. The XML tree will be stored in a tree of LiteXmlParserWriter
	 * objects and the root object will be returned.
	 * 
	 * @param in BufferedReader input stream
	 * @throws IOException if an IO error occurs.
	 * @throws LiteXmlParserWriterException if an eorror occurs reading.
	 * @throws LiteXmlParserWriterEncodingException if an error occurs encoding.
	 */
	public LiteXmlParserWriter(BufferedReader in)
			throws LiteXmlParserWriterException,
			LiteXmlParserWriterEncodingException, IOException {
		myFileReader f = new myFileReader(in);
		try {
			readXML(this, f);
		} catch (LiteXmlParserWriterEncodingException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (LiteXmlParserWriterException e) {
			throw e;
		}
		if (debug) {
			System.out.println("XML file successfully loaded");
			// System.exit(0);
		}
	}

	private static int popState(Stack st) {
		if (!st.empty()) {
			return ((Integer) st.pop()).intValue();
		}
		return PRE;

	}

	private final static int // Steven Brandt's original states
			TEXT = 1,
			ENTITY = 2, OPEN_TAG = 3, CLOSE_TAG = 4,
			START_TAG = 5,
			ATTRIBUTE_LVALUE = 6, ATTRIBUTE_EQUAL = 9,
			ATTRIBUTE_RVALUE = 10,
			QUOTE = 7, IN_TAG = 8, SINGLE_TAG = 12, COMMENT = 13,
			DONE = 11,
			DOCTYPE = 14, PRE = 15, CDATA = 16;

	private int line = 1, col = 0;

	private boolean eol = false;

	// This is a very simple method which allows me to unread characters that
	// have been read from the file stream. This one is critical to the
	// XMLreader
	// so that you can find the <tag> markers and push the "<" back into the
	// reader
	// to allow you to recursively call the reader as if the <tag> were a new
	// root
	// object.
	private class myFileReader implements Serializable {
		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		private Stack<Integer> stack = new Stack<Integer>();

		private transient BufferedReader f = null;

		private myFileReader(BufferedReader in) {
			f = in;
		}

		private void unread(int c) {
			stack.push(new Integer(c));
		}

		private int read() throws IOException {
			if (stack.empty()) {
				try {
					return f.read();
				} catch (IOException e) {
					throw e;
				}
			}
			int c = stack.pop().intValue();
			return c;
		}
	}

	// ------------------------------------------------------------------------------
	// OK, here is your workhorse for the parser. It implements the same kind of
	// character reader based state machine that Steven Brandt demonstrated in
	// http://www.javaworld.com/javaworld/javatips/jw-javatip128.html. But, the
	// significant change here is that the reader calls itself recursively to
	// build the document tree and doesn't make document handler calls. The
	// states have been reordered and turned into a switch() statement but they
	// behave mostly the same.
	// ------------------------------------------------------------------------------
	private void readXML(LiteXmlParserWriter xml, myFileReader in)
			throws LiteXmlParserWriterException,
			LiteXmlParserWriterEncodingException, IOException {
		int c;
		int state = PRE;
		Stack<Integer> stack = new Stack<Integer>();
		StringBuffer sb = new StringBuffer();
		StringBuffer etag = new StringBuffer();
		String lvalue = null, rvalue = null;
		int quotec = '"';

		if (debug) {
			System.out.println("readXML starting");
		}
		while ((c = in.read()) != -1) {
			// We need to map \r, \r\n, and \n to \n
			// See XML spec section 2.11
			if (c == '\n' && eol) {
				eol = false;
				continue;
			} else if (eol) {
				eol = false;
			} else if (c == '\n') {
				line++;
				col = 0;
			} else if (c == '\r') {
				eol = true;
				c = '\n';
				line++;
				col = 0;
			} else {
				col++;
			}

			switch (state) {
			case PRE:
				if (debug) {
					System.out.println("readXML: state PRE, c=" + (char) c);
				}
				if (c == '<') { // we are outside the root tag element
					stack.push(new Integer(TEXT));
					state = START_TAG;
				}
				break;
			case START_TAG:
				if (debug) {
					System.out.println("readXML: state START_TAG, c="
							+ (char) c);
				}
				state = popState(stack);
				if (c == '/') {
					stack.push(new Integer(state));
					state = CLOSE_TAG;
				} else if (c == '?') {
					state = DOCTYPE;
				} else if (xml.getTag() != null) {
					in.unread(c);
					in.unread('<');
					try {
						readXML(xml.addElement(null), in);
					} catch (LiteXmlParserWriterException e) {
						throw e;
					}
					state = popState(stack);
				} else {
					stack.push(new Integer(state));
					state = OPEN_TAG;
					sb.append((char) c);
				}
				break;
			case OPEN_TAG:
				if (debug) {
					System.out
							.println("readXML: state OPEN_TAG, c=" + (char) c);
				}
				if (c == '>') {
					if (xml.getTag() == null) {
						xml.setTag(sb.toString());
					} else {
						xml.addElement(sb.toString());
					}
					sb.setLength(0);
					state = popState(stack);
				} else if (c == '/') {
					state = SINGLE_TAG;
				} else if (c == '-' && sb.toString().equals("!-")) {
					state = COMMENT;
				} else if (c == '[' && sb.toString().equals("![CDATA")) {
					state = CDATA;
					sb.setLength(0);
				} else if (c == 'E' && sb.toString().equals("!DOCTYP")) {
					sb.setLength(0);
					state = DOCTYPE;
				} else if (Character.isWhitespace((char) c)) {
					xml.setTag(sb.toString());
					sb.setLength(0);
					state = IN_TAG;
				} else {
					sb.append((char) c);
				}
				break;
			case TEXT:
				if (Character.isWhitespace((char) c) && sb.length() < 1) {
					break;
				}
				if (debug) {
					System.out.println("readXML: state TEXT, c=" + (char) c);
				}
				if (c == '<') { // we are currently reading a value and get the
					// start of a tag
					stack.push(new Integer(state));
					state = START_TAG;
					if (sb.length() > 0) { // we had a value, so the "<" must
						// belong to a </close>
						xml.setValue(sb.toString());
						sb.setLength(0);
					}
				} else if (c == '&') { // encoded items such as &lt
					stack.push(new Integer(state));
					state = ENTITY;
					etag.setLength(0);
				} else {
					sb.append((char) c);
				}
				break;
			case CLOSE_TAG:
				if (debug) {
					System.out.println("readXML: state CLOSE_TAG, c="
							+ (char) c);
				}
				if (c == '>') { // currently processing endtag like </tag>
					state = popState(stack);
					String endTag = sb.toString();
					if (endTag.compareTo(xml.getTag()) != 0) {
						throw new LiteXmlParserWriterEncodingException(
								"Mismatched close tag near line " + line
										+ ", column " + col);
					}
					sb.setLength(0);
					if (debug) {
						System.out
								.println("readXML: state CLOSE_TAG - Element complete...returning");
					}
					return;
				}
				sb.append((char) c);

				break;
			case IN_TAG:
				if (debug) {
					System.out.println("readXML: state IN_TAG, c=" + (char) c);
				}
				if (c == '>') {
					state = popState(stack);
				} else if (c == '/') {
					state = SINGLE_TAG;
				} else if (Character.isWhitespace((char) c)) {
					// do nothing
				} else {
					state = ATTRIBUTE_LVALUE;
					sb.append((char) c);
				}
				break;
			case DONE: // due to recursion, this will never happen
				if (debug) {
					System.out.println("readXML: state DONE, c=" + (char) c);
				}
				return;
			case CDATA:
				if (debug) {
					System.out.println("readXML: state CDATA, c=" + (char) c);
				}
				if (c == '>' && sb.toString().endsWith("]]")) {
					sb.setLength(sb.length() - 2);
					xml.setValue(sb.toString());
					sb.setLength(0);
					state = popState(stack);
				} else {
					sb.append((char) c);
				}
				break;
			case COMMENT: // Inside <!-- ... -->
				if (debug) {
					System.out.println("readXML: state COMMENT, c=" + (char) c);
				}
				if (c == '>' && sb.toString().endsWith("--")) { // found the end
					// of a comment
					sb.setLength(0);
					state = popState(stack);
				} else {
					sb.append((char) c);
				}
				break;
			case DOCTYPE:
				if (debug) {
					System.out.println("readXML: state DOCTYPE, c=" + (char) c);
				}
				if (c == '>') { // currently processing <? ... ?>
					state = popState(stack);
					if (state == TEXT) {
						state = PRE;
					}
				}
				break;
			case ENTITY:
				if (debug) {
					System.out.println("readXML: state ENTITY, c=" + (char) c);
				}
				if (c == ';') { // we are completing an entity like &gt;, &lt;
					state = popState(stack);
					String cent = etag.toString();
					etag.setLength(0);
					if (cent.equals("lt")) {
						sb.append('<');
					} else if (cent.equals("gt")) {// &gt; = "<"
						sb.append('>');
					} else if (cent.equals("amp")) {// &amp; = "&"
						sb.append('&');
					} else if (cent.equals("quot")) {// &quot; = """
						sb.append('"');
					} else if (cent.equals("apos")) { // &apos; = "'"
						sb.append('\'');
					} else if (cent.startsWith("#")) { // &#33; = "!", &#63; =
						// "?",
						// &#47; = "/", &#59; = ";"
						sb.append((char) Integer.parseInt(cent.substring(1)));
					} else {
						throw new LiteXmlParserWriterEncodingException(
								"Unknown entity: &" + cent + "; near line "
										+ line + ", column " + col);
					}
				} else {
					etag.append((char) c);
				}
				break;
			case SINGLE_TAG: // Inside <tag/>
				if (debug) {
					System.out.println("readXML: state SINGLE_TAG, c="
							+ (char) c);
				}
				if (xml.getTag() == null) {
					xml.setTag(sb.toString());
					sb.setLength(0);
				}
				if (c != '>') {
					throw new LiteXmlParserWriterEncodingException(
							"Expected > for tag <" + xml.getTag()
									+ "/> near line " + line + ", column "
									+ col);
				}
				return;
			case QUOTE:
				if (debug) {
					System.out.println("readXML: state QUOTE, c=" + (char) c);
				}
				if (c == quotec) {
					rvalue = sb.toString();
					sb.setLength(0);
					xml.Attribute.add(lvalue, rvalue);
					state = IN_TAG;
					// See section the XML spec, section 3.3.3
					// on normalization processing.
				} else if (" \r\n\u0009".indexOf(c) >= 0) {
					sb.append(' ');
				} else if (c == '&') {
					stack.push(new Integer(state));
					state = ENTITY;
					etag.setLength(0);
				} else {
					sb.append((char) c);
				}
				break;
			case ATTRIBUTE_RVALUE:
				if (debug) {
					System.out.println("readXML: state ATTRIBUTE_RVALUE, c="
							+ (char) c);
				}
				if (c == '"' || c == '\'') {
					quotec = c;
					state = QUOTE;
				} else if (Character.isWhitespace((char) c)) {
					// do nothing
				} else {
					throw new LiteXmlParserWriterEncodingException(
							"Error in attribute processing near line " + line
									+ ", column " + col);
				}
				break;
			case ATTRIBUTE_LVALUE:
				if (debug) {
					System.out.println("readXML: state ATTRIBUTE_LVALUE, c="
							+ (char) c);
				}
				if (Character.isWhitespace((char) c)) {
					lvalue = sb.toString();
					sb.setLength(0);
					state = ATTRIBUTE_EQUAL;
				} else if (c == '=') {
					lvalue = sb.toString();
					sb.setLength(0);
					state = ATTRIBUTE_RVALUE;
				} else {
					sb.append((char) c);
				}
				break;
			case ATTRIBUTE_EQUAL:
				if (debug) {
					System.out.println("readXML: state ATTRIBUTE_EQUAL, c="
							+ (char) c);
				}
				if (c == '=') {
					state = ATTRIBUTE_RVALUE;
				} else if (Character.isWhitespace((char) c)) {
					// do nothing
				} else {
					throw new LiteXmlParserWriterEncodingException(
							"Error in attribute processing near line " + line
									+ ", column " + col);
				}
				break;
			default:
				throw new LiteXmlParserWriterEncodingException(
						"State exception near line " + line + ", column " + col);
			}
		}
		if (state == DONE) {
			return;
		}
		throw new LiteXmlParserWriterEncodingException(
				"Missing end tag near line " + line + ", column " + col);

	}

	/**
	 * Signals that the XML being parsed has an error in the encoding.
	 */
	static public class LiteXmlParserWriterEncodingException extends Exception
			implements Serializable {
		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/** Creates a new instance of this class. */
		public LiteXmlParserWriterEncodingException() {
			super();
		}

		/**
		 * Creates a new exception with a given message.
		 * 
		 * @param s the message.
		 */
		public LiteXmlParserWriterEncodingException(String s) {
			super(s);
		}
	}

	/**
	 * Signals that a general exception has occurred.
	 */
	static public class LiteXmlParserWriterException extends Exception
			implements Serializable {
		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/** Creates a new instance of this class. */
		public LiteXmlParserWriterException() {
			super();
		}

		/**
		 * Creates a new exception with a given message.
		 * 
		 * @param s the message.
		 */
		public LiteXmlParserWriterException(String s) {
			super(s);
		}
	}

	/**
	 * Signals that an I/O excetion has occurred.
	 */
	static public class LiteXmlParserWriterIOException extends Exception
			implements Serializable {

		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/** Creates a new instance of this class. */
		public LiteXmlParserWriterIOException() {
			super();
		}

		/**
		 * Creates a new exception with a given message.
		 * 
		 * @param s the message.
		 */
		public LiteXmlParserWriterIOException(String s) {
			super(s);
		}
	}
}
