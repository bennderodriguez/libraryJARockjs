/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockjs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class openrockjs {

    private ProcessBuilder pb;
    private String aux;
    private String resulset;
    private String progressExe;
    private String WorckSpace;
    private String pfdata;
    private String SO;
    private String intError;
    private String path;

    public openrockjs() throws IOException {
        this.resulset = "";

        //Ubico el propertiesPath donde se encuentra el JAR RockJS
        File jarPath = new File(openrockjs.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String propertiesPath = jarPath.getParentFile().getAbsolutePath();
        //Seteo el aboslute JAR PATH
        this.path = propertiesPath;
        //Cargo el arcivo de confiuración
        Properties config = new Properties();
        try {
            System.out.println("Your confutation file must be in  propertiesPath->" + propertiesPath);
            config.load(new FileInputStream(propertiesPath + "/RockJSConf.properties"));
            //iniciamos el path de RockJS
            //Evaluamos el SO en el que se esta trabajando
            this.SO = config.getProperty("server");
            String exec;
            switch (getSO().toString()) {
                case "Linux":
                    exec = "/_progres -b";
                    break;
                case "Windows":
                    //C:\PROGRESS\bin\_progres.exe
                    exec = "\\_progres.exe";
                    break;
                default:
                    exec = " Error in Application server operating system value: " + getSO() + " check the configuration file RockJSConf.properties ";
                    setIntError(exec);
                    error();
                    break;
            }
            
            //Seteo DLC, PATHPROG, PRECGI
            setProgressExe(config.getProperty("DLC") + exec);
            setWorckSpace(config.getProperty("PATHPROG"));
            setPfdata(config.getProperty("PROCGI"));

        } catch (IOException ex) {
            this.error(ex);
            setIntError(" The system can not find the RockJSConf.properties file in the path " + getPath());
            Logger.getLogger(openrockjs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean openrockjs(String p, TreeMap<String, String> treeMap) throws IOException {

        List<String> CMD = new ArrayList<>();
        CMD.add(getProgressExe());
        CMD.add("-pf");
        CMD.add(getPfdata());
        CMD.add("-p");
        CMD.add(getWorckSpace() + "\\" + p + ".p");

        pb = new ProcessBuilder(CMD);

        //this.pb.environment().put(key, value);
        // Imprimimos el Map con un Iterador que ya hemos instanciado anteriormente
        Iterator<String> it = treeMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            System.out.println("Clave: " + key + " -> Valor: " + treeMap.get(key));
            pb.environment().put(key.toString(), treeMap.get(key).toString());
        }

//        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            System.out.println("----environment-----");
//            System.out.println("key " + key + " value " + value);
//            //Proceso de subida a memoria httpKV
//            pb.environment().put(key, value);
//        }
        Process popen = pb.start();
        //Obj p se añade a inputstream para su ectura
        InputStream is = popen.getInputStream();
        //is se agrega a un buffer para leer la salida
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        //Se lee la primera linea
        aux = br.readLine();
        //mientras haya lineas en Salida

        while (aux != null) {

            if (aux.contains("**")) {
                setIntError("Progress internal error " + aux);
                error();
                return false;
            } else {
                // Se escribe la linea en pantalla 
                this.resulset += aux;
            }
            // y se lee la siguiente. 
            aux = br.readLine();
        }
        return true;
    }

    private void error() throws IOException {
        String ruta = getPath() + "\\error.log";
        File archivo = new File(ruta);
        BufferedWriter bw;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));

        if (archivo.exists()) {
            bw = new BufferedWriter(new FileWriter(archivo, true));
            bw.write("[ " + dtf.format(now) + " ] " + getIntError() + "\n");
        } else {
            bw = new BufferedWriter(new FileWriter(archivo, true));
            bw.write("[ " + dtf.format(now) + " ] " + getIntError() + "\n");
        }
        bw.close();
    }

    public String getResulset() {
        return resulset;
    }

    private String getProgressExe() {
        return progressExe;
    }

    private void setProgressExe(String progressExe) {
        this.progressExe = progressExe;
    }

    private String getWorckSpace() {
        return WorckSpace;
    }

    private void setWorckSpace(String WorckSpace) {
        this.WorckSpace = WorckSpace;
    }

    private String getPfdata() {
        return pfdata;
    }

    private void setPfdata(String pfdata) {
        this.pfdata = pfdata;
    }

    private String getSO() {
        return SO;
    }

    public String getIntError() {
        return intError;
    }

    private void setIntError(String intError) {
        this.intError += intError;
    }

    public String getPath() {
        return path;
    }

    /**
     * Muestra en error.log las excepciones ex de try cath
     * @param ex
     * @throws IOException 
     */
    private void error(IOException ex) throws IOException {
        String ruta = getPath() + "\\error.log";
        File archivo = new File(ruta);
        BufferedWriter bw;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        if (archivo.exists()) {
            bw = new BufferedWriter(new FileWriter(archivo, true));
            bw.write("[ " + dtf.format(now) + " ] " + ex.toString() + "\n");
        } else {
            bw = new BufferedWriter(new FileWriter(archivo, true));
            bw.write("[ " + dtf.format(now) + " ] " + ex.toString() + "\n");
        }
        bw.close();
    }

}
