package com.securepassmanager.service;

import com.securepassmanager.model.PasswordEntry;
import com.securepassmanager.security.CryptoService;

import java.io.*;
import java.util.*;

public class PasswordManager {
    private static final String FILE_NAME = "vault.txt";
    private List<PasswordEntry> entries = new ArrayList<>();

    public void loadPasswords(String key) throws Exception {
        entries.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            if (parts.length == 3) {
                String service = parts[0];
                String username = parts[1];
                String encrypted = parts[2];
                entries.add(new PasswordEntry(service, username, encrypted));
            }
        }
        reader.close();
    }

    public void savePasswords() throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
        for (PasswordEntry e : entries) {
            writer.write(e.getService() + ";" + e.getUsername() + ";" + e.getEncryptedPassword());
            writer.newLine();
        }
        writer.close();
    }

    public void addEntry(String service, String username, String password, String key) throws Exception {
        String encrypted = CryptoService.encrypt(password, key);
        entries.add(new PasswordEntry(service, username, encrypted));
        savePasswords();
    }

    public List<PasswordEntry> getEntries() {
        return entries;
    }
}