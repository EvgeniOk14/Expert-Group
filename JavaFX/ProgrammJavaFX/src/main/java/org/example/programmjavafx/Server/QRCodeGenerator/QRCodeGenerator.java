package org.example.programmjavafx.Server.QRCodeGenerator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * предназначен для генерации изображения QR-кода и его сохранения в файл.
 * **/
public class QRCodeGenerator
{
    // String text: Текст, который нужно закодировать в QR-код, int width: Ширина изображения QR-кода, int height: Высота изображения QR-кода, String filePath: Путь к файлу, в который будет сохранен QR-код
    public static void generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException
    {
        QRCodeWriter qrCodeWriter = new QRCodeWriter(); // Класс для генерации QR-кодов
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height); // BitMatrix - Класс для представления двумерного массива битов, используемого для хранения данных QR-кода, BarcodeFormat: Перечисление, содержащее различные форматы штрихкодов, включая QR-код.

        Path path = FileSystems.getDefault().getPath(filePath); // путь по которому необходимо сохранить QR-code
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);// MatrixToImageWriter - класс для преобразования BitMatrix в изображение и сохранения его в файл
    }
}

