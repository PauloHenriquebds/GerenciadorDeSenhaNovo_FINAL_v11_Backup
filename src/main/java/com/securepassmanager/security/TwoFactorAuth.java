package com.securepassmanager.security;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import org.apache.commons.codec.binary.Base32;
import javax.imageio.ImageIO;

public class TwoFactorAuth {

    
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20]; // 160 bits recomendado
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes).replace("=", "").replaceAll(" ", "");
    }


    
    public static String getTOTPCode(String secretKey) {
        try {
            Base32 base32 = new Base32();
            byte[] key = base32.decode(secretKey.replace("=", "").replaceAll(" ", ""));

            long timeIndex = System.currentTimeMillis() / 1000L / 30L;

            byte[] data = new byte[8];
            for (int i = 7; i >= 0; i--) {
                data[i] = (byte) (timeIndex & 0xFF);
                timeIndex >>= 8;
            }

            SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(signKey);
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xF;
            int binary =
                ((hash[offset] & 0x7F) << 24) |
                ((hash[offset + 1] & 0xFF) << 16) |
                ((hash[offset + 2] & 0xFF) << 8) |
                (hash[offset + 3] & 0xFF);

            int otp = binary % (int) Math.pow(10, 6); // 6 dígitos
            return String.format("%06d", otp);

        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar código TOTP", e);
        }
    }


    
    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            String normalizedBase32Key = secretKey.replace("=", "").replaceAll(" ", "");
            String otpAuthURL = String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA512&digits=6&period=30",
                URLEncoder.encode(issuer, "UTF-8"),
                URLEncoder.encode(account, "UTF-8"),
                normalizedBase32Key,
                URLEncoder.encode(issuer, "UTF-8")
            );
            return otpAuthURL;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
    

    public static byte[] generateQRCode(String barCodeData) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(barCodeData, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    public static void showQRCodeInWindow(String barCodeData) throws Exception {
        byte[] qrCodeBytes = generateQRCode(barCodeData);
        BufferedImage qrImage = ImageIO.read(new ByteArrayInputStream(qrCodeBytes));
        JLabel picLabel = new JLabel(new ImageIcon(qrImage));
        JOptionPane.showMessageDialog(null, picLabel, "Escaneie com Google Authenticator", JOptionPane.PLAIN_MESSAGE);
    }
}