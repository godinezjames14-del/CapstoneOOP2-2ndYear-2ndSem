package Serialization;

import java.io.*;

public class FileHandler {
    public static <T> void serialize(T data, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}