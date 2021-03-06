/**
 * 
 */
package nl.thanod.evade.document;

import nl.thanod.evade.document.visitor.DocumentVisitor;

/**
 * @author nilsdijk
 */
public class FloatDocument extends Document
{

	public final float value;

	/**
	 * @param version
	 * @param type
	 */
	public FloatDocument(long version, float value)
	{
		super(version, Type.FLOAT);
		// TODO Auto-generated constructor stub
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * nl.thanod.evade.document.Document#accept(nl.thanod.evade.document.visitor
	 * .ParameterizedDocumentVisitor, java.lang.Object)
	 */
	@Override
	public <OUT, IN> OUT accept(DocumentVisitor<OUT, IN> visitor, IN data)
	{
		return visitor.visit(this, data);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * nl.thanod.evade.document.Document#compareValue(nl.thanod.evade.document
	 * .Document)
	 */
	@Override
	protected int compareValue(Document other)
	{
		FloatDocument that = (FloatDocument) this;
		return Float.compare(this.value, that.value);
	}

	@Override
	public String toString()
	{
		return super.toString() + "(float)" + this.value;
	}

}
