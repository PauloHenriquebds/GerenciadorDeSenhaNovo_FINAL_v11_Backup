
package com.securepassmanager.security;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class PasswordUtils {

    public static boolean isStrongPassword(String password) {
        if (password.length() < 8) return false;

        Pattern upperCase = Pattern.compile("[A-Z]");
        Pattern lowerCase = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern specialChar = Pattern.compile("[^a-zA-Z0-9]");

        return upperCase.matcher(password).find()
            && lowerCase.matcher(password).find()
            && digit.matcher(password).find()
            && specialChar.matcher(password).find();
    }

    public static boolean isPasswordPwned(String password) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = sha1.digest(password.getBytes("UTF-8"));
            StringBuilder hash = new StringBuilder();

            for (byte b : hashBytes) {
                hash.append(String.format("%02X", b));
            }

            String fullHash = hash.toString();
            String prefix = fullHash.substring(0, 5);
            String suffix = fullHash.substring(5);

            URL url = new URL("https://api.pwnedpasswords.com/range/" + prefix);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(suffix)) {
                    return true; // Senha vazada
                }
            }
            reader.close();
            return false;

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException("Erro ao calcular SHA-1 da senha", e);
        } catch (IOException e) {
            System.out.println("Não foi possível verificar vazamento de senha (sem conexão). Prosseguindo...");
            return false;
        }
    }



    public static String generateStrongPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()-_=+[]{}|;:',.<>?";
        String allChars = upper + lower + digits + symbols;

        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();

        // Garantir pelo menos um de cada
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(symbols.charAt(random.nextInt(symbols.length())));

        // Preencher até 12 caracteres
        for (int i = 4; i < 12; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Embaralhar
        return new String(password.chars()
            .mapToObj(c -> (char) c)
            .collect(java.util.stream.Collectors.collectingAndThen(
                java.util.stream.Collectors.toList(),
                collected -> {
                    java.util.Collections.shuffle(collected);
                    StringBuilder result = new StringBuilder();
                    collected.forEach(result::append);
                    return result.toString();
                }
            )));
    }
}
