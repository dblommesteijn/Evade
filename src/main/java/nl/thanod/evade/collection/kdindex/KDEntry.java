/**
 * 
 */
package nl.thanod.evade.collection.kdindex;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import nl.thanod.evade.collection.index.IndexDescriptor;
import nl.thanod.evade.document.Document;
import nl.thanod.evade.document.modifiers.Modifier;
import nl.thanod.evade.document.visitor.DocumentSerializerVisitor;

/**
 * @author nilsdijk
 */
public class KDEntry
{
	public final UUID id;
	private final Document[] data;

	public static class Sorter implements Comparator<KDEntry>
	{

		private final int k;

		public Sorter(int k)
		{
			this.k = k;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(KDEntry o1, KDEntry o2)
		{
			return o1.get(k).compareTo(o2.get(k));
		}
	}

	public KDEntry(UUID id, Document... documents)
	{
		this.id = id;
		this.data = documents;
	}

	public int getDimensions()
	{
		return this.data.length;
	}

	public Document get(int k)
	{
		return this.data[k];
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(this.id);
		sb.append(": ");
		for (int i = 0; i < this.data.length; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(this.data[i]);
		}
		sb.append(']');
		return sb.toString();
	}

	public static List<KDEntry> list(Iterable<Document.Entry> table, IndexDescriptor... indexers)
	{
		ArrayList<KDEntry> list = new ArrayList<KDEntry>();
		for (Document.Entry e : table) {
			Document[] documents = new Document[indexers.length];
			for (int i = 0; i < indexers.length; i++) {
				documents[i] = Modifier.safeModify(indexers[i].modifier, e.doc.get(indexers[i].path));
			}
			list.add(new KDEntry(e.id, documents));
		}
		list.trimToSize();
		return list;
	}

	public static void write(KDEntry entry, DataOutput out) throws IOException
	{
		out.writeLong(entry.id.getMostSignificantBits());
		out.writeLong(entry.id.getLeastSignificantBits());

		int size = entry.getDimensions();
		out.writeInt(size);

		for (int i = 0; i < size; i++)
			entry.get(i).accept(DocumentSerializerVisitor.VISITOR, out);
	}

	public static KDEntry read(DataInput in) throws IOException
	{
		UUID id = new UUID(in.readLong(), in.readLong());
		int count = in.readInt();

		Document[] docs = new Document[count];
		for (int i = 0; i < count; i++)
			docs[i] = DocumentSerializerVisitor.deserialize(in);
		return new KDEntry(id, docs);
	}
}
