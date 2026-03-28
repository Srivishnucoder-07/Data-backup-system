import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.*;

// ================== DATA ITEM ==================
class DataItem implements Serializable {
    String key;
    Object value;
    String type;

    public DataItem(String key, Object value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public String toString() {
        return key + " = " + value + " (" + type + ")";
    }
}

// ================== BACKUP DATA ==================
class BackupData implements Serializable {
    UUID id;
    LocalDateTime time;
    String description;
    List<DataItem> items;

    public BackupData(String description) {
        this.id = UUID.randomUUID();
        this.time = LocalDateTime.now();
        this.description = description;
        this.items = new ArrayList<>();
    }

    public void addItem(DataItem item) {
        items.add(item);
    }
}

// ================== MAIN SYSTEM ==================
public class BackupSystem {

    static final String DIR = "backups/";

    public static void main(String[] args) throws Exception {
        new File(DIR).mkdir();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== DATA BACKUP SYSTEM ===");
            System.out.println("1. Create New Backup");
            System.out.println("2. Restore from Binary");
            System.out.println("3. Restore from Compressed");
            System.out.println("4. Import from CSV");
            System.out.println("5. List All Versions");
            System.out.println("6. Cleanup Old Backups");
            System.out.println("7. Backup Statistics");
            System.out.println("8. Compare Files");
            System.out.println("9. Schedule Backup (Demo)");
            System.out.println("10. Exit");

            int choice = sc.nextInt();

            switch (choice) {

                case 1 -> createBackup(sc);

                case 2 -> {
                    System.out.print("Enter file path: ");
                    String path = sc.next();
                    BackupData data = loadBinary(path);
                    display(data);
                }

                case 3 -> {
                    System.out.print("Enter .gz file: ");
                    String gz = sc.next();
                    String out = "temp.dat";
                    decompress(gz, out);
                    BackupData data = loadBinary(out);
                    display(data);
                }

                case 4 -> {
                    System.out.print("CSV path: ");
                    importCSV(sc.next());
                }

                case 5 -> listVersions();

                case 6 -> {
                    System.out.print("Days to keep: ");
                    cleanup(sc.nextInt());
                }

                case 7 -> stats();

                case 8 -> {
                    System.out.print("File1: ");
                    String f1 = sc.next();
                    System.out.print("File2: ");
                    String f2 = sc.next();
                    compare(f1, f2);
                }

                case 9 -> scheduleDemo();

                case 10 -> System.exit(0);
            }
        }
    }

    // ================== CREATE BACKUP ==================
    static void createBackup(Scanner sc) throws Exception {

        sc.nextLine();
        System.out.print("Description: ");
        String desc = sc.nextLine();

        BackupData data = new BackupData(desc);

        while (true) {
            System.out.print("Key: ");
            String key = sc.next();

            System.out.print("Value: ");
            String value = sc.next();

            System.out.print("Type: ");
            String type = sc.next();

            Object val = switch (type) {
                case "Integer" -> Integer.parseInt(value);
                case "Double" -> Double.parseDouble(value);
                case "Boolean" -> Boolean.parseBoolean(value);
                default -> value;
            };

            data.addItem(new DataItem(key, val, type));

            System.out.print("Add more (y/n): ");
            if (sc.next().equalsIgnoreCase("n")) break;
        }

        String time = data.time.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String base = DIR + "backup_" + time;

        saveBinary(data, base + ".dat");
        compress(base + ".dat", base + ".dat.gz");
        exportCSV(data, base + ".csv");

        System.out.println("✅ Backup Created!");
        System.out.println("ID: " + data.id);
    }

    // ================== SERIALIZATION ==================
    static void saveBinary(BackupData data, String path) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
        oos.writeObject(data);
        oos.close();
    }

    static BackupData loadBinary(String path) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
        BackupData data = (BackupData) ois.readObject();
        ois.close();
        return data;
    }

    // ================== COMPRESSION ==================
    static void compress(String input, String output) throws Exception {
        GZIPOutputStream g = new GZIPOutputStream(new FileOutputStream(output));
        FileInputStream fis = new FileInputStream(input);

        byte[] buf = new byte[1024];
        int len;
        while ((len = fis.read(buf)) > 0) g.write(buf, 0, len);

        fis.close();
        g.close();
    }

    static void decompress(String input, String output) throws Exception {
        GZIPInputStream g = new GZIPInputStream(new FileInputStream(input));
        FileOutputStream fos = new FileOutputStream(output);

        byte[] buf = new byte[1024];
        int len;
        while ((len = g.read(buf)) > 0) fos.write(buf, 0, len);

        g.close();
        fos.close();
    }

    // ================== CSV ==================
    static void exportCSV(BackupData data, String file) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        bw.write("key,value,type\n");

        for (DataItem i : data.items) {
            bw.write(i.key + "," + i.value + "," + i.type + "\n");
        }

        bw.close();
    }

    static void importCSV(String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        System.out.println("CSV Data:");

        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        br.close();
    }

    // ================== DISPLAY ==================
    static void display(BackupData d) {
        System.out.println("\n✅ RESTORED BACKUP:");
        System.out.println("ID: " + d.id);
        System.out.println("Time: " + d.time);
        System.out.println("Desc: " + d.description);

        int i = 1;
        for (DataItem item : d.items) {
            System.out.println(i++ + ". " + item);
        }
    }

    // ================== VERSION MANAGEMENT ==================
    static void listVersions() {
        File dir = new File(DIR);

        File[] files = dir.listFiles((d, n) -> n.endsWith(".dat"));

        Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        int i = 1;
        for (File f : files) {
            System.out.println("Version " + i++ + ": " + f.getName());
        }
    }

    // ================== CLEANUP ==================
    static void cleanup(int days) {
        File dir = new File(DIR);
        long now = System.currentTimeMillis();

        for (File f : dir.listFiles()) {
            long diff = now - f.lastModified();

            if (diff > days * 86400000L) {
                f.delete();
                System.out.println("Deleted: " + f.getName());
            }
        }
    }

    // ================== STATS ==================
    static void stats() {
        File dir = new File(DIR);

        long total = 0;
        int count = 0;

        for (File f : dir.listFiles()) {
            total += f.length();
            count++;
        }

        System.out.println("Total Files: " + count);
        System.out.println("Total Size: " + (total / 1024.0 / 1024.0) + " MB");
    }

    // ================== FILE COMPARE ==================
    static void compare(String f1, String f2) throws Exception {
        List<String> a = read(f1);
        List<String> b = read(f2);

        for (String s : a)
            if (!b.contains(s))
                System.out.println("Only in File1: " + s);

        for (String s : b)
            if (!a.contains(s))
                System.out.println("Only in File2: " + s);
    }

    static List<String> read(String f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        List<String> list = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null)
            list.add(line);

        br.close();
        return list;
    }

    // ================== SCHEDULER ==================
    static void scheduleDemo() {
        Timer t = new Timer();

        t.schedule(new TimerTask() {
            public void run() {
                System.out.println("⏰ Scheduled Backup Triggered!");
            }
        }, 5000); // 5 sec delay

        System.out.println("Backup scheduled in 5 seconds...");
    }
}
