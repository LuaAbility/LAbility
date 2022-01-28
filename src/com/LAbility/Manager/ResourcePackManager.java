package com.LAbility.Manager;

import com.LAbility.LAbilityMain;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ResourcePackManager {
    private final byte[] BUFFER = new byte[1024 * 1024 * 32];

    public void copy(InputStream input, OutputStream output) throws IOException {
        int bytesRead;

        while ((bytesRead = input.read(BUFFER))!= -1) output.write(BUFFER, 0, bytesRead);
    }

    public void patch(String[] path) throws IOException, ParseException {
        ArrayList<ZipFile> zipList = new ArrayList<>();
        for (String p : path) zipList.add(new ZipFile(p));

        ZipOutputStream moddedZip = new ZipOutputStream(new FileOutputStream(LAbilityMain.instance.getDataFolder().getPath() + File.separator + "LAbilityResource.zip"));

        Set<String> names = new HashSet<>();
        JSONObject soundJson = new JSONObject();
        ZipEntry jsonEntry = null;
        for (ZipFile file : zipList) {
            Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                if (e.getName().contains("assets/minecraft/sounds.json")){
                    String data = new String(file.getInputStream(e).readAllBytes(), StandardCharsets.UTF_8);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(data);
                    soundJson.putAll(jsonObject);
                    if (jsonEntry == null) jsonEntry = e;
                }
                else if (names.add(e.getName())) {
                    moddedZip.putNextEntry(e);
                    if (!e.isDirectory()) copy(file.getInputStream(e), moddedZip);
                    moddedZip.closeEntry();
                }
            }
        }

        if (soundJson.size() > 0) {
            moddedZip.putNextEntry(jsonEntry);
            InputStream input = new ByteArrayInputStream(soundJson.toJSONString().getBytes(StandardCharsets.UTF_8));
            copy(input, moddedZip);
            moddedZip.closeEntry();
        }
        for (ZipFile file : zipList) file.close();
        moddedZip.close();

        for (String p : path) {
            File file = new File(p);
            if (file.exists()) file.delete();
        }
    }

    public String[] downloadResourcePack(String[] fileURL){
        ArrayList<String> zipURLList = new ArrayList<>();
        int fileCount = 1;

        for (String p : fileURL) {
            String output = LAbilityMain.instance.getDataFolder().toString() + "\\" + fileCount + ".zip";
            File file = new File(output);
            if (file.exists()) file.delete();

            try(InputStream in = new URL(p).openStream()) {
                Path imagePath = Paths.get(output);
                Files.copy(in, imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileCount++;
            zipURLList.add(output);
        }

        String[] urlList = new String[zipURLList.size()];
        zipURLList.toArray(urlList);

        return urlList;
    }
}
