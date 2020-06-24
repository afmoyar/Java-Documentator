public class UML{
    interface Thing {
        public void getName(); // interface method (does not have a body)
        public void doStuff(); // interface method (does not have a body)
    }
    interface Person extends Thing{
        public void getName(); // interface method (does not have a body)
        public void doStuff(); // interface method (does not have a body)
    }
    interface Worker{
        public void getWork(); // interface method (does not have a body)
        public void getPaid(); // interface method (does not have a body)
    }
    class Teacher implements Person, Worker {
        private String designation = "Teacher";
        private String collegeName = "Beginnersbook";
        private Job teaches_job = "Teacher";
        private Heart heart;
        public Teacher(String designation,String collegeName, Job teaches_job){
            //constructor
        }

        public String getDesignation() {
            return designation;
        }
        protected void setDesignation(String designation) {
            this.designation = designation;
        }
        protected String getCollegeName() {
            return collegeName;
        }
        protected void setCollegeName(String collegeName) {
            this.collegeName = collegeName;
        }
        void does(){
            System.out.println("Teaching");
        }
    }
    class GoodTeacher extends Teacher{
        String mainSubject = "Physics";
        public static void main(String args[]){
            JavaExample obj = new JavaExample();
            /* Note: we are not accessing the data members
             * directly we are using public getter method
             * to access the private members of parent class
             */
            System.out.println(obj.getCollegeName());
            System.out.println(obj.getDesignation());
            System.out.println(obj.mainSubject);
            obj.does();
        }
    }
    class Job {
        private String role;
        private long salary;
        private int id;

        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
        public long getSalary() {
            return salary;
        }
        public void setSalary(long salary) {
            this.salary = salary;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
    }
    class Heart{
        private String cells;
        public String blood;
        public String muscles;

        public String getCells()
        {

        }
        public String getBlood()
        {

        }
        public String getMuscles() {
            return muscles;
        }
    }

}