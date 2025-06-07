package com.securepassmanager;

import com.securepassmanager.model.User;
import com.securepassmanager.service.PasswordManager;
import com.securepassmanager.util.PasswordGenerator;
import com.securepassmanager.util.LeakChecker;
import com.securepassmanager.security.TwoFactorAuth;

import java.util.Scanner;
import com.securepassmanager.security.PasswordUtils;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite seu nome de usuário:");
        String username = scanner.nextLine();

        String secretKey = TwoFactorAuth.generateSecretKey();
        String barCodeData = TwoFactorAuth.getGoogleAuthenticatorBarCode(secretKey, username, "SecurePassManager");
        TwoFactorAuth.showQRCodeInWindow(barCodeData);

        System.out.println("Digite o código do Google Authenticator:");
        String code = scanner.nextLine();

        if (!code.equals(TwoFactorAuth.getTOTPCode(secretKey))) {
            System.out.println("Código 2FA inválido!");
            return;
        }

        System.out.println("Autenticado com sucesso!");

        User user = new User(username, secretKey);
        PasswordManager manager = new PasswordManager();
        manager.loadPasswords(secretKey);

        while (true) {
            System.out.println("\n1. Adicionar senha\n2. Listar senhas\n3. Sugerir senha\n4. Verificar se uma senha foi vazada\n5. Sair");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    System.out.print("Serviço: ");
                    String service = scanner.nextLine();
                    System.out.print("Usuário: ");
                    String u = scanner.nextLine();
                    System.out.print("Senha: ");
                    String p = scanner.nextLine();

                    if (LeakChecker.isPasswordLeaked(p)) {
                        System.out.println("⚠️ Esta senha já apareceu em vazamentos!");
                        System.err.println("\nDeseja continuar com esta senha?: (S/N)");
                        String armazenar = scanner.nextLine().toUpperCase();
                        
                        if (armazenar.equals("S")) {
                            manager.addEntry(service, u, p, secretKey);
                        } else {
                            System.err.println("Encerrando o salvamento de senha...");
                            break;
                        }
                    }

                    manager.addEntry(service, u, p, secretKey);
                    break;
                case "2":
                    for (var entry : manager.getEntries()) {
                        System.out.println("Serviço: " + entry.getService() + ", Usuário: " + entry.getUsername());
                    }
                    break;
                case "3":
                System.out.println("Senha sugerida: " + PasswordGenerator.generateSecurePassword());
                    break;

                case "4":
                    System.out.print("Digite a senha que você deseja verificar: ");
                    String senhaParaVerificar = scanner.nextLine();
                    
                    if (LeakChecker.isPasswordLeaked(senhaParaVerificar)) {
                        System.out.println("⚠️ Esta senha já apareceu em vazamentos!");
                    } 
                    else {
                        System.out.println("✅ Esta senha NÃO foi encontrada em vazamentos conhecidos.");
                    }
                    break;

                    
                case "5":
                    return;
            }
        }
    }
}