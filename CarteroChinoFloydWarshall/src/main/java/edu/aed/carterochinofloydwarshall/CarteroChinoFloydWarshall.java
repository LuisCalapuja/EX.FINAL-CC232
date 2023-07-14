package edu.aed.carterochinofloydwarshall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/*
algoritmo del Problema del Cartero Chino para encontrar la ruta de recorrido 
mínimo en un grafo no dirigido. Ten en cuenta que este programa asume que el 
grafo es conexo.
Este programa resuelve el Problema del Cartero Chino utilizando el algoritmo 
de Floyd-Warshall para encontrar las distancias más cortas entre todos los 
pares de vértices en el grafo. Luego, se completa una matriz de distancias 
con las distancias adicionales necesarias para asegurar que todos los vértices 
tengan grado par. Finalmente, se utiliza el algoritmo de recorrido euleriano 
para obtener la ruta óptima.
*/
public class CarteroChinoFloydWarshall {

    private static final int INF = Integer.MAX_VALUE;

    public static void main(String[] args) {
        int[][] grafo = {
            {0, 2, 6, INF, INF},
            {2, 0, 3, 7, INF},
            {6, 3, 0, 1, 9},
            {INF, 7, 1, 0, 4},
            {INF, INF, 9, 4, 0}
        };

        int[] grados = calcularGrados(grafo);
        List<Integer> verticesImpares = obtenerVerticesImpares(grados);
        
        //Notamos que el grafo no posee aristas de peso negativo, con lo cual 
        //el algoritmo FloydWarshall no tendra ningun inconceniente como seria
        //el caso de generar bucles negativos.

        int[][] matrizDistancias = aplicarFloydWarshall(grafo);
        int[][] matrizCaminos = obtenerMatrizCaminos(grafo);

        int[][] matrizDistanciasCompletas = completarMatrizDistancias(matrizDistancias, matrizCaminos, verticesImpares);

        int[][] matrizRecorrido = aplicarFloydWarshall(matrizDistanciasCompletas);

        List<Integer> recorridoOptimo = obtenerRecorridoOptimo(matrizRecorrido, verticesImpares.get(0));
        
        imprimir(matrizDistancias);
                                
        System.out.println("Recorrido óptimo:");
        for (int i = 0; i < recorridoOptimo.size() - 1; i++) {
            int origen = recorridoOptimo.get(i);
            int destino = recorridoOptimo.get(i + 1);
            System.out.println("Ir de " + origen + " a " + destino);
        }
    }

    private static int[] calcularGrados(int[][] grafo) {
        //calculamos los grados de cada nodo.
        int[] grados = new int[grafo.length];

        for (int i = 0; i < grafo.length; i++) {
            for (int j = 0; j < grafo[i].length; j++) {
                if (grafo[i][j] != 0 && grafo[i][j] != INF) {
                    grados[i]++;
                //Contabilizamos las aristas de cada nodo, utilizamos el arreglo
                //grados[] para almacenar los grados de cada nodo, donde inician
                //en cero y si no hay arista que una los nodos (INF) pues no se
                //contabiliza.
                }
            }
        }

        return grados;
    }

    private static List<Integer> obtenerVerticesImpares(int[] grados) {
        List<Integer> verticesImpares = new ArrayList<>();

        for (int i = 0; i < grados.length; i++) {
            //Contabilizamos los vertices impares a través de una lista, para
            //lograrlo verificamos si el residuo de dividir entre 2 es cero.
            if (grados[i] % 2 != 0) {
                verticesImpares.add(i);
            }
        }

        return verticesImpares;
    }
    
    
    //Aplicamos el algoritmo de FloydWarshall el cual nos da la matriz de caminos
    //minimos.
    private static int[][] aplicarFloydWarshall(int[][] grafo) {
        int n = grafo.length;
        int[][] distancias = Arrays.copyOf(grafo, n);

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (distancias[i][k] != INF && distancias[k][j] != INF && distancias[i][k] + distancias[k][j] < distancias[i][j]) {
                        distancias[i][j] = distancias[i][k] + distancias[k][j];
                    }
                }
            }
        }
        
        return distancias;
    }
    
    //Método para imprimir las matrices
    public static void imprimir(int[][] matriz){
        System.out.println("Matriz de distancias mínimas:");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz.length; j++) {
                if (matriz[i][j] == INF) {
                    System.out.print("INF\t");
                } else {
                    System.out.print(matriz[i][j] + "\t");
                }
            }
            System.out.println();
        }
    }
    
    //Establecemos la matriz por la cual es posible establecer caminos.
    private static int[][] obtenerMatrizCaminos(int[][] grafo) {
        int n = grafo.length;
        int[][] caminos = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (grafo[i][j] != 0 && grafo[i][j] != INF) {
                    caminos[i][j] = j;
                } else {
                    caminos[i][j] = -1;
                }
            }
        }

        return caminos;
    }

    //Considero no es necesaria, ya que este metodo evita que hallan valores INF 
    //en la matriz, pero el algoritmo de FloydWarshall no permite esto, ya que al
    //ser un grafo conexo siempre hay un camino entre cualquier par de nodos.
    private static int[][] completarMatrizDistancias(int[][] distancias, int[][] caminos, List<Integer> verticesImpares) {
        int n = distancias.length;

        int[][] distanciasCompletas = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(distanciasCompletas[i], INF);
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (distancias[i][j] != INF) {
                    distanciasCompletas[i][j] = distancias[i][j];
                }
            }
        }

        for (int k = 0; k < verticesImpares.size(); k++) {
            for (int i = 0; i < verticesImpares.size(); i++) {
                for (int j = 0; j < verticesImpares.size(); j++) {
                    int origen = verticesImpares.get(i);
                    int destino = verticesImpares.get(j);
                    if (distancias[origen][destino] != INF && distanciasCompletas[origen][destino] == INF) {
                        int intermedio = verticesImpares.get(k);
                        int distancia = distancias[origen][intermedio] + distancias[intermedio][destino];
                        if (distancia < distanciasCompletas[origen][destino]) {
                            distanciasCompletas[origen][destino] = distancia;
                            caminos[origen][destino] = caminos[origen][intermedio];
                        }
                    }
                }
            }
        }

        return distanciasCompletas;
    }
    
    //Obtenemos el recorrido de menos longitud, a traves del uso de pilas. Notamos
    //que no hay ningun problema en trabajar directamente con la matriz de distancias
    //minimas del primer resultado de aplicar el alg. FloydWarshall.
    
    private static List<Integer> obtenerRecorridoOptimo(int[][] matrizRecorrido, int origen) {
        int n = matrizRecorrido.length;
        List<Integer> recorrido = new ArrayList<>();
        Stack<Integer> pila = new Stack<>();
        boolean[] visitados = new boolean[n];

        pila.push(origen);

        while (!pila.isEmpty()) {
            int actual = pila.peek();
            int siguiente = -1;

            for (int i = 0; i < n; i++) {
                if (matrizRecorrido[actual][i] != INF && !visitados[i]) {
                    siguiente = i;
                    break;
                }
            }

            if (siguiente != -1) {
                pila.push(siguiente);
                visitados[siguiente] = true;
            } else {
                int vertice = pila.pop();
                recorrido.add(vertice);
            }
        }

        return recorrido;
    }
}