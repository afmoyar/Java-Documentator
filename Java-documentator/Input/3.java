public class Example{

    class Estudiante
    {
        private String codigo;
        private String correo;
        private String nombre;
        private String apellido;
        private Carrera carreraEstudiante;
        private List<Grupo> gruposEstudiante;
        private List<Nota> notasEstudiante;

        public Estudiante(String nombre, String apellido, Carrera carrera, String codigo, String correo, List<Grupo> grupos, List<Nota> notas)
        {
            this.nombre=nombre;
            this.apellido=apellido;
            this.carreraEstudiante=carrera;
            this.codigo=codigo;
            this.correo=correo;
            this.gruposEstudiante=grupos;
            this.notasEstudiante=notas;
        }
        public Estudiante()
        {
            this.cambiarNombre("UNDEFINED");
            this.cambiarApellido("UNDEFINED");
            this.cambiarCarrera(null);
            this.cambiarCodigo("UNDEFINED");
            this.cambiarCorreo("UNDEFINED");
            this.cambiarGrupos(null);
            this.cambiarNotas(null);
        }

        public String darCodigo()
        {
            return codigo;
        }
        public void cambiarCodigo(String codigo)
        {
            this.codigo=codigo;
        }
        public String darCorreo()
        {
            return correo;
        }
        public void cambiarCorreo(String correo)
        {
            this.correo=correo;
        }
        public String darNombre()
        {
            return nombre;
        }
        public void cambiarNombre(String nombre)
        {
            this.nombre=nombre;
        }
        public String darApellido()
        {
            return apellido;
        }
        public void cambiarApellido(String apellido)
        {
            this.apellido=apellido;
        }
        public Carrera darCarrera()
        {
            return carreraEstudiante;
        }
        public void cambiarCarrera(Carrera carrera)
        {
            this.carreraEstudiante=carrera;
        }
        public List<Grupo> darGrupos()
        {
            return gruposEstudiante;
        }
        public void cambiarGrupos(List<Grupo> grupos)
        {
            this.gruposEstudiante=grupos;
        }
        public List<Nota> darNota()
        {
            return notasEstudiante;
        }
        public void cambiarNotas(List<Nota> notas)
        {
            this.notasEstudiante=notas;
        }
        @Override
        public String toString()
        {
            String cadena="Codigo: "+String.valueOf(this.darCodigo())+"\n"+"Correo: "+String.valueOf(this.darCorreo())
                    +"\n"+"Nombre: "+String.valueOf(this.darNombre()+"\n"+"Apellido: "+String.valueOf(this.darApellido())
                    +"\n"+"Carrera: "+String.valueOf(this.darCarrera())+"\n"+"Lista de Grupos: Vacio"+"\n"+
                    "Lista de notas: Vacio");
            return cadena;
        }

    }
}

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
