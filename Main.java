import java.awt.image.BufferedImage;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        procedimiento();
    }

    // Algoritmo principal que realiza todas las llamadas del programa.
    public static void procedimiento() {

        try {

            // Inicialización de algunas variables.
            // El vector imagenes es usado más tarde para poder mostrar las imagenes en el
            // JFrame.
            ArrayList<BufferedImage> imagenes = new ArrayList<>();
            String nombres[] = { "Original", "Gris", "RGBScale", "Histograma", "Blanco Y Negro", "Filtrado",
                    "Zhang-Suen", "Minucias" };
            Metodos m = new Metodos();
            Console c = System.console();

            // Pedimos la imagen al usuario y la leemos
            String response = c.readLine("Introduzca el nombre de la imagen: ");
            BufferedImage image = ImageIO.read(new File("imagenes/" + response));
            imagenes.add(image);

            System.out.print("Transformando la imagen a escala de grises...");
            FingerPrintImage gris = m.RGBtoMatrizGrises(image);
            imagenes.add(gris.toImage("resultado/" + nombres[1] + ".jpg"));
            System.out.println(" imagen a escala de grises lista.\n");

            System.out.print("Transformando la imagen a una matriz RGB...");
            File output = new File("resultado/" + nombres[2] + ".jpg");
            ImageIO.write(m.ImagenGrisestoRGB(gris, true), "jpg", output);
            imagenes.add(ImageIO.read(new File("resultado/" + nombres[2] + ".jpg")));
            System.out.println(" matriz RGB lista.\n");

            System.out.print("Realizando el histograma de la imagen...");
            FingerPrintImage his = m.histograma(gris);
            imagenes.add(his.toImage("resultado/" + nombres[3] + ".jpg"));
            System.out.println(" histograma listo.\n");

            int umbral = Integer
                    .parseInt(c.readLine("Introduzca el umbral para binarizar la imagen (debe estar entre 0 y 255)\n"));
            System.out.print("Binarizando la imagen...");

            FingerPrintImage byn = m.imagenGrisToByN(gris, (char) umbral);
            imagenes.add(byn.toImage("resultado/" + nombres[4] + ".jpg"));
            System.out.println(" binarizado listo.\n");

            System.out.print("Aplicando filtros...");
            FingerPrintImage fil = m.filtrado1(byn);
            fil = m.filtrado2(fil);
            imagenes.add(fil.toImage("resultado/" + nombres[5] + ".jpg"));
            System.out.println(" filtrado listo.\n");

            FingerPrintImage inv = m.invertir(fil);

            System.out.print("Adelgazando la imagen...");
            FingerPrintImage adelgazada = m.zhangsuen(inv);
            imagenes.add(adelgazada.toImage("resultado/" + nombres[6] + ".jpg"));
            System.out.println(" imagen adelgazada lista.\n");

            FingerPrintImage minucias = adelgazada.invertir();

            System.out.print("Detectando minucias...");
            File output2 = new File("resultado/" + nombres[7] + ".jpg");
            ImageIO.write(m.minucias(minucias), "jpg", output2);
            imagenes.add(ImageIO.read(new File("resultado/" + nombres[7] + ".jpg")));
            System.out.println(" minucias detectadas.");

            new MyFrame(imagenes, nombres);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}