public class ObserverPattern{
    interface Subject {

        //methods to register and unregister observers
        public void register(Observer obj);
        public void unregister(Observer obj);

        //method to notify observers of change
        public void notifyObservers();

        //method to get updates from subject
        public Object getUpdate(Observer obj);

    }
    interface Observer {

        //method to update the observer, used by subject
        public void update();

        //attach with subject to observe
        public void setSubject(Subject sub);
    }
    class MyTopic implements Subject {

        private List<Observer> observers;
        private String message;
        private boolean changed;
        private final Object MUTEX= new Object();

        public MyTopic(){
            this.observers=new ArrayList<>();
        }
        @Override
        public void register(Observer obj) {
            if(obj == null) throw new NullPointerException("Null Observer");
            synchronized (MUTEX) {
                if(!observers.contains(obj)) observers.add(obj);
            }
        }

        @Override
        public void unregister(Observer obj) {
            synchronized (MUTEX) {
                observers.remove(obj);
            }
        }

        @Override
        public void notifyObservers() {
            List<Observer> observersLocal = null;
            //synchronization is used to make sure any observer registered after message is received is not notified
            synchronized (MUTEX) {
                if (!changed)
                    return;
                observersLocal = new ArrayList<>(this.observers);
                this.changed=false;
            }
            for (Observer obj : observersLocal) {
                obj.update();
            }

        }

        @Override
        public Object getUpdate(Observer obj) {
            return this.message;
        }

        //method to post message to the topic
        public void postMessage(String msg){
            System.out.println("Message Posted to Topic:"+msg);
            this.message=msg;
            this.changed=true;
            notifyObservers();
        }

    }

    class MyTopicSubscriber implements Observer {

        private String name;
        private Subject topic;

        public MyTopicSubscriber(String nm){
            this.name=nm;
        }
        @Override
        public void update() {
            String msg = (String) topic.getUpdate(this);
            if(msg == null){
                System.out.println(name+":: No new message");
            }else
                System.out.println(name+":: Consuming message::"+msg);
        }

        @Override
        public void setSubject(Subject sub) {
            this.topic=sub;
        }

    }
}