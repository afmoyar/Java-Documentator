public class Example{
    class Carrera
    {
        private String nombre;
        public Facultad facultadCarrera;
        protected String departamento;
        private static List<Estudiante> nuevoListaEstudiantes;

        public Carrera(String nombre, Facultad facultad, String departamento, List<Estudiante> estudiantes)
        {
            this.nombre=nombre;
            this.facultadCarrera=facultad;
            this.departamento=departamento;
            this.nuevoListaEstudiantes=estudiantes;
        }
        /*
        public Carrera()
        {
            this.cambiarNombre("UNDEFINED");
            this.cambiarFacultad(null);
            this.cambiarDepartamento("UNDEFINED");
            this.cambiarEstudiantes(null);
        }
        */
        public Facultad darFacultad()
        {
            return facultadCarrera;
        }
        public void cambiarFacultad(Facultad facultad)
        {
            this.facultadCarrera=facultad;
        }
        public String darDepartamento()
        {
            return departamento;
        }
        public void cambiarDepartamento(String departamento)
        {
            this.departamento=departamento;
        }
        private static String darNombre()
        {
            return nombre;
        }
        protected void cambiarNombre(String nombre)
        {
            this.nombre=nombre;
        }
        public List<Estudiante> darEstudiantes()
        {
            return nuevoListaEstudiantes;
        }
        public void cambiarEstudiantes(List<Estudiante> estudiantes)
        {
            this.nuevoListaEstudiantes=estudiantes;
        }
        @Override
        public String toString()
        {
            String cadena="Carrera: "+String.valueOf(this.darNombre())+"\n"+"Facultad: "+String.valueOf(this.darFacultad())
                    +"\n"+"Departamento: "+String.valueOf(this.darDepartamento()+"\n"+"Lista de estudiantes: Vacio");
            return cadena;
        }

    }
}