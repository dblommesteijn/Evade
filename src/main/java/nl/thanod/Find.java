/**
 * 
 */
package nl.thanod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.thanod.evade.collection.Table;
import nl.thanod.evade.collection.index.Index.Entry;
import nl.thanod.evade.collection.index.SSIndex;
import nl.thanod.evade.collection.index.Search;
import nl.thanod.evade.document.Document;
import nl.thanod.evade.document.DocumentPath;
import nl.thanod.evade.document.StringDocument;
import nl.thanod.evade.document.modifiers.LowerCase;
import nl.thanod.evade.query.Constraint;
import nl.thanod.evade.query.string.StartsWithConstraint;

/**
 * @author nilsdijk
 */
public class Find
{
	public static void main(String... args) throws IOException
	{
		String name = "github";
		File data = new File("data", name);

		Table t = Table.load(data, name);
		SSIndex index = new SSIndex(new File(data, "github144.idx"));

		//		DocumentPath path = new DocumentPath("name");
		//		find(t, index, "than", path);

		DocumentPath path = new DocumentPath("actor_attributes", "login");
		find(t, index, "koenbollen", path);
		find(t, index, "floort", path);
		find(t, index, "thanodnl", path);
		find(t, index, "jderriks", path);
		find(t, index, "jorisdormans", path);
		find(t, index, "okoeroo", path);
	}

	/**
	 * @param t
	 * @param index
	 * @param name
	 */
	private static void find(Table t, SSIndex index, final String name, DocumentPath path)
	{
		System.out.println("looking for " + name);

		Constraint c = new StartsWithConstraint(new LowerCase(), name);

		Comparable<Entry> search = new Comparable<Entry>() {

			@Override
			public int compareTo(Entry o)
			{
				// TODO test! this could be in the incorrect order
				int diff = o.match.type.code - Document.Type.STRING.code;
				if (diff != 0)
					return diff;

				StringDocument sd = (StringDocument) o.match;
				String s = name;
				return s.compareTo(sd.value);
			}
		};

		int count1 = 0;
		int count2 = 0;
		List<Document.Entry> result = new ArrayList<Document.Entry>(100);
		long took = System.nanoTime();
		Entry e = Search.before(index, search);

		while (e != null && c.test(e.match)) {
			count1++;
			Document doc = t.get(e.id);
			if (doc != null) {
				Document q = doc.get(path);
				if (q != null) {
					if (c.test(q)) {
						count2++;
						if (count2 % 1000 == 0)
							System.out.println(count2);
						Document.Entry de = new Document.Entry(e.id, doc);
						result.add(de);
					}
				}
			}
			e = e.next();
		}

		took = System.nanoTime() - took;
		System.out.println("took: " + took + "ns (" + took / 1000000 + "ms) to find " + count2);
//		for (Document.Entry de : result)
//			System.out.println(de);
	}
}
