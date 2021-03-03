package mvc;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherServlet implements Servlet {

    private static Map<String, MethodInfo> requestMappingMap = new ConcurrentHashMap<>();
    private static Map<Class, Object> instanceMapping = new ConcurrentHashMap<>();

    private void start() throws Exception{
        //todo 扫描所有包含Controller的类
        List<Class> controllers = new ArrayList<>();
        for (Class controller : controllers) {
            Method[] methods = controller.getMethods();
            for (Method method : methods) {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                if (requestMapping == null) {
                    continue;
                }
                Object instance = instanceMapping.get(controller);
                if (instance == null) {
                    instance = controller.newInstance();
                    instanceMapping.put(controller, instance);
                }
                Parameter[] params = method.getParameters();
                MethodInfo methodInfo = new MethodInfo();
                methodInfo.setClazz(controller);
                methodInfo.setMethod(method);
                methodInfo.setParameters(params);
                requestMappingMap.put(requestMapping.value(), methodInfo);
            }
        }
    }


    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        try {
            start();
        }catch (Exception e){
            throw new RuntimeException("start init error");
        }
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String url = request.getRequestURI();
        MethodInfo methodInfo = requestMappingMap.get(url);
        //400
        if (methodInfo == null) {
            response.sendError(404);
            response.getOutputStream().write(404);
            return;
        }
        try {
            Class clazz = methodInfo.getClazz();
            Method method = methodInfo.getMethod();
            Parameter[] paramTypes = methodInfo.getParameters();
            Object instance = instanceMapping.get(clazz);

            Object[] params = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                //构造入参
                Parameter parameter = paramTypes[i];
                Class paramType = parameter.getType();
                String name = parameter.getName();
                String paramString = request.getParameter(name);
                //基本数据类型转化
                if (paramType.equals(String.class)){
                    params[i] = paramString;
                    continue;
                }
                if (paramType.equals(Integer.class)){
                    params[i] = Integer.valueOf(paramString);
                    continue;
                }
                if (paramType.equals(Long.class)){
                    params[i] = Long.valueOf(paramString);
                    continue;
                }
                if (paramType.equals(Boolean.class)){
                    params[i] = Boolean.valueOf(paramString);
                    continue;
                }
                //自定义参数类处理 todo
                params[i] = new Object();

            }
            Object result = method.invoke(instance, params);
            if (result.getClass().equals(String.class)) {
                //重定向
                OutputStream os = response.getOutputStream();
                os.write(1);
            }
        } catch (Exception e) {
            //异常500
            response.sendError(500, e.toString());
            response.getOutputStream().write(500);
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
