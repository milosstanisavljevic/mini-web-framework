package framework;
import java.util.Map;
import java.util.HashMap;

public class DependencyContainer {
    private Map<String, Object> implementations = new HashMap<>();

    public boolean hasImplementation(String impl) {
        if (implementations.get(impl) == null) {
            throw new RuntimeException("No implementation");
        } else {
            return true;
        }
    }
    public void addImpl(String value,Object clas){
        implementations.put(value,clas);
    }
    public Object getImplemetnation(String impl) {
        return implementations.get(impl);
    }

    public Map<String, Object> getImplementations() {
        return implementations;
    }
}
