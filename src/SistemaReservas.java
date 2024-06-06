import java.util.*;
import java.text.SimpleDateFormat;

abstract class Persona {
    private String nombre;
    private String email;

    public Persona(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public abstract void mostrarInfo();
}

class Usuario extends Persona {
    private int id;

    public Usuario(int id, String nombre, String email) {
        super(nombre, email);
        this.id = id;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("ID: " + id + ", Nombre: " + getNombre() + ", Email: " + getEmail());
    }
}

class Mesa {
    private int id;
    private int capacidad;
    private boolean disponible;

    public Mesa(int id, int capacidad) {
        this.id = id;
        this.capacidad = capacidad;
        this.disponible = true;
    }

    public int getId() { return id; }
    public int getCapacidad() { return capacidad; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}

class Reserva {
    private int id;
    private Usuario usuario;
    private Mesa mesa;
    private Calendar fechaHora;

    public Reserva(int id, Usuario usuario, Mesa mesa, Calendar fechaHora) {
        this.id = id;
        this.usuario = usuario;
        this.mesa = mesa;
        this.fechaHora = fechaHora;
        mesa.setDisponible(false);
    }

    public int getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Mesa getMesa() { return mesa; }
    public Calendar getFechaHora() { return fechaHora; }
    public void setFechaHora(Calendar fechaHora) { this.fechaHora = fechaHora; }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return "Reserva ID: " + id + " para " + usuario.getNombre() + " en mesa " + mesa.getId() + " el " + dateFormat.format(fechaHora.getTime());
    }
}

public class SistemaReservas {
    private static List<Usuario> usuarios = new ArrayList<>();
    private static List<Mesa> mesas = new ArrayList<>();
    private static List<Reserva> reservas = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        initMesas();
        boolean salir = false;
        while (!salir) {
            System.out.println("\n1. Hacer Reserva");
            System.out.println("2. Modificar Reserva");
            System.out.println("3. Eliminar Reserva");
            System.out.println("4. Verificar Disponibilidad");
            System.out.println("5. Mostrar Reservas");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");
            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();  // Consume nueva linea sobrante

                switch (opcion) {
                    case 1:
                        hacerReserva();
                        break;
                    case 2:
                        modificarReserva();
                        break;
                    case 3:
                        eliminarReserva();
                        break;
                    case 4:
                        verificarDisponibilidad();
                        break;
                    case 5:
                        mostrarReservas();
                        break;
                    case 6:
                        salir = true;
                        break;
                    default:
                        System.out.println("Opción no válida, intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine(); // Consume la entrada incorrecta y evita un bucle infinito
            }
        }
    }

    private static void initMesas() {
        for (int i = 1; i <= 8; i++) {
            mesas.add(new Mesa(i, 2));
        }
        for (int i = 9; i <= 12; i++) {
            mesas.add(new Mesa(i, 4));
        }
        for (int i = 13; i <= 14; i++) {
            mesas.add(new Mesa(i, 8));
        }
    }

    private static void verificarDisponibilidad() {
        System.out.println("Estado actual de las mesas:");
        for (Mesa mesa : mesas) {
            System.out.println("Mesa " + mesa.getId() + " para " + mesa.getCapacidad() + " personas - " +
                               (mesa.isDisponible() ? "Disponible" : "No disponible"));
        }
    }

    private static void hacerReserva() {
        System.out.println("Ingrese el nombre del comensal:");
        String nombreComensal = scanner.nextLine();
        System.out.println("Ingrese el email del comensal:");
        String emailComensal = scanner.nextLine();

        System.out.println("Seleccione una mesa disponible:");
        for (int i = 0; i < mesas.size(); i++) {
            if (mesas.get(i).isDisponible()) {
                System.out.println((i + 1) + ". Mesa " + mesas.get(i).getId() + " para " + mesas.get(i).getCapacidad() + " personas");
            }
        }
        int choiceMesa = scanner.nextInt();
        scanner.nextLine();  // Consume newline left-over
        Mesa mesa = mesas.get(choiceMesa - 1);

        System.out.println("Ingrese la fecha y hora de la reserva (dd/mm/yyyy HH:mm):");
        String fechaHoraIn = scanner.nextLine();
        Calendar fechaHora = Calendar.getInstance();
        try {
            fechaHora.setTime(dateTimeFormat.parse(fechaHoraIn));
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR, 1); // Incrementa la hora actual en una hora

            if (fechaHora.before(now)) {
                System.out.println("No se puede realizar una reserva menos de una hora en el futuro. Por favor, elija una hora adecuada.");
            } else {
                Usuario nuevoComensal = new Usuario(usuarios.size() + 1, nombreComensal, emailComensal);
                usuarios.add(nuevoComensal);  // Agregamos el nuevo usuario a la lista
                Reserva nuevaReserva = new Reserva(reservas.size() + 1, nuevoComensal, mesa, fechaHora);
                reservas.add(nuevaReserva);
                System.out.println("Reserva realizada exitosamente.");
            }
        } catch (Exception e) {
            System.out.println("Formato de fecha o hora no válido.");
        }
    }

    private static void modificarReserva() {
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas para modificar.");
            return;
        }
        System.out.println("Seleccione la reserva que desea modificar:");
        for (int i = 0; i < reservas.size(); i++) {
            System.out.println((i + 1) + ". " + reservas.get(i));
        }
        int choiceReserva = scanner.nextInt() - 1;
        scanner.nextLine();  // Consumir el resto de la línea
        if (choiceReserva < 0 || choiceReserva >= reservas.size()) {
            System.out.println("Selección no válida. Por favor, elija un número de la lista.");
            return;
        }
        Reserva reserva = reservas.get(choiceReserva);

        System.out.println("Ingrese la nueva fecha y hora de la reserva (dd/mm/yyyy HH:mm):");
        String fechaHoraIn = scanner.nextLine();
        try {
            Calendar fechaHora = Calendar.getInstance();
            fechaHora.setTime(dateTimeFormat.parse(fechaHoraIn));
            reserva.setFechaHora(fechaHora);
            System.out.println("Reserva modificada exitosamente.");
        } catch (Exception e) {
            System.out.println("Formato de fecha o hora no válido.");
        }
    }

    private static void eliminarReserva() {
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas para eliminar.");
            return;
        }
        System.out.println("Seleccione la reserva que desea eliminar:");
        for (int i = 0; i < reservas.size(); i++) {
            System.out.println((i + 1) + ". " + reservas.get(i));
        }
        int choiceReserva = scanner.nextInt() - 1;
        scanner.nextLine();  // Consumir el resto de la línea

        if (choiceReserva < 0 || choiceReserva >= reservas.size()) {
            System.out.println("Selección no válida. Por favor, elija un número de la lista.");
            return;
        }

        Reserva reserva = reservas.get(choiceReserva);
        reserva.getMesa().setDisponible(true); // Marca la mesa como disponible
        reservas.remove(choiceReserva); // Elimina la reserva de la lista
        System.out.println("Reserva eliminada exitosamente.");
    }

    private static void mostrarReservas() {
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas.");
            return;
        }
        for (Reserva reserva : reservas) {
            System.out.println(reserva);
        }
    }
}
