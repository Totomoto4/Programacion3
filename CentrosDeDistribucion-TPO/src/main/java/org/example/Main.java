package main.java.org.example;

import java.io.InputStream;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Lista de producion de clientes: ");
        int[] clientes = crearListaClientes();
        System.out.println();
        System.out.println("Matriz de centros de distribucion( costo Unitario y costo fijo) : ");
        int[][] centros = crearMatrizCentros();
        int[][] matrizRutas = crearMatrizRutas();

        System.out.println("Matriz de costos unitarios de envio de cliente a centro: ");
        int[][] matrizMinimos = crearMatrizMinimos(matrizRutas);

        System.out.println("Matriz de costos totales de envio: ");
        agregarCostosTransporteUnitario(matrizMinimos,centros,clientes);
    }

    private static int[] crearListaClientes(){

        int cantidadClientes = 4;

        //Codigo generado con CHATGPT3.5

        int[] clientesProduccion = new int[cantidadClientes];

        try {

            // Use the class loader to load the resource as an InputStream
            ClassLoader classLoader = Main.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("main/resources/clientes.txt");

            // Check if the resource is found
            if (inputStream == null) {
                System.out.println("File not found in the resources folder.");
            } else {
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    String[] values = scanner.nextLine().split(",");
                    clientesProduccion[Integer.parseInt(values[0])] = Integer.parseInt(values[1]);
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
        }
        System.out.println(Arrays.toString(clientesProduccion));
        return clientesProduccion;
    }

    //En posicion 0 tenemos el costo unitario de enviar al puerto
    //En posicion 1 tenemos el costo fijo del centro
    private static int[][]crearMatrizCentros(){

        int cantidadCentros = 2;

        //Codigo generado con CHATGPT3.5

        int[][] matrizCentros = new int[cantidadCentros][2];

        try {

            // Use the class loader to load the resource as an InputStream
            ClassLoader classLoader = Main.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("main/resources/centros.txt");

            // Check if the resource is found
            if (inputStream == null) {
                System.out.println("File not found in the resources folder.");
            } else {
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    String[] values = scanner.nextLine().split(",");
                    matrizCentros[Integer.parseInt(values[0])][0] = Integer.parseInt(values[1]);
                    matrizCentros[Integer.parseInt(values[0])][1] = Integer.parseInt(values[2]);
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
        }

        // Imprimir la matriz
        for (int i = 0; i < cantidadCentros; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.print(matrizCentros[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        return matrizCentros;
    }

    private static int[][] crearMatrizRutas(){

        // Tamaño de la matriz
        int filas = 6;
        int columnas = 6;

        // Crear una matriz de enteros con valores predeterminados de Integer.MAX_VALUE
        int[][] matrizRutas = new int[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (i == j){
                    matrizRutas[i][j] = 0;
                } else {
                    matrizRutas[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        try {

            // Use the class loader to load the resource as an InputStream
            ClassLoader classLoader = Main.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("main/resources/rutas.txt");

            // Check if the resource is found
            if (inputStream == null) {
                System.out.println("File not found in the resources folder.");
            } else {
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    String[] values = scanner.nextLine().split(",");
                    matrizRutas[Integer.parseInt(values[0])][Integer.parseInt(values[1])] = Integer.parseInt(values[2]);
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            System.out.println(e.getMessage());
        }

        // Imprimir la matriz
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                //System.out.print(matrizRutas[i][j] + " ");
            }
            //System.out.println();
        }
        //System.out.println();

        return matrizRutas;
    }

    private static int[] dijkstraUnVertice(int[][] matrizRutas, int vertice) {

        int cantidadClientes = 4;
        int cantidadCentros = 2;
        int ccc = cantidadCentros + cantidadClientes;//CantidadCentrosClientes

        //Creo el conjunto de visitados
        Set<Integer> visitados = new HashSet<>();
        visitados.add(vertice);

        //Creo el conjunto de candidatos
        Set<Integer> candidatos = new HashSet<>();
        for (int x = 0; x < ccc; x++){
            if (x != vertice){
                candidatos.add(x);
            }
        }

       //Creo la lista donde voy guardando los valores
        int[] valoresDijkstra= new int[ccc];

        //Cargo los valores del vecindario de v
        System.arraycopy(matrizRutas[vertice], 0, valoresDijkstra, 0, ccc);

        //System.out.println("Imprimo valor inicial valoresDijkstra");
        //System.out.println(Arrays.toString(valoresDijkstra));
        //System.out.println();

        while (!candidatos.isEmpty()){
            //System.out.println("Candidatos a agregar: " + candidatos);
            int min = Integer.MAX_VALUE;
            int candidatoAAgregar = -1;
            for( Integer candidato : candidatos){
                if (valoresDijkstra[candidato] <= min){
                    min = valoresDijkstra[candidato];
                    candidatoAAgregar = candidato;
                }
            }
            //System.out.println("Mejor candidato a agregar: " + Integer.toString(candidatoAAgregar));
            //System.out.println("Valor MIN: " + Integer.toString(min));

            visitados.add(candidatoAAgregar);
            candidatos.remove(candidatoAAgregar);
            List<Integer> auxCandidatos = new ArrayList<>(candidatos);

            //System.out.println("Lista de auxiliares: ");
            //System.out.println(auxCandidatos);

            for (Integer p : auxCandidatos){
                if (matrizRutas[candidatoAAgregar][p] != Integer.MAX_VALUE){
                    //System.out.printf("Valor %d tiene arista con %d\n",candidatoAAgregar,p);
                    //System.out.println("Valor arista: " + matrizRutas[candidatoAAgregar][p]);
                    if (valoresDijkstra[p] != Integer.MAX_VALUE){
                        if (valoresDijkstra[candidatoAAgregar] + matrizRutas[candidatoAAgregar][p] < valoresDijkstra[p]){
                            //System.out.println(valoresDijkstra[candidatoAAgregar] + matrizRutas[candidatoAAgregar][p] + " es mejor que " + valoresDijkstra[p]);
                            valoresDijkstra[p] = valoresDijkstra[candidatoAAgregar] + matrizRutas[candidatoAAgregar][p];
                        } else {
                            //System.out.println(valoresDijkstra[candidatoAAgregar] + matrizRutas[candidatoAAgregar][p] + " es peor que " + valoresDijkstra[p]);
                        }
                    } else {
                        //System.out.println("Agrego el valor ya que " + vertice + " no tiene arista con " + p);
                        valoresDijkstra[p] = valoresDijkstra[candidatoAAgregar] + matrizRutas[candidatoAAgregar][p];
                    }
                }
            }
            //System.out.println(Arrays.toString(valoresDijkstra));
            //System.out.println();
        }

        //System.out.println(Arrays.toString(valoresDijkstra));
        //System.out.println(Arrays.toString(valoresDijkstraCliente));

        return Arrays.copyOfRange(valoresDijkstra,cantidadCentros,ccc );

    }

    private static int[][] crearMatrizMinimos(int[][] matrizRutas){
        int cantidadCentrosDistrbucion = 2;
        int cantidadClientes = 4;
        int[][] matrizMinimos = new int[cantidadCentrosDistrbucion][cantidadClientes];

        for (int i = 0; i < cantidadCentrosDistrbucion; i++){
            matrizMinimos[i] = dijkstraUnVertice(matrizRutas,i);
        }

        // Imprimir la matriz
        for (int i = 0; i < cantidadCentrosDistrbucion; i++) {
            System.out.printf("Centro de Distribucion %d: ",i);
            for (int j = 0; j < cantidadClientes; j++) {
                System.out.print(matrizMinimos[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        return matrizMinimos;
    }

    private static void agregarCostosTransporteUnitario(int[][] matrizMinimos, int[][] matrizCentros, int[] clientes ){

        for (int i = 0; i < matrizMinimos.length; i++){
            for (int j = 0; j < matrizMinimos[0].length; j++){
                matrizMinimos[i][j] = (matrizMinimos[i][j] + matrizCentros[i][0])*clientes[j];
            }
        }

        // Imprimir la matriz
        for (int i = 0; i < matrizMinimos.length; i++) {
            for (int j = 0; j < matrizMinimos[0].length; j++) {
                System.out.print(matrizMinimos[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

    }
}