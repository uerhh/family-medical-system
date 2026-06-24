package com.familymedical.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class CaptchaUtil {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LEN = 4;
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final Random RANDOM = new Random();

    public static String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LEN; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    public static String generateBase64Image(String code) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Background
        g.setColor(getRandomColor(220, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Interference lines
        for (int i = 0; i < 6; i++) {
            g.setColor(getRandomColor(160, 200));
            int x1 = RANDOM.nextInt(WIDTH), y1 = RANDOM.nextInt(HEIGHT);
            int x2 = RANDOM.nextInt(WIDTH), y2 = RANDOM.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // Draw code characters
        Font font = new Font("Arial", Font.BOLD, 28);
        g.setFont(font);
        for (int i = 0; i < code.length(); i++) {
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(RANDOM.nextInt(30) - 15), 0, 0);
            g.setTransform(transform);
            g.setColor(getRandomColor(20, 130));
            g.drawString(String.valueOf(code.charAt(i)), 28 * i + 8, 30);
            g.setTransform(new AffineTransform());
        }

        // Interference dots
        for (int i = 0; i < 30; i++) {
            g.setColor(getRandomColor(100, 200));
            g.fillOval(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT), 3, 3);
        }

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private static Color getRandomColor(int min, int max) {
        int r = min + RANDOM.nextInt(max - min);
        int g = min + RANDOM.nextInt(max - min);
        int b = min + RANDOM.nextInt(max - min);
        return new Color(r, g, b);
    }
}
