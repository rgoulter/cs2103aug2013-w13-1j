//@author A0096790N
package jim;

import static org.junit.Assert.*;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;






@RunWith(Suite.class)
@SuiteClasses({AddUnitTests.class,
               CompleteUnitTests.class,
               DisplayUnitTests.class,
               EditUnitTests.class,
               RemoveUnitTests.class,
               SearchUnitTests.class,
               StringUtilsTests.class,
               ParserDateTimeTests.class,
               SuggestionManagerTest.class,
               UndoUnitTests.class,
               TaskStorageUnitTests.class,
               IntegrationTests.class})
public class JimTests {
}
