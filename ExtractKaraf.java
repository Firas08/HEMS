import java.io.*;
import java.util.zip.*;

public class ExtractKaraf {
    public static void main(String[] args) throws Exception {
        String zipFile = "karaf.zip";
        String destDir = ".";
        
        System.out.println("Extraction de " + zipFile + "...");
        
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        
        while (zipEntry != null) {
            File newFile = new File(destDir + File.separator + zipEntry.getName());
            
            if (zipEntry.isDirectory()) {
                newFile.mkdirs();
            } else {
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        
        zis.closeEntry();
        zis.close();
        
        System.out.println("Extraction terminee!");
        
        // Renommer
        File oldDir = new File("apache-karaf-4.4.6");
        File newDir = new File("karaf");
        if (newDir.exists()) {
            deleteDirectory(newDir);
        }
        oldDir.renameTo(newDir);
        
        System.out.println("Karaf pret!");
    }
    
    static void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
}
