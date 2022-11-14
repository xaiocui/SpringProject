package com.cui.spring;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class CuiApplicationContext {


    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    /**
     * 相当于容器，用来存入BeanDefinition
     * 1.首先通过通过ComponentScan注解来确认扫描.class文件范围
     * 2.获取被component注解标注的类
     * 3.将被component注释的类，封装成beanDefinition对象
     * @param config
     */
    public CuiApplicationContext(Class config) {
        this.configClass = config;
        if (configClass.isAnnotationPresent(ComponentScan.class)){
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScan.value();  //获取value值
            path = path.replace(".", "/");//替换

            ClassLoader classLoader = CuiApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);

            File file = new File(resource.getFile());
            System.out.println(file);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String className = f.getAbsolutePath();
                    System.out.println(className);
                    if (className.endsWith(".class")) {
                        // com\cui\service\UserService
                        className = className.substring(className.indexOf("com"), className.indexOf(".class"));

                        className = className.replace("\\", ".");

                        System.out.println(className);
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            if (clazz.isAnnotationPresent(Component.class)) {
                                Component component = clazz.getAnnotation(Component.class);
                                String componentValue = component.value();
                                // Bean 判断出这个Class是一个Bean对象
                                BeanDefinition beanDefinition = new BeanDefinition();
                                // 判断是单例还是多例
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scope = clazz.getAnnotation(Scope.class);
                                    String scopeValue = scope.value();
                                    beanDefinition.setScope(scopeValue);
                                } else {
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinition.setType(clazz);
                                beanDefinitionMap.put(componentValue, beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            System.out.println(e.getCause());
                        }
                    }
                }
            }
        }

        // 创建单例bean对象
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = singletonObjects.get(beanName);
                if (bean == null) {
                    Object o = createBean(beanDefinition);
                    singletonObjects.put(beanName, o);
                }
            } else {
                createBean(beanDefinition);
            }
        }
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            String scope = beanDefinition.getScope();
            if (scope.equals("singleton")) {
                Object bean = singletonObjects.get(beanName);
                if (bean != null) {
                    return bean;
                }
                Object o = createBean(beanDefinition);
                singletonObjects.put(beanName, o);
                return o;
            } else {
                return createBean(beanDefinition);
            }
        }
    }
}
