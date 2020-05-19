package Interop.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WriteInFile {
    private boolean append = false;
    private File filepath;

    public WriteInFile(File file_path, boolean Append){
        append = Append;
        filepath = file_path;
    }
    public void WriteToFile(String Text) throws IOException {
        FileWriter Write = new FileWriter(filepath, append);
        PrintWriter printwriter = new PrintWriter(Write);
        printwriter.printf(Text + " ");
        printwriter.close();

    }
}
