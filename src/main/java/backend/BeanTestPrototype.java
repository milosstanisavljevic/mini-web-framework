package backend;

import framework.annotations.Bean;
import framework.annotations.Qualifier;

@Bean(scope = "prototype")
@Qualifier("impl")
public class BeanTestPrototype {
    public BeanTestPrototype(){}
}
