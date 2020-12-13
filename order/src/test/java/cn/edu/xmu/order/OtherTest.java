package cn.edu.xmu.order;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class OtherTest {

    class a{
        public void aVoid(){
            System.out.println("a");
        }
    }

    class b{
        public void bVoid(){
            System.out.println("b");
        }
    }

    Class create(int i){
        if (i%2==0){
            return a.class;
        }
        else{
            return b.class;
        }
    }


    @Test
    public void createService() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class a1=create(2);
        System.out.println(a1);
        Constructor a11=a1.getConstructor();
        a x=(a)a11.newInstance();
        x.aVoid();
    }
<<<<<<< Updated upstream
=======

    @Test
    public void a(){
        int a=-1;
        int b=1-a;
        System.out.println(b);
    }
>>>>>>> Stashed changes
}
