package domain.ticservice;

import domain.ticframe.TicFrame;

public abstract class TicServiceTest implements  TicListener{

    protected static final String ROOT_DIR = System.getProperty("user.dir");
    protected static final String GLOBAL_TEST_DIR = ROOT_DIR + "/src/test";
    protected static final String GLOBAL_TEST_RESOURCES_DIR = GLOBAL_TEST_DIR + "/resources";
    protected static final String TEST_RESOURCES_DIR = GLOBAL_TEST_RESOURCES_DIR + "/domain/ticservice";



    public static void wait(int time) throws InterruptedException {
        int timeout = time;
        while (timeout>0){
            timeout--;
            Thread.sleep(1000);
        }
    }
}
