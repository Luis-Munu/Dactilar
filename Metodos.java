import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.*;
/**
 * Clase usada para realizar algunos metodos más complejos o que requieren de operar con FingerPrintImages.
 */
public class Metodos {

    /**
     * Conversion de RGB a matriz de grises, utiliza una máscara para transformar los pixeles.
     */
    public FingerPrintImage RGBtoMatrizGrises(BufferedImage imagenRGB){
        FingerPrintImage matrizGrises=new FingerPrintImage(imagenRGB.getHeight(), imagenRGB.getWidth());
        for(int x=0; x<imagenRGB.getWidth();x++){
            for (int y = 0; y < imagenRGB.getHeight(); ++y){
                int rgb = imagenRGB.getRGB(x, y);
                char r = (char)((rgb >> 16) & 0xFF);
                char g = (char)((rgb >> 8) & 0xFF);
                char b = (char)((rgb & 0xFF));
                char nivelGris = (char)((r + g + b) / 3);
                matrizGrises.setPixel(x, y, nivelGris);
            }
        }
        return matrizGrises;
    }

    /**
     * Conversion de matris de grises a RGB, la conversión no es perfecta debido a que el rango de valores de la matris de grises es
     * inferior al de la RGB. Hemos intentado varios filtros y este ha sido el que mejor ha resultado.
     */
    public BufferedImage ImagenGrisestoRGB(FingerPrintImage matrizGrises, boolean modo){
        BufferedImage matrizRGB=new BufferedImage(matrizGrises.getWidth(),matrizGrises.getHeight(),BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < matrizGrises.getWidth(); ++x){
            for (int y = 0; y < matrizGrises.getHeight(); ++y){
                int valor = matrizGrises.getPixel(x, y);
                if(modo==false) {
                    valor=valor*255;
                }
                int pixelRGB=(255<<24 | valor << 16 | valor << 8 | valor);
                matrizRGB.setRGB(x, y,pixelRGB);
            }
        }
        return matrizRGB;
    }

    /**
     * Realiza el histograma de la imagen, para ello toma la frecuencia de los diferentes valores de los pixeles y construye
     * una tabla de valores, con esos valores se crea una nueva imagen ecualizada.
     */
    public FingerPrintImage histograma(FingerPrintImage imagenentrada){
        FingerPrintImage imagenecualizada=new FingerPrintImage(imagenentrada.getHeight(), imagenentrada.getWidth());
        int width =imagenentrada.getWidth();
        int height =imagenentrada.getHeight();
        int tampixel= width*height;
        int[] histograma= new int[256];
        int i =0;
        //Recogida de frecuencia de los valores.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int valor=imagenentrada.getPixel(x, y);
                histograma[valor]++;
            }
        }

        int sum =0;
        float[] lut = new float[256];

        //Creacion de los valores a partir de la frecuencia.
        for ( i=0; i < 256; ++i ){
            sum += histograma[i];
            lut[i] = sum * 255 / tampixel;
        }
        i=0;

        //Crea una imagen a partir de la tabla y de la imagen anterior.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int valor=imagenentrada.getPixel(x, y);
                char valorNuevo= (char)lut[valor];
                imagenecualizada.setPixel(x, y, valorNuevo);
                i=i+1;
            }
        }
        return imagenecualizada;
    }
    
    /**
     * Transforma una imagen de grises a blanco y negro.
     * A partir de cierto umbral los pixeles pasan a ser blancos o negros.
     * Este umbral puede depender de la imagen en cuestion, algunas son más oscuras o sucias que otras, es por esto
     * que decidimos pedirle al usuario que introduzca el valor del umbral.
     */
    public FingerPrintImage imagenGrisToByN(FingerPrintImage imagenentrada, char umbral){
        FingerPrintImage imagensalida=new FingerPrintImage(imagenentrada.getHeight(), imagenentrada.getWidth());
        for (int x = 0; x < imagenentrada.getWidth(); x++){
            for (int y = 0; y < imagenentrada.getHeight(); y++){
                char valor = imagenentrada.getPixel(x, y);
                if(valor < umbral) {
                    char C=0;
                    imagensalida.setPixel(x, y, C);
                } 
                else {
                    char C=255;
                    imagensalida.setPixel(x, y, C);
                } 
            
            }
        }
        return imagensalida;
    }

    /**
     * Aplicación del filtro 1 en todos los pixeles de una imagen.
     */
    public FingerPrintImage filtrado1(FingerPrintImage imagenentrada){
        FingerPrintImage imagensalida=new FingerPrintImage(imagenentrada.getHeight(), imagenentrada.getWidth());
        int width = imagenentrada.getWidth();
        int height = imagenentrada.getHeight();
        for (int x = 1; x < width-1; x++) {
            for (int y = 1; y < height-1; y++) {
                imagensalida.setPixel(x, y, imagenentrada.filtro1(x, y));
            }
        }
        return imagensalida;
    }

    /**
     * Aplicación del filtro 2 en todos los pixeles de una imagen.
     */
    public FingerPrintImage filtrado2(FingerPrintImage imagenentrada){
        FingerPrintImage imagensalida=new FingerPrintImage(imagenentrada.getHeight(), imagenentrada.getWidth());
        int width = imagenentrada.getWidth();
        int height = imagenentrada.getHeight();
        for (int x = 1; x < width-1; x++) {
            for (int y = 1; y < height-1; y++) {
                imagensalida.setPixel(x, y, imagenentrada.filtro2(x, y));
            }
        }
        return imagensalida;
    }

    /**
     * Algoritmo de deteccion de minucias.
     * Pasa el metodo detectarMinucias por todos los pixeles de la imagen y si su valor es 1 o 3 pinta un rectángulo
     * alrededor de él.
     */
    public BufferedImage minucias(FingerPrintImage imagenentrada){
        FingerPrintImage clon = imagenentrada.copyF();
        clon=clon.invertir();
        BufferedImage imagenMinutias=clon.copy();
        Graphics2D g2d = imagenMinutias.createGraphics();
        g2d.setColor(Color.GREEN);
        for(int i=0;i<imagenentrada.getWidth()-1;i++){
            for(int j=0;j<imagenentrada.getHeight()-1;j++){
                if(imagenentrada.getPixel(i, j)==1){
                    if(imagenentrada.detectarMinucias(i, j)==true){
                        g2d.drawRect(i-10, j-10, 20, 20);
                    }
            }

            }
        }
        g2d.dispose();
        return imagenMinutias;
    }

    /**
     * Llamada y comprobacion de las dos iteraciones de Zhang-Suen. Itera hasta que detecta que no hay ningún cambio por hacer.
     */
    public FingerPrintImage zhangsuen(FingerPrintImage entrada){
        boolean cambio1,cambio2;
        do{
            cambio1=entrada.Iteration(0);
            cambio2=entrada.Iteration(1);
        }while(cambio1 == true || cambio2==true);
        return entrada;
    }

    /**
     * Algoritmo que invierte una imagen ByN. Sin distinciones especiales.
     */
    public FingerPrintImage invertir(FingerPrintImage entrada){
        FingerPrintImage salida=new FingerPrintImage(entrada.getHeight(), entrada.getWidth());
        for (int x = 0; x < entrada.getWidth(); x++){
            for (int y = 0; y < entrada.getHeight(); y++){
                char valor= entrada.getPixel(x, y);
                if(valor == 255) {
                    char C=0;
                    salida.setPixel(x, y, C);
                } 
                else {
                    char C=255;
                    salida.setPixel(x, y, C);
                }
            
            }
        }
        return salida;
    }
}