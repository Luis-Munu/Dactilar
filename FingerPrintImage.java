import java.lang.Math;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
/**
 * Clase usada para guardar una matriz de char y realizar diversas operaciones con ellos.
 * Los chars equivalen a pixeles de una imagen.
 */
public class FingerPrintImage{
    private char[][] imagen;
    private int Height; //alto
    private int Width;  //ancho

    public FingerPrintImage(int Height, int Width){
        imagen= new char[Width][Height];
        this.Height=Height;
        this.Width=Width;
    }
    
    //Setters y getters
    public int getHeight(){
        return Height;
    }
    public int getWidth(){
        return Width;
    }
    public char getPixel(int x, int y){
        return imagen[x][y];
    }
    public void setPixel(int x, int y, char gris){
        imagen[x][y]=gris;
    }

    /**
     * Implementación de los filtros usados para limpiar la imagen de impurezas.
     * El filtro 1 realiza la siguiente función B1=p+b.g.(d+e)+d.e.(b+g)
     * El filtro 2 realiza la siguiente función B2=p[(a+b+d).(e+g+h)+(b+c+e).(d+f+g)]
     */
    public char filtro1(int x,int y){
        return (char)(imagen[x][y] & imagen[x][y-1] | imagen[x][y+1] | (imagen[x-1][y] & imagen[x+1][y]) & imagen[x-1][y] | imagen[x+1][y] | (imagen[x][y-1] & imagen[x][y+1]));
    }
    public char filtro2(int x,int y){
        return (char)(imagen[x][y] | ((imagen[x-1][y-1] & imagen[x][y-1] & imagen[x-1][y]) | (imagen[x+1][y] & imagen[x][y+1] & imagen[x+1][y+1]) & (imagen[x][y-1] & imagen[x+1][y-1] & imagen[x+1][y]) | (imagen[x-1][y] & imagen[x-1][y+1] & imagen[x][y+1])));
    }

    /**
     * Algoritmo que realiza las iteraciones de Zhang-Suen.
     * Fuertemente inspirado en el link de Rosetta-Code y transformado a nuestras necesidades.
     * Realiza diversas operaciones en cada pixel de la imagen y sus adyacentes, sacando un valor del proceso.
     * Solo realizamos las operaciones si el pixel es negro para acelerar el proceso.
     */
    public boolean Iteration(int iter){
        char[] c= new char[9];
        FingerPrintImage aux = copyF();
        int count=0;
        int negros=0;
        boolean cambio=false;
        for (int i = 10; i < Width-1; i++)
        {
            for (int j = 10; j < Height-1; j++)
            {
                if(imagen[i][j]==255){
                    count=0;
                    negros=0;
                    c[0] = imagen[i-1][j];
                    c[1] = imagen[i-1][j+1];
                    c[2] = imagen[i][j+1];
                    c[3] = imagen[i+1][j+1];
                    c[4] = imagen[i+1][j];
                    c[5] = imagen[i+1][j-1];
                    c[6] = imagen[i][j-1];
                    c[7] = imagen[i-1][j-1];
                    c[8] = imagen[i-1][j];
                    for(int k=0; k<c.length-1;k++){
                        if(c[k]==255 && c[k+1]==0) count++;
                        if(c[k]==0) negros++;
                    }
                    
                    int A  = count;
                    int B  = negros;
                    int m1 = iter == 0 ? (c[0] * c[2] * c[4]) : (c[0] * c[2] * c[6]);
                    int m2 = iter == 0 ? (c[2] * c[4] * c[6]) : (c[0] * c[4] * c[6]);

                    if (A == 1 && (B >= 2 && B <= 6) && m1 == 0 && m2 == 0){
                        aux.setPixel(i, j, (char)255); 
                        cambio=true;
                    }
                    else aux.setPixel(i, j, (char)0);
                }
            }
        }
        for (int i = 0; i < Width; i++) {
            for (int j = 0; j < Height; j++) {

                int tmp = 255 - aux.getPixel(i, j);
                if (imagen[i][j] == tmp && imagen[i][j] == 255) {
                    imagen[i][j] = 255;
                } else {
                    imagen[i][j] = 0;
                }

            }
        }
        return cambio;
    }

    /**
     * Implementacion del algoritmo de deteccion de minucias.
     * Itera alrededor de un pixel y devuelve un valor, si el valor es 1 o 3 lo tendremos en cuenta.
     */
    public boolean detectarMinucias(int x, int y){
    int[][] nbrs = {{1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,1},{0,1},{1,1},{1,0}};
    int counter=0;
        for (int i = 0; i < nbrs.length - 1; i++){
            counter+=Math.abs(imagen[x + nbrs[i][1]][y + nbrs[i][0]]-imagen[x + nbrs[i+1][1]][y + nbrs[i+1][0]]);
        }
        if(counter/2==1 || counter/2==3){
            return true;
        }
        else{
            return false;
        }
        
    }

    /**
     * Algoritmo usado para convertir una FingerPrintImage a BufferedImage.
     */
    public BufferedImage copy() {

        BufferedImage copiamatriz = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < Width; i++) {

            for (int j = 0; j < Height; j++) {
                int a = imagen[i][j];
                Color newColor = new Color(a,a,a);
                copiamatriz.setRGB(i,j,newColor.getRGB());

            }

        }

        return copiamatriz;

    }

    /**
     * Algoritmo usado para crear una copia de una FingerPrintImage.
     */
    public FingerPrintImage copyF() {

        FingerPrintImage copiamatriz = new FingerPrintImage(Height,Width);

        for (int i = 0; i < Width; i++) {

            for (int j = 0; j < Height; j++) {

                copiamatriz.setPixel(i, j, imagen[i][j]);

            }

        }

        return copiamatriz;

    }

    /*
     * Invierte una imagen que esté en ByN. El negro pasa a blanco y el blanco pasa a negro.
     * Debido a algunas operaciones que realizamos distinguimos el valor 1 tambien.
     */
    public FingerPrintImage invertir(){
        FingerPrintImage salida=new FingerPrintImage(getHeight(), getWidth());
        for (int x = 0; x < getWidth(); x++){
            for (int y = 0; y < getHeight(); y++){
                char valor= getPixel(x, y);
                if(valor == 255) {
                    char C=1;
                    salida.setPixel(x, y, C);
                }
                else if(valor == 1){
                    char C=255;
                    salida.setPixel(x, y, C);
                }
                else if(valor == 0){
                    char C=0;
                    salida.setPixel(x, y, C);
                }
            }
        }
        return salida;
    }

    /**
     * Algoritmo usado para preparar la FingerPrintImage para ser guardada. 
     * Para hacerlo la convertimos a BufferedImage y usamos la librería ImageIO.
     */
   public BufferedImage toImage(String nombre){
    try {
        BufferedImage image=new BufferedImage(Width,Height,BufferedImage.TYPE_INT_RGB);
        for(int i=0; i<Width; i++) {
            for(int j=0; j<Height; j++) {
                int a = imagen[i][j];
                Color newColor = new Color(a,a,a);
                image.setRGB(i,j,newColor.getRGB());
            }
        }
        File output = new File(nombre);
        ImageIO.write(image, "jpg", output);
        return image;
    }

    catch(Exception e) {
        return null;
    }
   }
}   