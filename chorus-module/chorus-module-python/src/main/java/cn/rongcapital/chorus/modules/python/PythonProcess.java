package cn.rongcapital.chorus.modules.python;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Created by abiton on 08/06/2017.
 */
@Slf4j
public class PythonProcess {
    private static final String STATEMENT_END = "*!?flush reader!?*";
    InputStream stdout;
    OutputStream stdin;
    PrintWriter writer;
    BufferedReader reader;
    Process process;


    public void open() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("bash","-c","python -iu");
        builder.redirectErrorStream(true);
        process = builder.start();
        stdout = process.getInputStream();
        stdin = process.getOutputStream();
        writer = new PrintWriter(stdin,true);
        reader = new BufferedReader(new InputStreamReader(stdout));

    }

    public void close() throws IOException {
        process.destroy();
        reader.close();
        writer.close();
        stdin.close();
        stdout.close();
    }


    public String sendAndGetResult(String cmd) throws IOException {
        writer.println(cmd);
        writer.println();
        writer.println("\"" + STATEMENT_END + "\"");
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null &&
                !line.contains(STATEMENT_END)) {
            log.info("Read line from python shell : " + line);
            output.append(line + "\n");
        }
        return output.toString();
    }
}
