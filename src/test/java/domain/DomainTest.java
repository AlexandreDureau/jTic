package domain;

import testutils.TestUtils;

public abstract class DomainTest implements TestUtils {
    public static final String ROOT_DIR = System.getProperty("user.dir");
    public static final String TEST_DIR = ROOT_DIR + "/src/test";

    @Override
    public String getResourcesDir(){
        return TEST_DIR + "/resources/domain";
    }
}
