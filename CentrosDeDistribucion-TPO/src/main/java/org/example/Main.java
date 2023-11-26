package org.example;

import java.io.InputStream;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Lista de producion de clientes: ");
        int[] clientes = crearListaClientes();
        int cantidadClientes = clientes.length;
        System.out.println();

        System.out.println("Matriz de centros de distribucion(costo Unitario y costo fijo): ");
        int[][] centros = crearMatrizCentros();
        int cantidadCentros = centros.length;

        int[][] matrizRutas = crearMatrizRutas(cantidadClientes,cantidadCentros);
        System.out.println("Matriz de costos unitarios de envio de cliente a centro: ");
        int[][] matrizUnitarios = crearMatrizMinimos(matrizRutas,cantidadClientes,cantidadCentros);

        System.out.println("Matriz de costos totales de envio: ");
        int[][] matrizCostosTotalesEnvio = agregarCostosTransporteUnitario(matrizUnitarios, centros, clientes);
        buscarMejorOpcion(matrizCostosTotalesEnvio,centros);

    }

    private static int[] crearListaClientes(){

        //Codigo generado con CHATGPT3.5

        List<Integer> clientesProduccion = new ArrayList<>();

        try {
            // Use the class loader to load the resource as an InputStream
            ClassLoader classLoader = Main.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("clientes.txt");

            // Check if the resource is found
            if (inputStream == null) {
                System.out.println("File not found in the resources folder.");
            } else {
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    String[] values = scanner.nextLine().split(",");
                    clientesProduccion.add(Integer.parseInt(values[1]));
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            System.out.println(e.getMessage());
        }
        System.out.println(clientesProduccion);
        return clientesProduccion.stream().mapToInt(Integer::intValue).toArray();
    }

    //En posicion 0 tenemos el costo unitario de enviar al puerto
    //En posicion 1 tenemos el costo fijo del centro
    private static int[][]crearMatrizCentros(){

        //Codigo generado con CHATGPT3.5

        // List to store center coordinates dynamically
        List<int[]> matrizCentros = new ArrayList<>();

        try {
            // Use the class loader to load the resource as an InputStream
            ClassLoader classLoader = Main.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("centros.txt");

            // Check if the resource is found
            if (inputStream == null) {
                System.out.println("File not found in the resources folder.");
            } else {
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    String[] values = scanner.nextLine().split(",");
                    int centroIndex = Integer.parseInt(values[0]);
                    int xCoordinate = Integer.parseInt(values[1]);
                    int yCoordinate = Integer.parseInt(values[2]);

                    // Ensure the list has enough capacity to accommodate the center
                    while (matrizCentros.size() <= centroIndex) {
                        matrizCentros.add(new int[2]);
                    }

                    // Set the coordinates in the list
                    matrizCentros.get(centroIndex)[0] = xCoordinate;
                    matrizCentros.get(centroIndex)[1] = yCoordinate;
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            System.out.println(e.getMessage());
        }

        // Convert the list to an array
        int[][] matrizCentros2 = matrizCentros.toArray(new int[0][]);

        imprimirMatriz(matrizCentros2);

        return matrizCentros2;
    }

    private static int[][] crearMatrizRutas(int cantClientes, int cantCentros){

        // Tamaño de la matriz N*N
        int N = cantCentros + cantClientes;

        // Crear una matriz de enteros con valores predeterminados de Integer.MAX_VALUE
        int[][] matrizRutas = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
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
            InputStream inputStream = classLoader.getResourceAsStream("rutas.txt");

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

        //imprimirMatriz(matrizRutas);

        return matrizRutas;
    }

    private static int[] dijkstraUnVertice(int[][] matrizRutas, int vertice, int cantClientes, int cantCentros) {


        int ccc = cantCentros + cantClientes;//CantidadCentrosClientes

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

        return Arrays.copyOfRange(valoresDijkstra,0,cantClientes );

    }

    private static int[][] crearMatrizMinimos(int[][] matrizRutas,int cantClientes, int cantCentros){

        int[][] matrizMinimos = new int[cantCentros][cantClientes];

        for (int i = cantClientes; i < cantClientes + cantCentros; i++){
            matrizMinimos[i - cantClientes] = dijkstraUnVertice(matrizRutas,i,cantClientes, cantCentros);
        }

        imprimirMatriz(matrizMinimos);

        return matrizMinimos;
    }

    private static int[][] agregarCostosTransporteUnitario(int[][] matrizMinimos, int[][] matrizCentros, int[] clientes ){

        for (int i = 0; i < matrizMinimos.length; i++){
            for (int j = 0; j < matrizMinimos[0].length; j++){
                matrizMinimos[i][j] = (matrizMinimos[i][j] + matrizCentros[i][0])*clientes[j];
            }
        }

        imprimirMatriz(matrizMinimos);

        return matrizMinimos;
    }

    private static void buscarMejorOpcion(int[][] matrizCostosTotales, int[][] matrizCentros){

        int cantidadCentros = matrizCostosTotales.length;

        int u = -1 ;
        int c = -1;

        NodoBB nodoInicial = new NodoBB(new int[cantidadCentros],0,matrizCostosTotales, matrizCentros);

        PriorityQueue<NodoBB> colaNodos = new PriorityQueue<>(Comparator.comparing(NodoBB::getC));
        colaNodos.add(nodoInicial);

        while (!colaNodos.isEmpty() && (colaNodos.peek().getC() <= u || c == -1)){

            NodoBB nodoEstudiado = colaNodos.remove();
            System.out.println("Nodo: " + Arrays.toString(nodoEstudiado.getCordenadas()));
            System.out.println("U: " + nodoEstudiado.getU());
            u = nodoEstudiado.getU();
            System.out.println("C: " + nodoEstudiado.getC());
            c = nodoEstudiado.getC();

            if (nodoEstudiado.getIndice() < cantidadCentros){

                int[] copiaCordenadas1 = new int[cantidadCentros];
                System.arraycopy(nodoEstudiado.getCordenadas(),0,copiaCordenadas1,0,cantidadCentros);
                copiaCordenadas1[nodoEstudiado.getIndice()] = 1;
                colaNodos.add(new NodoBB(copiaCordenadas1,nodoEstudiado.getIndice() + 1, matrizCostosTotales, matrizCentros));

                int[] copiaCordenadas2 = new int[cantidadCentros];
                System.arraycopy(nodoEstudiado.getCordenadas(),0,copiaCordenadas2,0,cantidadCentros);

                copiaCordenadas2[nodoEstudiado.getIndice()] = -1;
                colaNodos.add(new NodoBB(copiaCordenadas2,nodoEstudiado.getIndice() + 1,matrizCostosTotales, matrizCentros));
            }
        }

        System.out.println("\nEstos son los nodos que quedaron en la cola sin recorrer");
        while (!colaNodos.isEmpty()){
            NodoBB nodoEstudiado = colaNodos.remove();
            System.out.println("Nodo: " + Arrays.toString(nodoEstudiado.getCordenadas()));
            System.out.println("U: " + nodoEstudiado.getU());
            System.out.println("C: " + nodoEstudiado.getC());
        }
    }

    private static void imprimirMatriz(int[][] matriz){
        int filas = matriz.length;
        int columnas = matriz[0].length;

        // Imprimir la matriz
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

    }

}