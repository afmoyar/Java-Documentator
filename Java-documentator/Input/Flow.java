public class Flow{
    void conditionals(){
        int n;
        if(1>4)
        {
            n = 2;
            if(n!=2)
                n *= 5;
        }
        else if(true)
        {
                n+=9;
        }
    }

    void forLoop()
    {
        for (int i = 0; i <6 ; i++) {
            if(i == 5)
            {
                String x = "hello";
            }

        }
    }
    void whileLoop()
    {
        int a = 5;
        while (true)
        {
            a =- 8;
            System.out.println("hello");
            forLoop();
        }
    }
    void doWhileLoop()
    {
        boolean a = true;
        do {
            a = false;
        }while (a == true);
    }
    int withReturnValue()
    {
        int number = 6;
        if(number>6)
        {
            return 8;
        }else
            return 20;
    }
}