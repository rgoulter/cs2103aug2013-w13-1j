package jim;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import jim.util.StringUtils;

import org.junit.Test;

public class StringUtilsTests {

	@Test
	public void testJoinStringArrayChar() {
		assertEquals("a b c", StringUtils.join(new String[]{"a", "b", "c"}, ' '));
		assertEquals("d.e.f", StringUtils.join(new String[]{"d", "e", "f"}, '.'));
	}

	@Test
	public void testJoinStringArrayCharInt() {
		assertEquals("a b c", StringUtils.join(new String[]{"Z", "a", "b", "c"}, ' ', 1));
	}

	@Test
	public void testJoinStringArrayCharIntInt() {
		assertEquals("a b c", StringUtils.join(new String[]{"Y", "a", "b", "c", "Z"}, ' ', 1, 4));
	}

	@Test
	public void testIsStringSurroundedBy() {
		assertTrue(StringUtils.isStringSurroundedBy("<abc>", '<', '>'));
		assertFalse(StringUtils.isStringSurroundedBy("abc", '<', '>'));
	}

	@Test
	public void testStripStringPrefixSuffix() {
		assertEquals("abc", StringUtils.stripStringPrefixSuffix("<abc>", 1));
		assertEquals("b", StringUtils.stripStringPrefixSuffix("abc", 1));
	}
	
	@Test
	public void testSubsequenceMatching() {
		Set<String> inputSet = new HashSet<String>();
		inputSet.add("ax");
		inputSet.add("ay");

		// Can match with the last character in an input subsequence
		Set<String> outputSet = StringUtils.filterMatchBySubseq(inputSet, "ax");
		assertTrue(outputSet.contains("ax"));
		assertFalse(outputSet.contains("ay"));
	}

}
