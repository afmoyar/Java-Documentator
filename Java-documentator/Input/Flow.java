public class Flow{
    void conditionals(){
        int n;
        if(1>4||n!=0)
        {
            n = 2;
            if(n!=2)
                n *= 5;
        }
        else if(true||false)
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
    void withArrays()
    {
        int[] numbers;
        ArrayList<String> words = new ArrayList<>();
        words.add("Word");
        HashMap<String,Integer> numberMap = new HashMap<>();
        HashSet<Integer> numberSet = new HashSet<>();
        Integer x = new Integer(5);
    }
}