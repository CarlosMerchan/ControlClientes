package web;

import datos.ClienteDaoJdbc;
import dominio.Clientes;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import jdk.nashorn.internal.objects.NativeArray;

@WebServlet("/ServletControlador")
public class ServletControlador extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion != null) {
            switch (accion) {
                case "editar":
                    this.editarCliente(request, response);
                    break;

                case "eliminar":
                    this.eliminarCliente(request, response);
                    break;

                default:
                    this.accionDefault(request, response);

            }
        } else {
            this.accionDefault(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion != null) {
            switch (accion) {
                case "insertar":
                    this.insertarCliente(request, response);
                    break;

                case "modificar":
                    this.modificarCliente(request, response);
                    break;

                default:
                    this.accionDefault(request, response);

            }
        } else {
            this.accionDefault(request, response);
        }

    }

    private double calcularSaldoClientes(List<Clientes> clientes) {
        double total = 0;
        for (Clientes cliente : clientes) {
            total += cliente.getSaldo();
        }

        return total;
    }

    private void insertarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recuperamos los valores del formulario
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        double saldo = 0;
        String saldoString = request.getParameter("saldo");
        if (saldoString != null && !"".equals(saldoString)) {
            saldo = Double.parseDouble(saldoString);
        }

        //Creamos el objeto cliente
        Clientes cliente = new Clientes(nombre, apellido, email, telefono, saldo);
        //Insertar en la base de datos
        int registrosModificados = new ClienteDaoJdbc().insertar(cliente);
        System.out.println("Registros modificados " + registrosModificados);
        //redirigir a la pagina principal
        this.accionDefault(request, response);

    }

    private void editarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //recuperamos el idCliente
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        Clientes cliente = new ClienteDaoJdbc().encontrar(new Clientes(idCliente));
        System.out.println(cliente);
        request.setAttribute("cliente", cliente);
        String jspEditar = "/WEB-INF/paginas/clientes/editarCliente.jsp";
        request.getRequestDispatcher(jspEditar).forward(request, response);

    }

    private void accionDefault(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Clientes> clientes = new ClienteDaoJdbc().listar();
        System.out.println(clientes);
        HttpSession sesion = request.getSession();
        sesion.setAttribute("ListaClientes", clientes);
        sesion.setAttribute("totalClientes", clientes.size());
        sesion.setAttribute("saldoTotal", calcularSaldoClientes(clientes));
        //request.getRequestDispatcher("clientes.jsp").forward(request, response);
        response.sendRedirect("clientes.jsp");
    }

    private void modificarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recuperamos los valores del formulario editarCliente
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        double saldo = 0;
        String saldoString = request.getParameter("saldo");
        if (saldoString != null && !"".equals(saldoString)) {
            saldo = Double.parseDouble(saldoString);
        }

        //Creamos el objeto cliente
        Clientes cliente = new Clientes(idCliente, nombre, apellido, email, telefono, saldo);
        //Insertar en la base de datos
        int registrosModificados = new ClienteDaoJdbc().actualizar(cliente);
        System.out.println("Registros modificados " + registrosModificados);
        //redirigir a la pagina principal
        this.accionDefault(request, response);
    }

    private void eliminarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //recuperamos el idCliente
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        int registroEliminado = new ClienteDaoJdbc().delete(new Clientes(idCliente));
        System.out.println("Registro Eliminado "+ registroEliminado);
        //redirigir a la pagina principal
        this.accionDefault(request, response);

    }
}
