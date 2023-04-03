package framework;

import framework.annotations.*;
import framework.request.Request;
import framework.response.Response;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DIEngine {
    private List<MethodMapper> instancesMethods = new ArrayList<>();
    private HashMap<Class,Object> singltons = new HashMap<>();
    private HashMap<Class,Object> instances = new HashMap<>();
    private DependencyContainer dependencyContainer = new DependencyContainer();
    public DIEngine() {
        try {
            this.findFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findFiles() throws IOException {
        //File f = new File("/Users/milos/Desktop/http/src/main/java/backend");
        Path dir = Paths.get("/Users/milos/Desktop/Milos_Stanisavljevic_D2/src/main/java/backend");
        Files.walk(dir).forEach(path -> listFile(path.toFile()));

    }

    public void listFile(File file) {
        if (file != null && file.getName().endsWith(".java")) {
            String s = file.getName().split("\\.")[0];
            String parent = file.getParentFile().getName();
            try {
                Class c = Class.forName(parent + "." + s);
                if (c.isAnnotationPresent(Controller.class)){
                    instanceMethods(c);
                    instanceAttributes(c);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressWarnings({"rawtypes","unchecked"})
    public void instanceMethods(Class c){
        Method[] methods = c.getDeclaredMethods();
        for(Method m : methods){
            try {
                Method method = c.getDeclaredMethod(m.getName(), Request.class);
                if (method.isAnnotationPresent(framework.annotations.Path.class)) {
                    framework.annotations.Path p = method.getAnnotation(framework.annotations.Path.class);
                    if(method.isAnnotationPresent(GET.class)){
                        Object cl = c.getDeclaredConstructor().newInstance();
                        MethodMapper methodMapper = new MethodMapper("GET", p.path(),cl,method);
                        instancesMethods.add(methodMapper);
                    }
                    if(method.isAnnotationPresent(POST.class)){
                        Object clas = c.getDeclaredConstructor().newInstance();
                        MethodMapper methodMapper = new MethodMapper("POST", p.path(),clas,method);
                        instancesMethods.add(methodMapper);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public void instanceAttributes(Class c) throws Exception{
        Field[] fields = c.getDeclaredFields();
        for(Field f : fields){
            Autowired autowired = f.getAnnotation(Autowired.class);
            if (autowired != null){
                if(autowired.verbose()) {
                    System.out.println("Initialized .. .. ");
                }
                Class klas = Class.forName(f.getType().toString().split(" ")[1]);
                if(f.getType().isInterface()){
                    //kada je klasa interface tada tu klasu instanciramo uz pomoc DC
                }else{
                    Bean b = (Bean) klas.getAnnotation(Bean.class);
                    if(klas.isAnnotationPresent(Service.class) || b.scope() == "singleton"){
                       // Object newInstance = klas.getDeclaredConstructor().newInstance();
                        boolean inst = isAlreadyCreated(klas);
                        if(!inst){
                            Object instance = klas.getDeclaredConstructor().newInstance();
                            singltons.put(klas,instance);
                        }
                        System.out.println(inst);

                    }else if(b.scope()!= "singleton"){
                        Object o = klas.getDeclaredConstructor().newInstance();
                        instances.put(klas,o);

                    }
                    else if(klas.isAnnotationPresent(Component.class)){
                        Object o = klas.getDeclaredConstructor().newInstance();
                        instances.put(klas,o);
                    }
                    else if(klas.isAnnotationPresent(Qualifier.class)){
                        Qualifier qual = (Qualifier) klas.getAnnotation(Qualifier.class);
                        dependencyContainer.addImpl(qual.value(), klas);
                    }
                    else {
                        throw new Exception("No annotations");
                    }
                }
            }
        }
    }
    public boolean isAlreadyCreated(Class c){
        if(singltons.get(c) == null){
            return false;
        }
        else return true;
    }

    public Response methodRequest(Request request) throws Exception{
        Response r = null;
        for(MethodMapper mm :instancesMethods){
            if(request.getLocation().equals(mm.getRoute()) && request.getMethod().name().equals(mm.getMethodType())){
                Object o = mm.getControllerClass();
                r = (Response) mm.getMethod().invoke(o,request);
            }
        }
        return r;
    }

}
