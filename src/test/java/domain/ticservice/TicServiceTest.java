package domain.ticservice;

import domain.DomainTest;

public abstract class TicServiceTest extends DomainTest {

    @Override
    public String getResourcesDir(){
        return super.getResourcesDir() + "/ticservice";
    }
}
